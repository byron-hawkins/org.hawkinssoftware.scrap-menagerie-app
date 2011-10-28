package org.hawkinssoftware.ui.util.scraps.fragment;

import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieFragment;

public class ScrapMenagerieFragmentStampFactory implements CellStamp.Factory
{
	private final CellStamp<ScrapMenagerieFragment> fragmentStamp = new ScrapMenagerieFragmentStamp();

	@SuppressWarnings("unchecked")
	public <DataType> CellStamp<DataType> getStamp(RowAddress address, DataType datum)
	{
		return (CellStamp<DataType>) fragmentStamp;
	}
}
