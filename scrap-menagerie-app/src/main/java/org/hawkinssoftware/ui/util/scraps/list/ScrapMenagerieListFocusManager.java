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
package org.hawkinssoftware.ui.util.scraps.list;

import java.awt.Color;

import org.hawkinssoftware.azia.core.action.InstantiationTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.AbstractEventDispatch;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.cell.CellViewport;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.input.MouseAware;
import org.hawkinssoftware.azia.ui.paint.canvas.Canvas;
import org.hawkinssoftware.azia.ui.paint.canvas.Inset;
import org.hawkinssoftware.azia.ui.paint.plugin.BorderPlugin;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieKeyCommand;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ScrapMenagerieListFocusManager extends AbstractEventDispatch
{
	public static void install()
	{
		new InitializationTask().start();
	}

	public static ScrapMenagerieListFocusManager getInstance()
	{
		return INSTANCE;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class InitializationTask extends InstantiationTask.StandaloneInstantiationTask
	{
		InitializationTask()
		{
			super(SynchronizationRole.AUTONOMOUS, ScrapMenagerieListFocusManager.class.getSimpleName());
		}

		@Override
		protected void executeInTransaction()
		{
			INSTANCE = new ScrapMenagerieListFocusManager();
			INSTANCE.initialize();
		}
	}

	private static ScrapMenagerieListFocusManager INSTANCE;

	private static final Color FOCUS_COLOR = Color.black;
	private static final Color NON_FOCUS_COLOR = Color.gray;

	private final ViewportBorderPainter borderPlugin = new ViewportBorderPainter(2);

	private ScrapMenagerieListViewport historyViewport;
	private ScrapMenagerieListViewport fragmentViewport;

	@ValidateRead
	@ValidateWrite
	private ScrapMenagerieListViewport focused = null;

	@InvocationConstraint(domains = AssemblyDomain.class)
	private void initialize() 
	{
		historyViewport = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CLIP_LIST_ASSEMBLY).getViewport();
		fragmentViewport = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.FRAGMENT_LIST_ASSEMBLY).getViewport();

		historyViewport.installHandler(new FocusHandler(new SetFocusAction(historyViewport)));
		fragmentViewport.installHandler(new FocusHandler(new SetFocusAction(fragmentViewport)));

		historyViewport.getPainter().borderPlugins.insertPlugin(borderPlugin);
		fragmentViewport.getPainter().borderPlugins.insertPlugin(borderPlugin);
		focused = historyViewport;

		KeyEventDispatch.getInstance().installHandler(new KeyHandler());
	}

	public ScrapMenagerieListViewport getFocusedList()
	{
		return focused;
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class ViewportBorderPainter extends BorderPlugin<CellViewport>
	{
		public ViewportBorderPainter(int thickness)
		{
			super(Inset.homogenous(thickness));
		}

		@Override
		public void paintBorder(CellViewport component)
		{
			Canvas c = Canvas.get();

			if ((focused != null) && (focused.getComponent() == component))
			{
				c.pushColor(FOCUS_COLOR);
				BorderPlugin.Solid.paintBorder(c, inset);
			}
			else
			{
				c.pushColor(NON_FOCUS_COLOR);
				BorderPlugin.Solid.paintBorder(c, BorderPlugin.Solid.HAIRLINE);
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class KeyHandler implements UserInterfaceHandler
	{
		public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
		{
			if (ScrapMenagerieKeyCommand.getCommand(key.event) == ScrapMenagerieKeyCommand.TOGGLE_LIST_FOCUS)
			{
				if (focused != historyViewport)
				{
					transaction.contribute(new SetFocusAction(historyViewport));
				}
				else
				{
					transaction.contribute(new SetFocusAction(fragmentViewport));
				}
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class FocusHandler implements UserInterfaceHandler
	{
		private final SetFocusAction action;

		FocusHandler(SetFocusAction action)
		{
			this.action = action;

			ScrapMenagerieListFocusManager.this.installHandler(this);
		}

		public void mouseEvent(MouseAware.EventPass pass, PendingTransaction transaction)
		{
			if (pass.event().getButtonPress() != null)
			{
				transaction.contribute(action);
			}
		}

		public void setFocus(SetFocusAction focus)
		{
			focused = focus.activate;

			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(historyViewport.getComponent()));
			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(fragmentViewport.getComponent()));
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public class SetFocusAction extends UserInterfaceDirective
	{
		private final ScrapMenagerieListViewport activate;

		SetFocusAction(ScrapMenagerieListViewport activate)
		{
			super(ScrapMenagerieListFocusManager.this);

			this.activate = activate;
		}
	}
}
