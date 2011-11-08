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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.input.KeyboardInputEvent;
import org.hawkinssoftware.azia.input.NativeInput;
import org.hawkinssoftware.azia.input.KeyboardInputEvent.State;
import org.hawkinssoftware.azia.input.key.LogicalKey;
import org.hawkinssoftware.rns.core.log.Log;

/**
 * Defines the set of key commands recognized by the application. The key assignments are loaded from the file
 * "config/shortcus.properties", which is expected to be available from a classpath root.
 * 
 * @author Byron Hawkins
 */
public enum ScrapMenagerieKeyCommand
{
	SELECTION_DOWN("MoveSelectionDown"),
	SELECTION_UP("MoveSelectionUp"),
	RE_COPY("ReCopy"),
	CHANGE_LEADING_CASE("ChangeLeadingCase"),
	REDUCE_TO_PLAIN_TEXT("ReduceToPlainText"),
	ACTIVATE_SELECTED_FRAGMENT("ActivateSelectedFragment"),
	ADD_FRAGMENT("NewFragment"),
	REMOVE_SELECTED_FRAGMENT("RemoveFragment"),
	TOGGLE_LIST_FOCUS("ToggleFocusedList"),
	PIN_CONSOLE("PinConsole"),
	QUIT("Quit"),
	NONE("");

	final String propertyName;

	private ScrapMenagerieKeyCommand(String propertyName)
	{
		this.propertyName = propertyName;
	}

	private static final String CUSTOM_PROPERTY_FILENAME = "shortcuts.properties";
	private static final String DEFAULT_PROPERTY_FILENAME = "default-shortcuts.properties";
	private static final String PROPERTY_FILEPATH = "config/";
	private static final String META_KEYS_PROPERTY_NAME = "MetaKeys";
	private static final Map<LogicalKey, ScrapMenagerieKeyCommand> commandKeys = new EnumMap<LogicalKey, ScrapMenagerieKeyCommand>(LogicalKey.class);

	public static final Set<LogicalKey> META_KEYS = EnumSet.noneOf(LogicalKey.class);

	public static ScrapMenagerieKeyCommand getCommand(KeyboardInputEvent event)
	{
		if ((event.state == State.DOWN) && event.pressedLogicalKeys.containsAll(META_KEYS))
		{
			ScrapMenagerieKeyCommand command = commandKeys.get(event.key.logicalKey);
			if (command != null)
			{
				return command;
			}
		}
		return NONE;
	}

	public static void initialize()
	{
		commandKeys.clear();

		try
		{
			String path = PROPERTY_FILEPATH + DEFAULT_PROPERTY_FILENAME;
			loadProperties(Thread.currentThread().getContextClassLoader().getResourceAsStream(path), path);
			Log.out(Tag.DEBUG, "Default shortcut keys loaded: %s", commandKeys);
		}
		catch (IOException e)
		{
			Log.out(Tag.CRITICAL, e, "Failed to load the default shortcut properties. Shortcut keys will be unavailable for this session.");
			return;
		}

		File userConfig = new File("./" + PROPERTY_FILEPATH + CUSTOM_PROPERTY_FILENAME);
		if (userConfig.exists())
		{
			try
			{
				Log.out(Tag.DEBUG, "Configuring custom shortcut keys from file %s.", userConfig.getAbsolutePath());
				loadProperties(new FileInputStream(userConfig), userConfig.getAbsolutePath());
			}
			catch (IOException e)
			{
				Log.out(Tag.CRITICAL, e, "Failed to load the shortcut property file %s. Custom shortcut keys will be unavailable for this session.",
						userConfig.getAbsolutePath());
			}
		}
		else
		{
			Log.out(Tag.DEBUG, "No custom shortcut keys configured in %s. Using all defaults.", userConfig.getAbsolutePath());
		}

		NativeInput.getInstance().setMetaKeys(META_KEYS);
	}

	private static void loadProperties(InputStream in, String propertyFilePath) throws IOException
	{
		Properties properties = new Properties();
		properties.load(in);

		Log.out(Tag.DEBUG, "Shortcut key properties: %s", properties);

		for (Map.Entry<Object, Object> entry : properties.entrySet())
		{
			String propertyName = (String) entry.getKey();
			String propertyValue = (String) entry.getValue();

			if (propertyName.equals(META_KEYS_PROPERTY_NAME))
			{
				META_KEYS.clear();

				String[] values = propertyValue.split(",");
				for (String value : values)
				{
					LogicalKey key = LogicalKey.valueOf(value);
					if (key == null)
					{
						Log.out(Tag.WARNING, "Warning: unknown key %s assigned to the meta key combination in %s", value, propertyFilePath);
					}
					META_KEYS.add(key);
				}
				if (propertyValue.contains(",,,"))
				{
					META_KEYS.add(LogicalKey.COMMA);
				}
			}
			else
			{
				ScrapMenagerieKeyCommand command = null;
				for (ScrapMenagerieKeyCommand nextCommand : ScrapMenagerieKeyCommand.values())
				{
					if (nextCommand.propertyName.equals(propertyName))
					{
						command = nextCommand;
						break;
					}
				}
				if (command == null)
				{
					Log.out(Tag.WARNING, "Warning: unknown key command %s found in %s", propertyName, propertyFilePath);
					continue;
				}

				LogicalKey key = LogicalKey.valueOf(propertyValue);
				if (key == null)
				{
					Log.out(Tag.WARNING, "Warning: attempt to bind key command %s to unknown key %s", command.name(), propertyValue);
					continue;
				}

				commandKeys.put(key, command);
			}
		}
	}
}
