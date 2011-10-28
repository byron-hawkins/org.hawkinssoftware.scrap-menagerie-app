package org.hawkinssoftware.ui.util.scraps.list;

import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelCellViewport;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

public class ScrapMenagerieListViewport extends CellViewportComposite<ListModelPainter> implements ListDataModel.ComponentContext
{
	@DomainRole.Join(membership = ModelListDomain.class)
	public static class Assembly extends CellViewport.Assembly<ListModelPainter, ScrapMenagerieListViewport>
	{
		public Assembly()
		{
			super(ListModelCellViewport.class, ScrapMenagerieListViewport.class);
		}

		@Override
		protected ListModelPainter createCellPainter()
		{
			return new ListModelPainter();
		}
	}

	public final ScrapMenagerieListSelection selection;

	public ScrapMenagerieListViewport(ListModelCellViewport viewport)
	{
		super(viewport);

		selection = new ScrapMenagerieListSelection();
		installHandler(selection);
	}

	@Override
	public RowAddress createAddress(int row, Section section)
	{
		return new RowAddress(getComponent(), row, section);
	}
}
