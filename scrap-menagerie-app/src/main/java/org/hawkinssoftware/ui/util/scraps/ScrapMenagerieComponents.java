package org.hawkinssoftware.ui.util.scraps;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.hawkinssoftware.azia.ui.component.button.ButtonComposite;
import org.hawkinssoftware.azia.ui.component.button.PushButton;
import org.hawkinssoftware.azia.ui.component.button.PushedStateHandler;
import org.hawkinssoftware.azia.ui.component.button.ToggleIconPlugin;
import org.hawkinssoftware.azia.ui.component.text.Label;
import org.hawkinssoftware.azia.ui.component.text.LabelComposite;
import org.hawkinssoftware.azia.ui.component.transaction.mouse.MousePressedState;
import org.hawkinssoftware.azia.ui.paint.basic.button.PushButtonPainter;
import org.hawkinssoftware.azia.ui.tile.LayoutEntity;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.ui.util.scraps.clip.CurrentClip;
import org.hawkinssoftware.ui.util.scraps.fragment.ScrapMenagerieFragmentLabel;
import org.hawkinssoftware.ui.util.scraps.fragment.ScrapMenagerieFragmentList;
import org.hawkinssoftware.ui.util.scraps.history.ScrapMenagerieHistoryList;

public class ScrapMenagerieComponents
{
	public enum LayoutKey implements LayoutEntity.Key<LayoutKey>
	{
		WINDOW,
		MAIN_PANEL, // pair
		DATA_PANEL, // pair
		CURRENT_CLIP_PANEL, // unit
		CURRENT_CLIP_LABEL_PANEL, // unit
		CONSOLE_PIN_PANEL, // floating unit
		CURRENT_CLIP_DISPLAY_PANEL, // unit
		LISTS_PANEL, // pair:H (adjacent labeled lists)
		CLIP_CONTENT_PANEL, // pair:V (label over list)
		CLIP_LABEL_PANEL, // unit
		CLIP_LIST_PANEL, // unit
		FRAGMENT_CONTENT_PANEL, // pair:V (label over list)
		FRAGMENT_LABEL_PANEL, // unit
		FRAGMENT_LIST_PANEL, // unit
		BUTTON_PANEL, // unit
		CONSOLE_PIN, // component
		CURRENT_CLIP_LABEL, // component
		CURRENT_CLIP, // component
		CLIP_LABEL, // component
		CLIP_LIST, // component
		FRAGMENT_LABEL, // component
		FRAGMENT_LIST, // component
		SELECT_BUTTON; // component

		@Override
		public String getName()
		{
			return name();
		}
	}

	public static class CellComponentDomain extends DomainRole
	{
		@DomainRole.Instance
		public static final CellComponentDomain INSTANCE = new CellComponentDomain();
	}

	private static class CurrentClipLabelAssembly extends Label.Assembly
	{
		@Override
		public void assemble(LabelComposite<Label, ?> enclosure)
		{
			super.assemble(enclosure);
		}
	}

	private static class ConsolePinAssembly extends PushButton.Assembly
	{
		private static final String PUSHED_PIN_PATH = "images/PushpinDown.jpg";
		private static final String PULLED_PIN_PATH = "images/PushpinUp.jpg";

		@Override
		public void assemble(ButtonComposite<PushButton, ?> enclosure)
		{
			super.assemble(enclosure);

			try
			{
				Image pushedPin = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(PUSHED_PIN_PATH));
				Image pulledPin = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream(PULLED_PIN_PATH));

				MousePressedState.install(enclosure.getComponent());
				PushedStateHandler.install(enclosure.getComponent());

				PushButtonPainter painter = (PushButtonPainter) enclosure.getPainter();
				painter.setIconPlugin(new ToggleIconPlugin(20, 18, pulledPin, pushedPin));
				painter.setBackground(null);
				painter.borderPlugins.clearPlugins();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Failed to load the pushpin images from the classpath.", e);
			}
		}
	}

	public static final ConsolePinAssembly CONSOLE_PIN_ASSEMBLY = new ConsolePinAssembly();
	public static final CurrentClipLabelAssembly CURRENT_CLIP_LABEL_ASSEMBLY = new CurrentClipLabelAssembly();
	public static final CurrentClip.Assembly CURRENT_CLIP_ASSEMBLY = new CurrentClip.Assembly();
	public static final Label.Assembly CLIP_LABEL_ASSEMBLY = new Label.Assembly();
	public static final ScrapMenagerieHistoryList.ScrollPaneAssembly CLIP_LIST_ASSEMBLY = new ScrapMenagerieHistoryList.ScrollPaneAssembly();
	public static final ScrapMenagerieFragmentLabel.Assembly FRAGMENT_LABEL_ASSEMBLY = new ScrapMenagerieFragmentLabel.Assembly();
	public static final ScrapMenagerieFragmentList.ScrollPaneAssembly FRAGMENT_LIST_ASSEMBLY = new ScrapMenagerieFragmentList.ScrollPaneAssembly();
	public static final PushButton.Assembly SELECT_BUTTON = new PushButton.TextButtonAssembly();

	private static final ScrapMenagerieComponents INSTANCE = new ScrapMenagerieComponents();

	public static ScrapMenagerieComponents getInstance()
	{
		return INSTANCE;
	}

	private ScrapMenagerieConsole console;

	public ScrapMenagerieConsole getConsole()
	{
		return console;
	}

	void setConsole(ScrapMenagerieConsole console)
	{
		this.console = console;
	}
}
