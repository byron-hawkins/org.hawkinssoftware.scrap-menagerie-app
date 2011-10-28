package org.hawkinssoftware.ui.util.scraps.fragment;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

public class FragmentCollectionNotification extends UserInterfaceNotification.Directed
{
	public enum Command
	{
		ADD,
		REMOVE;
	}

	public final Command command;

	public FragmentCollectionNotification(UserInterfaceActorDelegate actor, Command command)
	{
		super(actor);

		this.command = command;
	}
}
