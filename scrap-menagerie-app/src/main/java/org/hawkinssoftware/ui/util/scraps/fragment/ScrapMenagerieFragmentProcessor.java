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

import java.util.HashMap;
import java.util.Map;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.FlyweightCellDomain;
import org.hawkinssoftware.azia.input.KeyboardInputEvent.State;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.azia.ui.model.RowAddress;
import org.hawkinssoftware.azia.ui.model.RowAddress.Section;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.model.list.ListDataModelTransaction;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintInstanceDirective;
import org.hawkinssoftware.azia.ui.paint.transaction.repaint.RepaintRequestManager;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.rns.core.util.UnknownEnumConstantException;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieComponents;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieKeyCommand;
import org.hawkinssoftware.ui.util.scraps.data.ScrapMenagerieFragment;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListFocusManager;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListViewport;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = { ModelListDomain.class, FlyweightCellDomain.class })
public class ScrapMenagerieFragmentProcessor implements UserInterfaceHandler
{
	// WIP: R-N-S
	public static void install()
	{
		INSTANCE.initialize();
	}

	public static ScrapMenagerieFragmentProcessor getInstance()
	{
		return INSTANCE;
	}

	private static final ScrapMenagerieFragmentProcessor INSTANCE = new ScrapMenagerieFragmentProcessor();

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private interface TextChange
	{
		void apply(ScrapMenagerieFragment fragment);
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private static class Append implements TextChange
	{
		private final char c;

		Append(char c)
		{
			this.c = c;
		}

		@Override
		public void apply(ScrapMenagerieFragment fragment)
		{
			fragment.setText(fragment.getText() + c);
		}
	}

	private final DataChangeTask changeTask = new DataChangeTask();
	private final AddFragmentTask addTask = new AddFragmentTask();
	private final RemoveFragmentTask removeTask = new RemoveFragmentTask();

	private ScrapMenagerieListViewport viewport;
	private ListDataModel model;

	@InvocationConstraint(domains = AssemblyDomain.class)
	public void initialize()
	{
		ScrapMenagerieFragmentList list = ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.FRAGMENT_LIST_ASSEMBLY);
		viewport = list.getViewport();
		viewport.installHandler(this);
		model = list.getModel();

		KeyEventDispatch.getInstance().installHandler(this);
	}

	public void activateFragment(ActivateFragmentNotification activation, PendingTransaction transaction)
	{
		changeTask.transformer.activations.put(activation.id, activation.activate);
		changeTask.schedule();
	}

	public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
	{
		if (ScrapMenagerieKeyCommand.getCommand(key.event) == ScrapMenagerieKeyCommand.ACTIVATE_SELECTED_FRAGMENT)
		{
			if (ScrapMenagerieListFocusManager.getInstance().getFocusedList() == viewport)
			{
				int selectedRow = viewport.selection.getSelectedRow();
				if (selectedRow >= 0)
				{
					RowAddress address = viewport.createAddress(selectedRow, Section.SCROLLABLE);
					ScrapMenagerieFragment fragment = (ScrapMenagerieFragment) model.get(address);
					changeTask.transformer.activations.put(fragment.id, !fragment.isActive());
					changeTask.schedule();
					RepaintRequestManager.requestRepaint(viewport.getCellPainter().createRepaintRequest(address));
				}
			}
		}
		else if ((key.event.state == State.DOWN) && key.event.isCharacter())
		{
			changeTask.transformer.textChange = new Append(key.event.getCharacter()); 
			changeTask.schedule();
		}
	}

	public void collectionChange(FragmentCollectionNotification change, PendingTransaction transaction)
	{
		switch (change.command)
		{
			case ADD:
				addTask.schedule();
				break;
			case REMOVE:
				removeTask.schedule();
				break;
			default:
				throw new UnknownEnumConstantException(change.command);
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class DataTransformer implements ListDataModel.DataChange<ScrapMenagerieFragment>
	{
		private final Map<Integer, Boolean> activations = new HashMap<Integer, Boolean>();
		private TextChange textChange = null;

		@Override
		public void applyChange(Section section, int row, ScrapMenagerieFragment data)
		{
			Boolean activate = activations.get(data.id);
			if (activate != null)
			{
				data.setActive(activate);
			}

			if (data.isActive() && (textChange != null))
			{
				textChange.apply(data);
				RepaintRequestManager.requestRepaint(viewport.getCellPainter().createRepaintRequest(viewport.createAddress(row, section)));
			}
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = ModelListDomain.class)
	private abstract class FragmentListTask extends UserInterfaceTask
	{
		boolean pending = false; 

		void schedule()
		{
			if (pending)
			{
				return;
			}
			pending = true;

			try
			{
				TransactionRegistry.executeTask(this);
			}
			catch (ConcurrentAccessException e)
			{
				Log.out(Tag.CRITICAL, "Failed to execute the fragment data change task.");
			}
		} 
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class DataChangeTask extends FragmentListTask
	{
		DataTransformer transformer = new DataTransformer();

		@Override
		protected boolean execute()
		{
			ListDataModelTransaction transaction = getTransaction(ListDataModelTransaction.class);
			ListDataModel.Session session = model.createSession(transaction);
			session.changeAllRows(transformer);
			transformer = new DataTransformer();
			pending = false;
			return true;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class AddFragmentTask extends FragmentListTask
	{
		@Override
		protected boolean execute()
		{
			ListDataModelTransaction transaction = getTransaction(ListDataModelTransaction.class);
			ListDataModel.Session session = model.createSession(transaction);
			session.add(new ScrapMenagerieFragment());
			pending = false;
			return true;
		}
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	private class RemoveFragmentTask extends FragmentListTask
	{
		private int row;

		void schedule()
		{
			row = viewport.selection.getSelectedRow();
			if (row < 0)
			{
				Log.out(Tag.NIT, "Skipping fragment removal because no row is selected.");
				return;
			}

			super.schedule();
		}

		@Override
		protected boolean execute()
		{
			ListDataModelTransaction transaction = getTransaction(ListDataModelTransaction.class);
			ListDataModel.Session session = model.createSession(transaction);
			session.remove(row);
			RepaintRequestManager.requestRepaint(new RepaintInstanceDirective(viewport.getComponent()));
			pending = false;
			return true;
		}
	}
}
