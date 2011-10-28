package org.hawkinssoftware.ui.util.scraps;

import java.util.EnumSet;
import java.util.Set;

import org.hawkinssoftware.azia.input.KeyboardInputEvent;
import org.hawkinssoftware.azia.input.KeyboardInputEvent.State;
import org.hawkinssoftware.azia.input.key.HardwareKey;

public enum ScrapMenagerieKeyCommand
{
	SELECTION_DOWN(HardwareKey.DOWN),
	SELECTION_UP(HardwareKey.UP),
	RE_COPY(HardwareKey.KEYBOARD6),
	ACTIVATE_SELECTED_FRAGMENT(HardwareKey.KEYBOARD0),
	ADD_FRAGMENT(HardwareKey.OEM_PLUS),
	REMOVE_SELECTED_FRAGMENT(HardwareKey.OEM_MINUS),
	TOGGLE_LIST_FOCUS(HardwareKey.KEYBOARD7),
	PIN_CONSOLE(HardwareKey.P),
	QUIT(HardwareKey.Q),
	NONE(null);

	final HardwareKey key;

	private ScrapMenagerieKeyCommand(HardwareKey key)
	{
		this.key = key;
	}

	public static final Set<HardwareKey> META_KEYS = EnumSet.of(HardwareKey.RCONTROL, HardwareKey.G);

	public static ScrapMenagerieKeyCommand getCommand(KeyboardInputEvent event)
	{
		if ((event.state == State.DOWN) && event.pressedKeys.containsAll(META_KEYS))
		{
			for (ScrapMenagerieKeyCommand command : ScrapMenagerieKeyCommand.values())
			{
				if (event.pressedKeys.contains(command.key))
				{
					return command;
				}
			}
		}
		return NONE;
	}
}
