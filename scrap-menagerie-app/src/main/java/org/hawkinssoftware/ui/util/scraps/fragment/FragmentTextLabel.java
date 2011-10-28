package org.hawkinssoftware.ui.util.scraps.fragment;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.input.MouseInputEvent.Button;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.VirtualComponent;
import org.hawkinssoftware.azia.ui.component.composition.CompositionElement;
import org.hawkinssoftware.azia.ui.component.composition.CompositionRegistry;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellContext;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieFragment;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListSelection;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;
import org.hawkinssoftware.ui.util.scraps.list.SetSelectedRowDirective;

@DomainRole.Join(membership = RenderingDomain.class)
public class FragmentTextLabel implements CompositionElement.Initializing
{
	private ScrapMenagerieListSelection selection;
	private ScrapMenagerieListViewport viewport;

	@Override
	public void compositionCompleted()
	{
		selection = CompositionRegistry.getService(ScrapMenagerieListSelection.class);
		viewport = CompositionRegistry.getComposite(ScrapMenagerieFragmentList.class).getViewport();
	}

	void paint(Canvas c, RowAddress address, ScrapMenagerieFragment datum, InteractiveInstance stateHandler)
	{
		c.g.setColor(Color.black);
		c.g.drawString(datum.getText(), 0, CellStamp.TEXT_BASELINE);
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
		final Handler handler = new Handler();

		public InteractiveInstance(CellContext<ScrapMenagerieFragment> cellContext)
		{
			this.cellContext = cellContext;

			installHandler(handler);
		}

		@Override
		public void requestRepaint()
		{
			RepaintRequestManager.requestRepaint(cellContext.createRepaintRequest());
		}

		@DomainRole.Join(membership = FlyweightCellDomain.class)
		public class Handler implements UserInterfaceHandler
		{
			public void mouseEvent(EventPass pass, PendingTransaction transaction)
			{
				if (pass.event().getButtonPress() == Button.LEFT)
				{
					transaction.contribute(new SetSelectedRowDirective(viewport, cellContext.getAddress().row));
				}
			}
		}
	}
}
