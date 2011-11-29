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
package org.hawkinssoftware.ui.util.scraps.history;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.input.MouseInputEvent.Button;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.transaction.SetSelectedRowDirective;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.basic.cell.AbstractCellStamp;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Size;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieItem;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListSelection;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = ModelListDomain.class)
public class ScrapMenagerieClipStamp extends AbstractCellStamp<ScrapMenagerieItem>
{
	public static final CellPluginKey<CellHandler> CELL_PLUGIN_KEY = new CellPluginKey<CellHandler>();

	private static final Color SELECTION_BACKGROUND = new Color(0xEEFFBB);

	private Size size = Size.EMPTY;

	private ScrapMenagerieListViewport viewport;
	private ScrapMenagerieListSelection selection;

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		ScrapMenagerieHistoryList historyList = CompositionRegistry.getComposite(ScrapMenagerieHistoryList.class);
		viewport = historyList.getViewport();
		selection = historyList.getService(ScrapMenagerieListSelection.class);
	}

	protected void interactiveCellCreated(InteractiveCell cell)
	{
		cell.addPlugin(new CellHandler(cell));
	}

	@Override
	protected void paint(RowAddress address, ScrapMenagerieItem datum, InteractiveCell interactiveCell)
	{
		Canvas c = Canvas.get();

		if (selection.getSelectedRow() == address.row)
		{
			c.pushColor(SELECTION_BACKGROUND);
			c.g.fillRect(0, 0, c.span().width, c.span().height); // viewport.getBounds().width - 2, ROW_HEIGHT);
		}

		c.pushColor(Color.black);
		c.g.drawString(datum.getCellText(), 0, CellStamp.TEXT_BASELINE);
	}

	@Override
	public int getSpan(Axis axis, ScrapMenagerieItem datum)
	{
		switch (axis)
		{
			case H:
				size = InstancePainter.TextMetrics.INSTANCE.getSize(datum.getCellText(), BoundsType.TEXT);
				return size.width;
			case V:
				// TODO: agent gags on this constant when not qualified by the interface name
				return CellStamp.ROW_HEIGHT;
			default:
				throw new UnknownEnumConstantException(axis);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@ValidateRead
	@ValidateWrite
	@DomainRole.Join(membership = { FlyweightCellDomain.class })
	public static class CellHandler implements CellPlugin, UserInterfaceHandler, CompositionElement.Initializing, UserInterfaceActorDelegate
	{
		private final InteractiveCell cell;
		private ScrapMenagerieListViewport viewport;

		public CellHandler(InteractiveCell cell)
		{
			this.cell = cell;
			cell.installHandler(this);
		}

		@Override
		public void compositionCompleted()
		{
			// WIP: it's very annoying that this cell needs to know which composite it's registered under. I want the
			// viewport, and would prefer to simply ask for it, but the stamp factory was instantiated under the scroll
			// pane composite, so that is the only thing I can see from here.
			viewport = CompositionRegistry.getComposite(ScrapMenagerieHistoryList.class).getViewport();
		}

		public void mouseEvent(EventPass pass, PendingTransaction transaction)
		{
			if (pass.event().getButtonPress() == Button.LEFT)
			{
				transaction.contribute(new SetSelectedRowDirective(viewport, cell.cellContext.getAddress().row));
			}
		}

		@Override
		public UserInterfaceActor getActor()
		{
			return cell.getActor();
		}

		@Override
		public CellPluginKey<? extends CellPlugin> getKey()
		{
			return CELL_PLUGIN_KEY;
		}
	}
}
