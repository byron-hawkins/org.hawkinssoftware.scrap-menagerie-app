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

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ActivateFragmentNotification extends UserInterfaceNotification.Directed
{
	public final int id;;
	public final boolean activate;

	public ActivateFragmentNotification(UserInterfaceActorDelegate actor, int id, boolean activate)
	{
		super(actor);

		this.id = id;
		this.activate = activate;
	}
}
