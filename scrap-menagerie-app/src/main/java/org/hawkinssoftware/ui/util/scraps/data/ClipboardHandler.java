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
 * Applies user actions to the system clipboard and responds to observed changes on the system clipboard.
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
		/**
		 * @JTourBusStop 1, ClipboardHandler, Component references are obtained without any structural references:
		 * 
		 *               The currentClipActor and list are obtained from the ComponentRegistry by key, so the
		 *               ClipboardHandler needs no information about how that part of the user interface is structured.
		 *               The references are guaranteed to be available by the time this constructor executes, making the
		 *               ClipboardHandler free of dependencies on construction sequence.
		 */
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

		/**
		 * @JTourBusStop 2, ClipboardHandler, Repaint can be requested from any thread:
		 * 
		 *               This repaint request will come from a thread that monitors the system clipboard. Since all
		 *               threads are equal in Azia, the request can be made directly from this thread. The transaction
		 *               engine guarantees that the repaint will be executed after all transaction participants have
		 *               made their data changes, allowing secondary responses to the ChangeTextDirective (above) to be
		 *               free of concern about repainting the modified clip.
		 */
		RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(currentClipActor));

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

	/**
	 * @JTourBusStop 4, ClipboardHandler, Another example of guaranteed data integrity:
	 * 
	 *               When the user presses the Re-Copy button (or the equivalent shortcut key), this ReCopyCommand
	 *               occurs in a transaction on the native input thread. The transaction engine automatically locks the
	 *               ClipboardMonitor's data before executing putClipboardContents(), so that changes to the system
	 *               clipboard during the transaction will pend until this transaction completes. Simultaneous
	 *               transactions do have a risk of deadlock, and in that case the transaction engine chooses one of the
	 *               contenders to terminate and retry. All client code is eligible for retry without implementing
	 *               anything special to support it.
	 */
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
	 * Transactional task to add a scrap to the system clipboard. Searches existing scraps for duplicates and removes
	 * them. If the maximum number of scraps has been exceeded, the least re-copied scrap is removed from the list.
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
			/**
			 * @JTourBusStop 3, ClipboardHandler, Concurrency is handled internally for all shared data:
			 * 
			 *               The list model can be accessed in the same way by any number of threads concurrently, and
			 *               the transaction engine will guarantee data integrity for both reads and writes across the
			 *               entire transaction. Other collaborators in this transaction have an option to see either
			 *               the current data changes, or the original view as it appeared before this transaction
			 *               started.
			 */
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
