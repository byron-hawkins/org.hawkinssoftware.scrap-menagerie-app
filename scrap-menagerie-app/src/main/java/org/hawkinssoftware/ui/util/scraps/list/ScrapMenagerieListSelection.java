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
package org.hawkinssoftware.ui.util.scraps.list;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.DataChangeNotification;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieKeyCommand;

// TODO: require @ValidateRead/Write tags to be on a class of type UIActor or delegate
/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = ModelListDomain.class)
public class ScrapMenagerieListSelection implements UserInterfaceHandler, UserInterfaceActorDelegate, CompositionElement.Initializing
{
	private ListDataModel model;
	private CellViewportComposite<ListModelPainter> viewport;

	@ValidateRead
	@ValidateWrite
	private int selectedRow = -1;

	public ScrapMenagerieListSelection()
	{
		KeyEventDispatch.getInstance().installHandler(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void compositionCompleted()
	{
		model = CompositionRegistry.getService(ListDataModel.class);
		viewport = CompositionRegistry.getComposite(CellViewportComposite.class);
	}

	@Override
	public UserInterfaceActor getActor()
	{
		return viewport.getComponent();
	}

	public int getSelectedRow()
	{
		return selectedRow;
	}

	public void dataChanging(DataChangeNotification change, PendingTransaction transaction)
	{
		// WIP: how to handle multiple row removals in the transaction? Either I need to see the whole transaction, or I
		// need some kind of notification having all rows in it--or I'll have to keep track of every row change, which
		// is outside the scope of this class.
		
		switch (change.type)
		{
			case ADD:
				if (selectedRow < 0)
				{
					transaction.contribute(new SetSelectedRowDirective(change.address.getComponent(), 0));
				}
				break;
			case REMOVE:
				if ((selectedRow == change.address.row) && (change.address.row == (model.getRowCount(Section.SCROLLABLE) - 1)))
				{ // the last row is being deleted, so move up one
					transaction.contribute(new SetSelectedRowDirective(change.address.getComponent(), change.address.row - 1));
				}
				break;
		}
	}

	public void setSelectedRow(SetSelectedRowDirective selection)
	{
		selectedRow = selection.row;
	}

	public void selectedRowChanging(SetSelectedRowDirective.Notification notification, PendingTransaction transaction)
	{
		viewport.getCellPainter().repaint(viewport.createAddress(selectedRow, Section.SCROLLABLE));
		viewport.getCellPainter().repaint(viewport.createAddress(notification.row, Section.SCROLLABLE));
	}

	public void keyEvent(KeyboardInputNotification event, PendingTransaction transaction)
	{
		if (ScrapMenagerieListFocusManager.getInstance().getFocusedList() != viewport)
		{
			return;
		}

		switch (ScrapMenagerieKeyCommand.getCommand(event.event))
		{
			case SELECTION_UP:
				if (selectedRow > 0)
				{
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), selectedRow - 1));
				}
				break;
			case SELECTION_DOWN:
				if (selectedRow < (model.getRowCount(Section.SCROLLABLE) - 1))
				{
					transaction.contribute(new SetSelectedRowDirective(viewport.getComponent(), selectedRow + 1));
				}
				break;
		}
	}
}
