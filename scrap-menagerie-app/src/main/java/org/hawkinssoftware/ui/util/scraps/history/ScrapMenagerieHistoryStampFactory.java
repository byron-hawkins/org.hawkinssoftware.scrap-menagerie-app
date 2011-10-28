package org.hawkinssoftware.ui.util.scraps.history;

import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieItem;

public class ScrapMenagerieHistoryStampFactory implements CellStamp.Factory
{
	private final CellStamp<ScrapMenagerieItem> itemStamp = new ScrapMenagerieClipStamp();

	@SuppressWarnings("unchecked")
	public <DataType> CellStamp<DataType> getStamp(RowAddress address, DataType datum)
	{
		return (CellStamp<DataType>) itemStamp;
	}
}
