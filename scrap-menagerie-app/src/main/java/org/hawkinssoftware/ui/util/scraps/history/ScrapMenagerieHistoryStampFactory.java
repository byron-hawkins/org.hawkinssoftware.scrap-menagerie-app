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

import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieItem;

/**
 * A factory for creating ScrapMenagerieHistoryStamp objects.
 */
public class ScrapMenagerieHistoryStampFactory implements CellStamp.Factory
{
	private final CellStamp<ScrapMenagerieItem> itemStamp = new ScrapMenagerieClipStamp();

	@SuppressWarnings("unchecked")
	public <DataType> CellStamp<DataType> getStamp(RowAddress address, DataType datum)
	{
		return (CellStamp<DataType>) itemStamp;
	}
}
