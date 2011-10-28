package org.hawkinssoftware.ui.util.scraps.fragment;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

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
