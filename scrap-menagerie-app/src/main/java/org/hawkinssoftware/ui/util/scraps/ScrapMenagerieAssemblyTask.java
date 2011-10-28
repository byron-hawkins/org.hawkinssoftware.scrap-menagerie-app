package org.hawkinssoftware.ui.util.scraps;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.GenericTransaction;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity.Expansion;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.input.clipboard.ClipboardMonitor;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.DesktopWindow;
import org.hawkinssoftware.azia.ui.component.button.ButtonComposite;
import org.hawkinssoftware.azia.ui.component.button.ChangePushedStateDirective;
import org.hawkinssoftware.azia.ui.component.button.PushButton;
import org.hawkinssoftware.azia.ui.component.text.Label;
import org.hawkinssoftware.azia.ui.component.text.LabelComposite;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.azia.ui.paint.PainterRegistry;
import org.hawkinssoftware.azia.ui.paint.basic.PlainRegionPainter;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;
import org.hawkinssoftware.azia.ui.tile.LayoutUnit.Floater.Edge;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.azia.ui.tile.UnitTile.Layout;
import org.hawkinssoftware.azia.ui.tile.transaction.modify.ModifyLayoutTransaction;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents.LayoutKey;
import org.hawkinssoftware.ui.util.scraps.clip.CurrentClip;
import org.hawkinssoftware.ui.util.scraps.fragment.ScrapMenagerieFragmentLabel;
import org.hawkinssoftware.ui.util.scraps.fragment.ScrapMenagerieFragmentList;
import org.hawkinssoftware.ui.util.scraps.history.ScrapMenagerieHistoryList;

@DomainRole.Join(membership = { RenderingDomain.class })
public class ScrapMenagerieAssemblyTask extends UserInterfaceTask
{
	DesktopWindow<ScrapMenagerieComponents.LayoutKey> window;

	@Override
	protected boolean execute()
	{
		try
		{ 
			@SuppressWarnings("unchecked")
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey> layoutTransaction = getTransaction(ModifyLayoutTransaction.class);
			window = layoutTransaction.createWindow(ScrapMenagerieComponents.LayoutKey.WINDOW, DesktopWindow.FrameType.CLOSE_BUTTON, "Scrap Menagerie");
			PlainRegionPainter<TopTile<ScrapMenagerieComponents.LayoutKey>> topTilePainter = (PlainRegionPainter<TopTile<LayoutKey>>) PainterRegistry
					.getInstance().getPainter(window.getTopTile());
			topTilePainter.borderPlugins.insertPlugin(new BorderPlugin.Solid<TopTile<ScrapMenagerieComponents.LayoutKey>>(Color.black));

			GenericTransaction adHocTransaction = getTransaction(GenericTransaction.class);

			ButtonComposite<PushButton, ?> consolePinComponent = ComponentRegistry.getInstance().establishComposite(
					ScrapMenagerieComponents.CONSOLE_PIN_ASSEMBLY, window);
			ChangePushedStateDirective pushPin = new ChangePushedStateDirective(consolePinComponent.getComponent(), true);
			adHocTransaction.addAction(pushPin);

			LabelComposite<Label, ?> currentClipLabelComponent = ComponentRegistry.getInstance().establishComposite(
					ScrapMenagerieComponents.CURRENT_CLIP_LABEL_ASSEMBLY, window);
			ChangeTextDirective setLabelText = new ChangeTextDirective(currentClipLabelComponent.getComponent(), "System Clipboard");
			adHocTransaction.addAction(setLabelText);

			CurrentClip currentClipComponent = ComponentRegistry.getInstance().establishComposite(ScrapMenagerieComponents.CURRENT_CLIP_ASSEMBLY, window);
			ChangeTextDirective showInitialClip = new ChangeTextDirective(currentClipComponent.getComponent(), CurrentClip.summarize(ClipboardMonitor
					.getInstance().getCurrentClipboardContents()));
			adHocTransaction.addAction(showInitialClip);

			LabelComposite<Label, ?> clipLabelComponent = ComponentRegistry.getInstance().establishComposite(ScrapMenagerieComponents.CLIP_LABEL_ASSEMBLY,
					window);
			ChangeTextDirective setClipLabelText = new ChangeTextDirective(clipLabelComponent.getComponent(), "Clipboard History");
			adHocTransaction.addAction(setClipLabelText);
 
			ScrapMenagerieHistoryList clipListComponent = ComponentRegistry.getInstance().establishComposite(ScrapMenagerieComponents.CLIP_LIST_ASSEMBLY,
					window);
			ScrapMenagerieFragmentLabel fragmentLabelComponent = ComponentRegistry.getInstance().establishComposite(
					ScrapMenagerieComponents.FRAGMENT_LABEL_ASSEMBLY, window);
			ScrapMenagerieFragmentList fragmentListComponent = ComponentRegistry.getInstance().establishComposite(
					ScrapMenagerieComponents.FRAGMENT_LIST_ASSEMBLY, window);

			ComponentEnclosure<PushButton, ?> selectButtonComponent = ComponentRegistry.getInstance().establishComposite(
					ScrapMenagerieComponents.SELECT_BUTTON, window);
			ChangeTextDirective setButtonText = new ChangeTextDirective(selectButtonComponent.getComponent(), "Re-Copy");
			adHocTransaction.addAction(setButtonText);

			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.PairHandle mainPanel = layoutTransaction.createPairTile(
					ScrapMenagerieComponents.LayoutKey.MAIN_PANEL, Axis.V);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.PairHandle dataPanel = layoutTransaction.createPairTile(
					ScrapMenagerieComponents.LayoutKey.DATA_PANEL, Axis.V);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.PairHandle currentClipPanel = layoutTransaction.createPairTile(
					ScrapMenagerieComponents.LayoutKey.CURRENT_CLIP_PANEL, Axis.V);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle currentClipLabelPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.CURRENT_CLIP_LABEL_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.FloaterHandle consolePinPanel = layoutTransaction
					.createFloaterTile(ScrapMenagerieComponents.LayoutKey.CONSOLE_PIN_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle currentClipDisplayPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.CURRENT_CLIP_DISPLAY_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.PairHandle listsPanel = layoutTransaction.createPairTile(
					ScrapMenagerieComponents.LayoutKey.LISTS_PANEL, Axis.H);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.PairHandle clipContentPanel = layoutTransaction.createPairTile(
					ScrapMenagerieComponents.LayoutKey.CLIP_CONTENT_PANEL, Axis.V);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle clipLabelPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.CLIP_LABEL_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle clipListPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.CLIP_LIST_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.PairHandle fragmentContentPanel = layoutTransaction.createPairTile(
					ScrapMenagerieComponents.LayoutKey.FRAGMENT_CONTENT_PANEL, Axis.V);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle fragmentLabelPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.FRAGMENT_LABEL_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle fragmentListPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.FRAGMENT_LIST_PANEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.UnitHandle buttonPanel = layoutTransaction
					.createUnitTile(ScrapMenagerieComponents.LayoutKey.BUTTON_PANEL);

			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle consolePin = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.CONSOLE_PIN);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle currentClipLabel = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.CURRENT_CLIP_LABEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle currentClip = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.CURRENT_CLIP);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle clipLabel = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.CLIP_LABEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle clipList = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.CLIP_LIST);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle fragmentLabel = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.FRAGMENT_LABEL);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle fragmentList = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.FRAGMENT_LIST);
			ModifyLayoutTransaction<ScrapMenagerieComponents.LayoutKey>.ComponentHandle selectButton = layoutTransaction
					.createComponentTile(ScrapMenagerieComponents.LayoutKey.SELECT_BUTTON);

			layoutTransaction.getTopHandle().setUnit(mainPanel);

			mainPanel.setFirstTile(dataPanel);
			mainPanel.setSecondTile(buttonPanel);
			mainPanel.setCrossExpansionPolicy(Expansion.FILL);

			dataPanel.setFirstTile(currentClipPanel);
			dataPanel.setSecondTile(listsPanel);
			dataPanel.setCrossExpansionPolicy(Expansion.FILL);

			currentClipPanel.setFirstTile(currentClipLabelPanel);
			currentClipPanel.setSecondTile(currentClipDisplayPanel);
			currentClipPanel.setCrossExpansionPolicy(Expansion.FILL);

			currentClipLabelPanel.setUnit(currentClipLabel);
			currentClipLabelPanel.setPadding(4, 4, 0, 4);
			currentClipLabelPanel.setLayoutPolicy(Axis.H, Layout.FILL);
			currentClipLabelPanel.setLayoutPolicy(Axis.V, Layout.FIT);

			consolePinPanel.setUnit(consolePin);
			consolePinPanel.setEdge(Edge.RIGHT);
			consolePinPanel.setPadding(0, 4, 0, 0);
			currentClipLabelPanel.addFloater(consolePinPanel);

			currentClipDisplayPanel.setUnit(currentClip);
			currentClipDisplayPanel.setPadding(4, 4, 4, 4);
			currentClipDisplayPanel.setLayoutPolicy(Axis.H, Layout.FILL);
			currentClipDisplayPanel.setLayoutPolicy(Axis.V, Layout.FIT);

			listsPanel.setFirstTile(clipContentPanel);
			listsPanel.setSecondTile(fragmentContentPanel);
			listsPanel.setCrossExpansionPolicy(Expansion.FILL);

			clipContentPanel.setFirstTile(clipLabelPanel);
			clipContentPanel.setSecondTile(clipListPanel);
			clipContentPanel.setCrossExpansionPolicy(Expansion.FILL);

			clipLabelPanel.setUnit(clipLabel);
			clipLabelPanel.setPadding(4, 0, 4, 0);
			clipLabelPanel.setLayoutPolicy(Axis.H, Layout.CENTER);
			clipLabelPanel.setLayoutPolicy(Axis.V, Layout.FIT);

			clipListPanel.setUnit(clipList);
			clipListPanel.setPadding(0, 4, 4, 4);
			clipListPanel.setLayoutPolicy(Axis.H, Layout.FILL);
			clipListPanel.setLayoutPolicy(Axis.V, Layout.FILL);

			fragmentContentPanel.setFirstTile(fragmentLabelPanel);
			fragmentContentPanel.setSecondTile(fragmentListPanel);
			fragmentContentPanel.setCrossExpansionPolicy(Expansion.FILL);

			fragmentLabelPanel.setUnit(fragmentLabel);
			fragmentLabelPanel.setPadding(4, 0, 4, 0);
			fragmentLabelPanel.setLayoutPolicy(Axis.H, Layout.CENTER);
			fragmentLabelPanel.setLayoutPolicy(Axis.V, Layout.FIT);

			fragmentListPanel.setUnit(fragmentList);
			fragmentListPanel.setPadding(0, 4, 4, 0);
			fragmentListPanel.setLayoutPolicy(Axis.H, Layout.FILL);
			fragmentListPanel.setLayoutPolicy(Axis.V, Layout.FILL);

			buttonPanel.setUnit(selectButton);
			buttonPanel.setPadding(0, 0, 4, 0);
			buttonPanel.setLayoutPolicy(Axis.H, Layout.CENTER);
			buttonPanel.setLayoutPolicy(Axis.V, Layout.FIT);

			consolePin.setComponent(consolePinComponent);
			currentClipLabel.setComponent(currentClipLabelComponent);
			currentClip.setComponent(currentClipComponent);
			clipLabel.setComponent(clipLabelComponent);
			clipList.setComponent(clipListComponent);
			fragmentLabel.setComponent(fragmentLabelComponent);
			fragmentList.setComponent(fragmentListComponent);
			selectButton.setComponent(selectButtonComponent);

			layoutTransaction.assemble();

//			ClipboardHandler.install();
//			ScrapMenagerieLayoutHandler.install();
//			ScrapMenagerieFragmentProcessor.install();
//			ReCopyHandler.install();
//			ScrapMenagerieListFocusManager.install();

			return true;
		}
		catch (Throwable t)
		{
			Log.out(Tag.CRITICAL, t, "Failed to assemble the Scrap Menagerie application.");
			throw new RuntimeException(t);
		}
	}
}
