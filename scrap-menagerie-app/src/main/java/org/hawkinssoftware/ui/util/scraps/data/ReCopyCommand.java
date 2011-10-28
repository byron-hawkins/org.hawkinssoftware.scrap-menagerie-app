package org.hawkinssoftware.ui.util.scraps.data;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

public class ReCopyCommand extends UserInterfaceDirective
{
	final ClipboardContents.Provider clipboardProvider;

	public ReCopyCommand(UserInterfaceActorDelegate actor, ClipboardContents.Provider clipboardProvider)
	{
		super(actor);
		this.clipboardProvider = clipboardProvider;
	}
}
