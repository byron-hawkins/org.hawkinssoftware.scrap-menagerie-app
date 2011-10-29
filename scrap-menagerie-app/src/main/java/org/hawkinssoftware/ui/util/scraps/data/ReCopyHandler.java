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

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.PaintableActorDelegate;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.clipboard.ClipboardEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangePressedStateDirective;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieKeyCommand;
import org.hawkinssoftware.ui.util.scraps.fragment.ScrapMenagerieFragmentList;
import org.hawkinssoftware.ui.util.scraps.history.ScrapMenagerieHistoryList;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListFocusManager;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

// WIP: what's really the distinction between ModelListDomain and FlyCellDomain?
/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
public class ReCopyHandler implements UserInterfaceHandler
{
	@InvocationConstraint(domains = AssemblyDomain.class)
	public static void install()
	{
		KeyEventDispatch.getInstance().installHandler(INSTANCE);
		ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.SELECT_BUTTON).installHandler(INSTANCE);
	}

	public static ReCopyHandler getInstance()
	{
		return INSTANCE;
	}

	private static final ReCopyHandler INSTANCE = new ReCopyHandler();

	private final PaintableActorDelegate currentClipActor;

	private final ReCopySource clipSource;
	private final ReCopySource fragmentSource;

	public ReCopyHandler()
	{
		ScrapMenagerieHistoryList clipList = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CLIP_LIST_ASSEMBLY);
		clipSource = new ReCopySource(clipList.getViewport(), clipList.getModel());

		ScrapMenagerieFragmentList fragmentList = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.FRAGMENT_LIST_ASSEMBLY);
		fragmentSource = new ReCopySource(fragmentList.getViewport(), fragmentList.getModel());

		currentClipActor = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CURRENT_CLIP_ASSEMBLY);
	}

	private ReCopySource getFocusedReCopySource()
	{
		if (ScrapMenagerieListFocusManager.getInstance().getFocusedList() == clipSource.viewport)
		{
			return clipSource;
		}
		else
		{
			return fragmentSource;
		}
	}

	public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
	{
		if (ScrapMenagerieKeyCommand.getCommand(key.event) == ScrapMenagerieKeyCommand.RE_COPY)
		{
			reCopySelectedRow(transaction);
		}
	}

	public void buttonPressed(ChangePressedStateDirective.Notification button, PendingTransaction transaction)
	{
		if (button.isPressed())
		{
			reCopySelectedRow(transaction);
		}
	}

	private void reCopySelectedRow(PendingTransaction transaction)
	{
		ReCopySource source = getFocusedReCopySource();
		RowAddress address = source.viewport.createAddress(source.viewport.selection.getSelectedRow(), Section.SCROLLABLE);
		if (address.row < 0)
		{
			return;
		}
		ClipboardContents.Provider selectedItem = (ClipboardContents.Provider) source.model.get(address);

		transaction.contribute(new ReCopyCommand(ClipboardEventDispatch.getInstance(), selectedItem));
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = ModelListDomain.class)
	private class ReCopySource
	{
		private final ScrapMenagerieListViewport viewport;
		private final ListDataModel model;

		public ReCopySource(ScrapMenagerieListViewport viewport, ListDataModel model)
		{
			this.viewport = viewport;
			this.model = model;
		}
	}
}
