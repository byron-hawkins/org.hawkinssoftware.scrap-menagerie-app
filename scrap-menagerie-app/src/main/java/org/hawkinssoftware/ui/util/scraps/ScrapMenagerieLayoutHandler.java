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

import java.util.HashSet;
import java.util.Set;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.component.ComponentRegistry;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel;
import org.hawkinssoftware.azia.ui.model.list.ListDataModel.ModelListDomain;
import org.hawkinssoftware.azia.ui.tile.LayoutRegion.TileLayoutDomain;
import org.hawkinssoftware.azia.ui.tile.TopTile;
import org.hawkinssoftware.azia.ui.tile.transaction.resize.ApplyLayoutTransaction;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.role.DomainRole;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
@DomainRole.Join(membership = ModelListDomain.class)
public class ScrapMenagerieLayoutHandler implements UserInterfaceHandler
{
	@InvocationConstraint(domains = AssemblyDomain.class)
	static void install()
	{
		ComponentRegistry.getInstance().getComposite(ScrapMenagerieComponents.CLIP_LIST_ASSEMBLY).getViewport().installHandler(INSTANCE);
	}

	public static ScrapMenagerieLayoutHandler getInstance()
	{
		return INSTANCE;
	}

	private static final ScrapMenagerieLayoutHandler INSTANCE = new ScrapMenagerieLayoutHandler();

	private final UpdateLayoutTask layoutTask = new UpdateLayoutTask();

	public void dataChanging(ListDataModel.DataChangeNotification dataChange, PendingTransaction transaction)
	{
		// update is occurring before the data is committed, so all data-related measurements are stale
		layoutTask.updateListLayout();
	}

	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	@DomainRole.Join(membership = TileLayoutDomain.class)
	private class UpdateLayoutTask extends UserInterfaceTask
	{
		private final Set<ScrapMenagerieComponents.LayoutKey> tilesToUpdate = new HashSet<ScrapMenagerieComponents.LayoutKey>();

		@Override
		protected boolean execute()
		{
			TopTile<ScrapMenagerieComponents.LayoutKey> topTile = ScrapMenagerieComponents.getInstance().getConsole().getTopTile();
			ApplyLayoutTransaction transaction = getTransaction(ApplyLayoutTransaction.class);
			for (ScrapMenagerieComponents.LayoutKey key : tilesToUpdate)
			{
				transaction.addRegion(topTile.getEntity(key));
			}
			transaction.beginAssembly();

			tilesToUpdate.clear();
			return true;
		}

		void updateListLayout()
		{
			try
			{
				if (tilesToUpdate.isEmpty())
				{
					// won't actually execute until the data change notifications have all been sent
					TransactionRegistry.executeTask(this);
				}

				tilesToUpdate.add(ScrapMenagerieComponents.LayoutKey.CLIP_LIST_PANEL);
			}
			catch (ConcurrentAccessException e)
			{
				Log.out(Tag.CRITICAL, e, "Failed to update the layout after a scrap list data change.");
			}
		}
	}
}
