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
package org.hawkinssoftware.ui.util.scraps;

import java.awt.Dimension;

import org.hawkinssoftware.azia.core.action.GenericTransaction;
import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask.ConcurrentAccessException;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.input.KeyboardInputEvent.State;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.DesktopWindow;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.button.ChangePushedStateDirective;
import org.hawkinssoftware.azia.ui.component.button.PushButton;
import org.hawkinssoftware.azia.ui.component.button.PushedStateHandler;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.component.transaction.window.SetVisibleAction;
import org.hawkinssoftware.azia.ui.component.transaction.window.WindowFocusAction;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ScrapMenagerieConsole
{
	private final DesktopWindow<ScrapMenagerieComponents.LayoutKey> window;
	private final PushButton pushpin;

	public ScrapMenagerieConsole(DesktopWindow<ScrapMenagerieComponents.LayoutKey> window)
	{
		this.window = window;
		window.frame.setAlwaysOnTop(true);

		VisibilityHandler visibilityHandler = new VisibilityHandler();
		window.installHandler(visibilityHandler);

		pushpin = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CONSOLE_PIN_ASSEMBLY).getComponent();
		pushpin.installHandler(visibilityHandler);
	}

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void assemble()
	{
		window.pack(new Dimension(400, 300), new Dimension(800, 600));
		window.center(0);

		KeyEventDispatch.getInstance().installHandler(new VisibilityHandler());
	}

	public void display(final boolean b)
	{
		try
		{
			TransactionRegistry.executeTask(new UserInterfaceTask() {
				@Override
				protected boolean execute()
				{
					GenericTransaction transaction = getTransaction(GenericTransaction.class);
					transaction.addAction(new SetVisibleAction(window, b));
					return true;
				}
			});
		}
		catch (ConcurrentAccessException e)
		{
			Log.out(Tag.CRITICAL, e, "Failed to open the console.");
		}
		// window.setVisible(b);
	}

	public TopTile<ScrapMenagerieComponents.LayoutKey> getTopTile()
	{
		return window.getTopTile();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class VisibilityHandler implements UserInterfaceHandler
	{
		private boolean metaOn = false;

		public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
		{
			if (ScrapMenagerieKeyCommand.META_KEYS.contains(key.event.key.logicalKey))
			{
				if (metaOn)
				{
					if (key.event.state == State.UP)
					{
						metaOn = false;

						if (!isPinned())
						{
							transaction.contribute(new SetVisibleAction(window, false));
						}
					}
				}
				else
				{
					if ((key.event.state == State.DOWN) && key.event.pressedLogicalKeys.containsAll(ScrapMenagerieKeyCommand.META_KEYS))
					{
						metaOn = true;

						if (!isPinned())
						{
							transaction.contribute(new SetVisibleAction(window, true));
						}
					}
				}
			}
			else
			{
				switch (ScrapMenagerieKeyCommand.getCommand(key.event))
				{
					case PIN_CONSOLE:
						transaction.contribute(new ChangePushedStateDirective(pushpin, !isPinned()));
						break;
					case QUIT:
						System.exit(0);
				}
			}
		}

		private boolean isPinned()
		{
			// WIP: as a secondary handler on the pushpin, will I have the proper lock for reading?
			return pushpin.getDataHandler(PushedStateHandler.KEY).isPushed();
		}

		public void windowFocusChanged(WindowFocusAction.Notification change, PendingTransaction transaction)
		{
			if (!(change.isFocused() || isPinned()))
			{
				transaction.contribute(new SetVisibleAction(window, false));
			}
		}
	}
}
