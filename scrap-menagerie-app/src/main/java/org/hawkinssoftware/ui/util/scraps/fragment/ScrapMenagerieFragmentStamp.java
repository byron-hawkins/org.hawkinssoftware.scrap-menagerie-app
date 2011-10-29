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
package org.hawkinssoftware.ui.util.scraps.fragment;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.input.MouseAware.EventPass;
import org.hawkinssoftware.azia.ui.input.MouseAware.Forward;
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
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieFragment;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListSelection;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = ModelListDomain.class)
public class ScrapMenagerieFragmentStamp extends AbstractCellStamp<ScrapMenagerieFragment>
{
	public static final CellPluginKey<CellHandler> CELL_PLUGIN_KEY = new CellPluginKey<CellHandler>();

	private static final int BUTTON_SPAN = InstancePainter.TextMetrics.INSTANCE.getSize("_", BoundsType.TEXT).width;
	private static final int PAD = 4;
	private static final int LABEL_START_X = CellStamp.ROW_HEIGHT + PAD;

	private static final Color SELECTION_BACKGROUND = new Color(0xEEFFBB);

	private Size size = Size.EMPTY;

	private ScrapMenagerieListViewport viewport;
	private ScrapMenagerieListSelection selection;

	private final FragmentActivationButton button = new FragmentActivationButton(BUTTON_SPAN, ROW_HEIGHT);
	private final FragmentTextLabel label = new FragmentTextLabel();

	@Override
	public void compositionCompleted()
	{
		super.compositionCompleted();

		ScrapMenagerieFragmentList fragmentList = CompositionRegistry.getComposite(ScrapMenagerieFragmentList.class);
		viewport = fragmentList.getViewport();
		selection = fragmentList.getService(ScrapMenagerieListSelection.class);
	}

	protected void interactiveCellCreated(InteractiveCell cell)
	{
		cell.addPlugin(new CellHandler(cell));
	}

	@Override
	protected void paint(RowAddress address, ScrapMenagerieFragment datum, InteractiveCell interactiveCell)
	{
		Canvas c = Canvas.get();
		CellHandler handler = (interactiveCell == null ? null : interactiveCell.getPlugin(CELL_PLUGIN_KEY));

		if (selection.getSelectedRow() == address.row)
		{
			c.pushColor(SELECTION_BACKGROUND);
			c.g.fillRect(0, 0, viewport.getBounds().width, c.span().height);
		}

		button.paint(c, address, datum, (handler == null) ? null : handler.button);

		c.pushBoundsPosition(LABEL_START_X, 0);
		label.paint(c, address, datum, (handler == null) ? null : handler.label);
	}

	@Override
	public int getSpan(Axis axis, ScrapMenagerieFragment datum)
	{
		switch (axis)
		{
			case H:
				size = InstancePainter.TextMetrics.INSTANCE.getSize(datum.getText(), BoundsType.TEXT);
				return LABEL_START_X + size.width;
			case V:
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
	public class CellHandler implements CellPlugin, UserInterfaceHandler, CompositionElement.Initializing
	{
		private final InteractiveCell cell;
		private ScrapMenagerieListViewport viewport;

		public final FragmentActivationButton.InteractiveInstance button;
		public final FragmentTextLabel.InteractiveInstance label;

		public CellHandler(InteractiveCell cell)
		{
			this.cell = cell;
			cell.installHandler(this);

			button = ScrapMenagerieFragmentStamp.this.button.createStateHandler(cell.cellContext);
			label = ScrapMenagerieFragmentStamp.this.label.createStateHandler(cell.cellContext.translate(LABEL_START_X, 0));
		}

		@Override
		public void compositionCompleted()
		{
			// WIP: it's very annoying that this cell needs to know which composite it's registered under. I want the
			// viewport, and would prefer to simply ask for it, but the stamp factory was instantiated under the scroll
			// pane composite, so that is the only thing I can see from here.
			viewport = CompositionRegistry.getComposite(ScrapMenagerieFragmentList.class).getViewport();
		}

		public void mouseEvent(EventPass pass, PendingTransaction transaction)
		{
			if (pass.event().x() < (cell.cellContext.x() + LABEL_START_X))
			{
				transaction.contribute(new Forward(button));
			}
			else
			{
				transaction.contribute(new Forward(label));
			}
		}

		@Override
		public CellPluginKey<? extends CellPlugin> getKey()
		{
			return CELL_PLUGIN_KEY;
		}
	}
}
