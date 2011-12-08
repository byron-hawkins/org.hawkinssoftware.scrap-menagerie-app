/*
 * Copyright (c) 2011 HawkinsSoftware
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Byron Hawkins of HawkinsSoftware
 */
package org.hawkinssoftware.ui.util.scraps.data;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContentsMap;
import org.hawkinssoftware.azia.input.clipboard.ClipboardMonitor;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.PaintableActorDelegate;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.clipboard.ClipboardChangeDirective;
import org.hawkinssoftware.azia.ui.component.transaction.clipboard.ClipboardEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListWriteDomain;
import org.hawkinssoftware.azia.ui.model.list.ListDataModelTransaction;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents;
import org.hawkinssoftware.ui.util.scraps.clip.CurrentClip;
import org.hawkinssoftware.ui.util.scraps.history.ScrapMenagerieHistoryList;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@ValidateRead
@ValidateWrite
public class ClipboardHandler implements UserInterfaceHandler, UserInterfaceActorDelegate
{
	public static void install()
	{
		ClipboardEventDispatch.getInstance().installHandler(INSTANCE);
	}

	public static ClipboardHandler getInstance()
	{
		return INSTANCE;
	}

	private static final ClipboardHandler INSTANCE = new ClipboardHandler();

	private static final int MAX_CLIP_COUNT = 100;

	private final PaintableActorDelegate currentClipActor;
	private final ScrapMenagerieHistoryList list;

	private ScrapMenagerieItem currentClip;

	private ClipboardContentsMap<Integer> clipUsageMap = new ClipboardContentsMap<Integer>();

	public ClipboardHandler()
	{
		this.currentClipActor = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CURRENT_CLIP_ASSEMBLY);
		this.list = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CLIP_LIST_ASSEMBLY);

		ClipboardContents currentContents = ClipboardMonitor.getInstance().getCurrentClipboardContents();
		currentClip = new ScrapMenagerieItem(currentContents, CurrentClip.summarize(currentContents));
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return currentClipActor.getActor();
	}

	public void clipboardEvent(ClipboardChangeDirective.Notification notification, PendingTransaction transaction)
	{
		String summary = CurrentClip.summarize(notification.clipboardContents);

		ChangeTextDirective textChange = new ChangeTextDirective(currentClipActor, summary);
		transaction.contribute(textChange);

		RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(currentClipActor.getActor()));

		try
		{
			if (currentClip != null)
			{
				TransactionRegistry.executeTask(new AddScrap(currentClip, notification.clipboardContents));
			}
		}
		catch (ConcurrentAccessException e)
		{
			Log.out(Tag.CRITICAL, e, "Failed to add a scrap to the list");
		}
	}

	public void clipboardEvent(ClipboardChangeDirective change)
	{
		currentClip = new ScrapMenagerieItem(change.clipboardContents, CurrentClip.summarize(change.clipboardContents));
	}

	public void reCopy(ReCopyCommand command)
	{
		ClipboardContents contents = command.clipboardProvider.getClipboardContents();
		ClipboardMonitor.getInstance().putClipboardContents(contents);

		Integer usage = clipUsageMap.get(contents);
		if (usage == null)
		{
			usage = 1;
		}
		else
		{
			usage = usage + 1;
		}
		clipUsageMap.put(contents, usage);
	}

	public void transformScrap(TransformScrapCommand transform)
	{
		ClipboardMonitor.getInstance().putClipboardContents(transform.clipboardProvider.getClipboardContents());
	}

	ClipboardContents getCurrentClip()
	{
		return currentClip.getClipboardContents();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = { RenderingDomain.class, ModelListWriteDomain.class })
	private class AddScrap extends UserInterfaceTask
	{
		private final ScrapMenagerieItem pushItem;
		private final ClipboardContents pendingContents;

		AddScrap(ScrapMenagerieItem pushItem, ClipboardContents pendingContents)
		{
			this.pushItem = pushItem;
			this.pendingContents = pendingContents;
		}

		@Override
		protected boolean execute()
		{
			ListDataModel.Session session = list.getModel().createSession(getTransaction(ListDataModelTransaction.class));

			// remove dups, going in reverse order to avoid index confusion
			boolean removedDuplicate = false;
			int obsoletionCandidate = -1;
			int minUsage = Integer.MAX_VALUE;
			for (int i = list.getModel().getRowCount(Section.SCROLLABLE) - 1; i >= 0; i--)
			{
				RowAddress address = list.getViewport().createAddress(i, Section.SCROLLABLE);
				ScrapMenagerieItem existingItem = (ScrapMenagerieItem) list.getModel().get(address);
				if (existingItem.getClipboardContents().equals(pendingContents))
				{
					session.remove(i);
					removedDuplicate = true;
				}
				else
				{
					Integer itemUsage = clipUsageMap.get(existingItem.getClipboardContents());
					if (itemUsage == null)
					{
						itemUsage = 0;
					}
					if (minUsage > itemUsage)
					{
						minUsage = itemUsage;
						obsoletionCandidate = i;
					}
				}
			}

			// only obsoleting a row when no duplicate was removed, so sequence integrity is safe (horky, but works...)
			if ((obsoletionCandidate >= 0) && (list.getModel().getRowCount(Section.SCROLLABLE) >= MAX_CLIP_COUNT) && !removedDuplicate)
			{
				session.remove(obsoletionCandidate);
			}

			// insert after removals to avoid row index confusion
			session.insert(0, pushItem, Section.SCROLLABLE);

			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(list.getComponent()));

			return true;
		}
	}
}
