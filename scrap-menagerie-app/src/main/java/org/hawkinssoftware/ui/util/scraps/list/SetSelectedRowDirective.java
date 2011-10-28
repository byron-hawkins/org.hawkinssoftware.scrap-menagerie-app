package org.hawkinssoftware.ui.util.scraps.list;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.core.action.UserInterfaceNotification;

// assumes the row is in the scrollable content
public class SetSelectedRowDirective extends UserInterfaceDirective
{
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
