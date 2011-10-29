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

import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ScrapMenagerieItem implements ClipboardContents.Provider
{
	private final ClipboardContents clipboardContents;
	private String cellText = "";

	public ScrapMenagerieItem(ClipboardContents clipboardContents, String cellText)
	{
		this.clipboardContents = clipboardContents;
		this.cellText = cellText;
	}

	@Override
	public ClipboardContents getClipboardContents()
	{
		return clipboardContents;
	}

	public String getCellText()
	{
		return cellText;
	}

	public void setCellText(String cellText)
	{
		this.cellText = cellText;
	}
}
