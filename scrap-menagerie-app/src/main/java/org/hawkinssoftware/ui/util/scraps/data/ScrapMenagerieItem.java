package org.hawkinssoftware.ui.util.scraps.data;

import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

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
