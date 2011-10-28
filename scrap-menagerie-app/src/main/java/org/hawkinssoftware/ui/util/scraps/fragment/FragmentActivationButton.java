package org.hawkinssoftware.ui.util.scraps.fragment;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MousePressedState;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangePressedStateDirective;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellContext;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents.CellComponentDomain;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieFragment;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

@DomainRole.Join(membership = { CellComponentDomain.class, FlyweightCellDomain.class })
public class FragmentActivationButton implements CompositionElement.Initializing
{
	private static final Color ACTIVE_COLOR = new Color(0x990000);
	private static final Color INACTIVE_COLOR = Color.black;

	private final int span;
	private final int footprint;
	private final int inset;

	private ScrapMenagerieListViewport viewport;

	FragmentActivationButton(int span, int footprint)
	{
		this.span = span;
		this.footprint = footprint;
		
		inset = (footprint - span) / 2;
	}

	@Override
	public void compositionCompleted()
	{
		viewport = CompositionRegistry.getComposite(ScrapMenagerieFragmentList.class).getViewport();
	}

	public void paint(Canvas c, RowAddress address, ScrapMenagerieFragment datum, InteractiveInstance stateHandler)
	{
		if (datum.isActive())
		{
			c.g.setColor(ACTIVE_COLOR);
		}
		else
		{
			c.g.setColor(INACTIVE_COLOR);
		}

		c.g.fillRect(inset, inset, span, span);
	}

	InteractiveInstance createStateHandler(CellContext<ScrapMenagerieFragment> cellContext)
	{
		return new InteractiveInstance(cellContext);
	}

	@ValidateRead
	@ValidateWrite
	@DomainRole.Join(membership = { UserInterfaceActor.DependentActorDomain.class, FlyweightCellDomain.class })
	class InteractiveInstance extends VirtualComponent
	{
		private final CellContext<ScrapMenagerieFragment> cellContext;

		private final ToggleActiveFlagAction toggleAction = new ToggleActiveFlagAction();
		private boolean active = false;

		public InteractiveInstance(CellContext<ScrapMenagerieFragment> cellContext)
		{
			this.cellContext = cellContext;

			MousePressedState.install(this);
			installHandler(new PressedStateHandler());
		}

		@Override
		public void requestRepaint()
		{
			RepaintRequestManager.requestRepaint(cellContext.createRepaintRequest());
		}

		@DomainRole.Join(membership = FlyweightCellDomain.class)
		public class PressedStateHandler implements UserInterfaceHandler
		{
			public void pressedStateChanging(ChangePressedStateDirective.Notification change, PendingTransaction transaction)
			{
				if (change.isPressed())
				{
					transaction.contribute(toggleAction);
					transaction.contribute(new ActivateFragmentNotification(viewport, cellContext.getDatum().id, !active));
				}
			}

			public void toggleActiveState(ToggleActiveFlagAction action)
			{
				active = !active;
			}
		}

		public class ToggleActiveFlagAction extends UserInterfaceDirective
		{
			public ToggleActiveFlagAction()
			{
				super(InteractiveInstance.this);
			}
		}
	}
}
