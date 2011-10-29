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
package org.hawkinssoftware.ui.util.scraps.data;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ScrapMenagerieFragment implements ClipboardContents.Provider, Transferable
{
	private static final DataFlavor[] DATA_FLAVORS = new DataFlavor[] { DataFlavor.stringFlavor };

	private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

	public final int id = ID_GENERATOR.incrementAndGet();

	private boolean active = false;

	private String text = "";

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@Override
	public ClipboardContents getClipboardContents()
	{
		try
		{
			return new ClipboardContents(this);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Failed to construct clipboard contents from a " + getClass().getSimpleName());
		}
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (flavor.isFlavorTextType())
		{
			return text;
		}
		throw new UnsupportedFlavorException(flavor);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors()
	{
		return DATA_FLAVORS;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return flavor.isFlavorTextType();
	}
}
