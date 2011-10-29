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

import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.paint.basic.cell.CellStamp;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieFragment;

/**
 * A factory for creating ScrapMenagerieFragmentStamp objects.
 */
public class ScrapMenagerieFragmentStampFactory implements CellStamp.Factory
{
	private final CellStamp<ScrapMenagerieFragment> fragmentStamp = new ScrapMenagerieFragmentStamp();

	@SuppressWarnings("unchecked")
	public <DataType> CellStamp<DataType> getStamp(RowAddress address, DataType datum)
	{
		return (CellStamp<DataType>) fragmentStamp;
	}
}
