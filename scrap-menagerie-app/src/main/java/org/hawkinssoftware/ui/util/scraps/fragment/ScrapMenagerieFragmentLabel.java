package org.hawkinssoftware.ui.util.scraps.fragment;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.GenericTransaction;
import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceActor.SynchronizationRole;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.layout.Axis;
import org.hawkinssoftware.azia.core.layout.BoundedEntity;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.DisplayBoundsDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.RenderingDomain;
import org.hawkinssoftware.azia.ui.component.AbstractComponent;
import org.hawkinssoftware.azia.ui.component.ComponentEnclosure;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.EnclosureBounds;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.button.ButtonComposite;
import org.hawkinssoftware.azia.ui.component.button.PushButton;
import org.hawkinssoftware.azia.ui.component.composition.AbstractComposite;
import org.hawkinssoftware.azia.ui.component.composition.CompositeAssembly;
import org.hawkinssoftware.azia.ui.component.text.Label;
import org.hawkinssoftware.azia.ui.component.text.LabelComposite;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.component.transaction.resize.ComponentBoundsChangeDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangePressedStateDirective;
import org.hawkinssoftware.azia.ui.component.transaction.state.ChangeTextDirective;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.paint.InstancePainter;
import org.hawkinssoftware.azia.ui.paint.InstancePainter.TextMetrics.BoundsType;
import org.hawkinssoftware.azia.ui.paint.basic.button.PushButtonPainter;
import org.hawkinssoftware.azia.ui.paint.basic.text.LabelPainter;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;
import org.hawkinssoftware.azia.ui.paint.plugin.LabelTextPlugin;
import org.hawkinssoftware.azia.ui.paint.transaction.paint.PaintIncludeNotification;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieKeyCommand;
import org.hawkinssoftware.ui.util.scraps.fragment.FragmentCollectionNotification.Command;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

public class ScrapMenagerieFragmentLabel extends AbstractComposite<ScrapMenagerieFragmentLabel.Component, ScrapMenagerieFragmentLabelPainter>
{
	public static class Assembly extends CompositeAssembly<Component, Painter, ScrapMenagerieFragmentLabel>
	{
		private static final Color BUTTON_OUTLINE_COLOR = new Color(0x8888888);
		
		private final Label.Assembly text = new Label.Assembly();
		private final PushButton.Assembly addButton = new PushButton.TextButtonAssembly();
		private final PushButton.Assembly removeButton = new PushButton.TextButtonAssembly();

		public Assembly()
		{
			super(SynchronizationRole.AUTONOMOUS);

			setComponent(new AbstractComponent.Key<Component>(Component.class));
			setEnclosure(new ComponentEnclosure.Key(ScrapMenagerieFragmentLabel.class));
			setPainter(InstancePainter.Key.createKey(Painter.class, ScrapMenagerieFragmentLabelPainter.class));
		}

		@SuppressWarnings("unchecked")
		@Override
		public void assemble(final ScrapMenagerieFragmentLabel label)
		{
			super.assemble(label);

			label.text = (LabelComposite<Label, LabelPainter<Label>>) ComponentRegistry.getInstance().getComposite(text);
			label.addButton = (ButtonComposite<PushButton, PushButtonPainter>) ComponentRegistry.getInstance().getComposite(addButton);
			label.addButton.getPainter().borderPlugins.clearPlugins();
			label.addButton.getPainter().borderPlugins.insertPlugin(new BorderPlugin.Solid<PushButton>(BUTTON_OUTLINE_COLOR));
			label.addButton.getPainter().setTextPlugin(new LabelTextPlugin.Fixed(1, 9, 10, 10));
			label.removeButton = (ButtonComposite<PushButton, PushButtonPainter>) ComponentRegistry.getInstance().getComposite(removeButton);
			label.removeButton.getPainter().borderPlugins.clearPlugins();
			label.removeButton.getPainter().borderPlugins.insertPlugin(new BorderPlugin.Solid<PushButton>(BUTTON_OUTLINE_COLOR));
			label.removeButton.getPainter().setTextPlugin(new LabelTextPlugin.Fixed(3, 8, 10, 10));
			label.initialize();

			// TODO: wish it were easy to lighten this up a bit:
			try
			{
				TransactionRegistry.executeTask(new UserInterfaceTask() {
					@Override
					protected boolean execute()
					{
						GenericTransaction transaction = getTransaction(GenericTransaction.class);
						transaction.addAction(new ChangeTextDirective(label.text, "Typing Fragments"));
						transaction.addAction(new ChangeTextDirective(label.addButton, "+"));
						transaction.addAction(new ChangeTextDirective(label.removeButton, "-"));
						return true;
					}
				});
			}
			catch (ConcurrentAccessException e)
			{
				Log.out(Tag.CRITICAL, e, "Failed to initialize text in the fragment label.");
			}
		}
	}

	public static class Component extends AbstractComponent
	{
		@Override
		public Expansion getExpansion(Axis axis)
		{
			return Expansion.FIT;
		}
	}

	private class SizeHandler implements BoundedEntity
	{
		@Override
		public Expansion getExpansion(Axis axis)
		{
			switch (axis)
			{
				case H:
					return Expansion.FILL;
				case V:
					return Expansion.FIT;
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		@Override
		public int getPackedSize(Axis axis)
		{
			switch (axis)
			{
				case H:
					return text.getPackedSize(axis) + (2 * BUTTON_SPAN) + PAD;
				case V:
					return text.getPackedSize(axis);
				default:
					throw new UnknownEnumConstantException(axis);
			}
		}

		@Override
		public MaximumSize getMaxSize(Axis axis)
		{
			return new MaximumSize(getPackedSize(axis));
		}

	}

	public interface Painter
	{
		// marker
	}

	private static final int BUTTON_SPAN = InstancePainter.TextMetrics.INSTANCE.getSize("_", BoundsType.TEXT).width + 5;
	private static final int PAD = 4;

	private final Handler handler = new Handler();
	private final CollectionHandler collectionHandler = new CollectionHandler();

	private LabelComposite<Label, LabelPainter<Label>> text;
	private ButtonComposite<PushButton, PushButtonPainter> addButton;
	private ButtonComposite<PushButton, PushButtonPainter> removeButton;

	private ScrapMenagerieListViewport viewport;

	public ScrapMenagerieFragmentLabel(Component component)
	{
		super(component);
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	void initialize()
	{
		installHandler(handler);
		installSizeDelegate(new SizeHandler());
		addButton.installHandler(collectionHandler);
		removeButton.installHandler(collectionHandler);
		KeyEventDispatch.getInstance().installHandler(collectionHandler);

		viewport = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.FRAGMENT_LIST_ASSEMBLY).getViewport();
	}

	@DomainRole.Join(membership = DisplayBoundsDomain.class)
	public class Handler implements UserInterfaceHandler
	{
		public void mouseEvent(MouseAware.EventPass pass, PendingTransaction transaction)
		{ 
			if (pass.event().x() < addButton.getBounds().getPosition(Axis.H))
			{
				transaction.contribute(new MouseAware.Forward(text.getComponent()));
			}
			else if (pass.event().x() < removeButton.getBounds().getPosition(Axis.H))
			{
				transaction.contribute(new MouseAware.Forward(addButton.getComponent()));
			}
			else if (pass.event().x() < removeButton.getBounds().getExtent(Axis.H))
			{
				transaction.contribute(new MouseAware.Forward(removeButton.getComponent()));
			}
		}

		// WIP: not enforced b/c router instrumentation goes around it
		@InvocationConstraint(domains = RenderingDomain.class)
		public void paint(PaintIncludeNotification notification, PendingTransaction transaction)
		{
			transaction.contribute(new PaintIncludeNotification(text));
			transaction.contribute(new PaintIncludeNotification(addButton));
			transaction.contribute(new PaintIncludeNotification(removeButton));
		}

		public void sizeChanging(ComponentBoundsChangeDirective.Notification notification, PendingTransaction transaction)
		{
			int x = notification.getPosition(Axis.H);
			int y = notification.getPosition(Axis.V);
			int textWidth = notification.getSpan(Axis.H) - ((2 * BUTTON_SPAN) + PAD);
			int height = notification.getSpan(Axis.V);
			EnclosureBounds textBounds = new EnclosureBounds(x, y, textWidth, height);
			transaction.contribute(new ComponentBoundsChangeDirective(text.getComponent(), textBounds));

			x += textWidth;
			int yButton = y + ((height - BUTTON_SPAN) / 2);
			EnclosureBounds addButtonBounds = new EnclosureBounds(x, yButton, BUTTON_SPAN, BUTTON_SPAN);
			transaction.contribute(new ComponentBoundsChangeDirective(addButton.getComponent(), addButtonBounds));

			x += BUTTON_SPAN + PAD;
			EnclosureBounds removeButtonBounds = new EnclosureBounds(x, yButton, BUTTON_SPAN, BUTTON_SPAN);
			transaction.contribute(new ComponentBoundsChangeDirective(removeButton.getComponent(), removeButtonBounds));
		}
	}

	public class CollectionHandler implements UserInterfaceHandler
	{
		public void buttonPressed(ChangePressedStateDirective.Notification press, PendingTransaction transaction)
		{
			if (press.isPressed())
			{
				if (press.getButton() == addButton.getComponent())
				{
					transaction.contribute(new FragmentCollectionNotification(viewport, Command.ADD));
				}
				else
				{
					transaction.contribute(new FragmentCollectionNotification(viewport, Command.REMOVE));
				}
			}
		}

		public void keyPressed(KeyboardInputNotification key, PendingTransaction transaction)
		{
			switch (ScrapMenagerieKeyCommand.getCommand(key.event))
			{
				case ADD_FRAGMENT:
					transaction.contribute(new FragmentCollectionNotification(viewport, Command.ADD));
					break;
				case REMOVE_SELECTED_FRAGMENT:
					transaction.contribute(new FragmentCollectionNotification(viewport, Command.REMOVE));
					break;
			}
		}
	}
}
