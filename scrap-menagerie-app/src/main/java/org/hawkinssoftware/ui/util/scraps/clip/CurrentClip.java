package org.hawkinssoftware.ui.util.scraps.clip;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.text.Label;
import org.hawkinssoftware.azia.ui.component.text.LabelComposite;
import org.hawkinssoftware.azia.ui.component.text.handler.PlainTextHandler;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.basic.text.LabelPainter;
import org.hawkinssoftware.azia.ui.paint.canvas.Inset;
import org.hawkinssoftware.azia.ui.paint.plugin.BackgroundPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;

// WIP: character '@' is truncated because it has extra-deep descent
public class CurrentClip extends LabelComposite<Label, LabelPainter<Label>>
{
	public static class Assembly extends CompositeAssembly<Label, Label.Painter, CurrentClip>
	{
		private static final Color BORDER_COLOR = new Color(0x666666);

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Assembly()
		{
			super(SynchronizationRole.AUTONOMOUS);

			setComponent(new AbstractComponent.Key<Label>(Label.class));
			setPainter(InstancePainter.Key.createKey(Label.Painter.class, (Class<LabelPainter<Label>>) (Class) LabelPainter.class));
			setEnclosure(new ComponentEnclosure.Key(CurrentClip.class));
		}

		@Override
		public void assemble(CurrentClip enclosure)
		{
			super.assemble(enclosure);

			enclosure.getComponent().installHandler(new PlainTextHandler.Basic(enclosure.getComponent()));

			enclosure.getPainter().setBackground(new BackgroundPlugin.Solid<Label>(Color.white));
			enclosure.getPainter().borderPlugins.clearPlugins();
			enclosure.getPainter().borderPlugins.insertPlugin(new BorderPlugin.Solid<Label>(BORDER_COLOR));
			enclosure.getPainter().borderPlugins.insertPlugin(new BorderPlugin.Empty<Label>(Inset.homogenous(4)));
		}
	}

	public static String summarize(ClipboardContents clipboardContents)
	{
		String clipboardSummary = clipboardContents.getSummary();
		if (clipboardSummary.length() > 60)
		{
			clipboardSummary = clipboardSummary.substring(0, 60) + "...";
		}
		return clipboardSummary.replaceAll("\\s+", " ");
	}

	public CurrentClip(Label component)
	{
		super(component);
	}
}
