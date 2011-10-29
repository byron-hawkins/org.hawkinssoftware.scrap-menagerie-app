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

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

// assumes the row is in the scrollable content
/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class SetSelectedRowDirective extends UserInterfaceDirective
{
	
	/**
	 * DOC comment task awaits.
	 * 
	 * @author Byron Hawkins
	 */
	public final class Notification extends UserInterfaceNotification
	{
		public final int row;

		Notification(int row)
		{
			this.row = row;
		}
	}
	
	public final int row;

	public SetSelectedRowDirective(UserInterfaceActorDelegate actor, int row)
	{
		super(actor);
		this.row = row;
	}
	
	@Override
	public UserInterfaceNotification createNotification()
	{
		return new Notification(row);
	}
}
