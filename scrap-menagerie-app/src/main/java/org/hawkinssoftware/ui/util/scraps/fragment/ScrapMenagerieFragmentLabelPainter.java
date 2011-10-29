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

import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.ui.paint.ComponentPainter;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
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
