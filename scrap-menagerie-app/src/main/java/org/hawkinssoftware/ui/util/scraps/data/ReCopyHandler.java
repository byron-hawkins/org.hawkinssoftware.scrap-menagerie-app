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

/**
 * @JTourBusStop 1, ReCopyHandler participates in mouse and keyboard transactions, Introducing the ReCopyHandler:
 * 
 *               The only external data operation of the Scrap Menagerie application is to put objects into the system
 *               clipboard. This occurs when the user clicks the "Re-Copy" button, or presses the equivalent shortcut
 *               keys on the keyboard. This role of this ReCopyHandler is to observe all user interface transactions and
 *               respond to these re-copy triggers by contributing an action that puts the corresponding data on the
 *               system clipboard.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
public class ReCopyHandler implements UserInterfaceHandler
{
	/**
	 * @JTourBusStop 2, ReCopyHandler participates in mouse and keyboard transactions, Installing the ReCopyHandler into
	 *               the mouse and keyboard action sources:
	 * 
	 *               The ReCopyHandler wants to collaborate in the transactions of key events and clicks on the
	 *               "Re-Copy" button. This is done by installing the handler into the relevant instance of
	 *               UserInterfaceHandler.Host, in this case the KeyEventDispatch and a PushButton, respectively. Now,
	 *               any UserInterfaceDirective (write action) or UserInterfaceNotification (readonly action) sent to
	 *               the KeyEventDispatch or the "Re-Copy" button will be broadcast to this handler.
	 */
	@InvocationConstraint(domains = AssemblyDomain.class)
	public static void install()
	{
		KeyEventDispatch.getInstance().installHandler(INSTANCE);
		ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.RE_COPY_BUTTON).installHandler(INSTANCE);
	}

	public static ReCopyHandler getInstance()
	{
		return INSTANCE;
	}

	private static final ReCopyHandler INSTANCE = new ReCopyHandler();

	private final PaintableActorDelegate currentClipActor;

	private final ReCopySource clipSource;
	private final ReCopySource fragmentSource;

	private ReCopyHandler()
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

	/**
	 * @JTourBusStop 3, ReCopyHandler participates in mouse and keyboard transactions, Receiving a
	 *               KeyboardInputNotification:
	 * 
	 *               When the user presses any key on the keyboard, a transaction is created and a
	 *               KeyboardInputNotification is added to it. The transaction automatically broadcasts the notification
	 *               to this handler, as explained In stop #2. Parameter PendingTransaction is an invitation for this
	 *               handler to collaborate by adding actions in response to the KeyboardInputNotification. If the key
	 *               event is the re-copy shortcut key combination, then re-copy is executed.
	 **/
	public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
	{
		if (ScrapMenagerieKeyCommand.getCommand(key.event) == ScrapMenagerieKeyCommand.RE_COPY)
		{
			reCopySelectedRow(transaction);
		}
	}

	/**
	 * @JTourBusStop 5, ReCopyHandler participates in mouse and keyboard transactions, Receiving a
	 *               ChangePressedStateDirective.Notification:
	 * 
	 *               The process of responding to a "Re-Copy" button press is identical to that of a re-copy shortcut
	 *               key press. The same ReCopyCommand will be added to the PendingTransaction for execution on commit.
	 */
	public void buttonPressed(ChangePressedStateDirective.Notification button, PendingTransaction transaction)
	{
		if (button.isPressed())
		{
			reCopySelectedRow(transaction);
		}
	}

	/**
	 * @JTourBusStop 4, ReCopyHandler participates in mouse and keyboard transactions, Contributing a ReCopyCommand:
	 * 
	 *               To execute a re-copy, this handler identifies the data which is to be re-copied, and adds it to the
	 *               transaction in a ReCopyCommand directed at the ClipboardEventDispatch. This method returns without
	 *               making any changes to the system clipboard. When all collaborators have made their contributions to
	 *               the transaction, its actions will be executed in a sequential commit phase (4.1). The ReCopyCommand
	 *               puts its data into the system clipboard at the time it is committed (4.2).
	 */
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
