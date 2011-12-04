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

import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.cell.CellViewportComposite;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelCellViewport;
import org.hawkinssoftware.azia.ui.paint.basic.cell.ListModelPainter;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ScrapMenagerieListViewport extends CellViewportComposite<ListModelPainter> implements ListDataModel.ComponentContext
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
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
}
