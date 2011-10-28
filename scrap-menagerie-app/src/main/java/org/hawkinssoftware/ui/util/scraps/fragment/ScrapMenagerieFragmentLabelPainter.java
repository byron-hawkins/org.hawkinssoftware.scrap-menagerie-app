package org.hawkinssoftware.ui.util.scraps.fragment;

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;

public class ScrapMenagerieFragmentLabelPainter extends ComponentPainter<ScrapMenagerieFragmentLabel.Component> implements ScrapMenagerieFragmentLabel.Painter
{
	private BackgroundPlugin<ScrapMenagerieFragmentLabel.Component> background = new BackgroundPlugin<ScrapMenagerieFragmentLabel.Component>();

	public void setBackground(BackgroundPlugin<ScrapMenagerieFragmentLabel.Component> background)
	{
		this.background = background;
	}
	
	@Override
	public void paint(ScrapMenagerieFragmentLabel.Component component)
	{
		background.paint(component);
	}

	@Override
	public int getPackedSize(Axis axis)
	{
		return 0;
	}
}
