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
package org.hawkinssoftware.ui.util.scraps.data;

import org.hawkinssoftware.azia.core.action.UserInterfaceActorDelegate;
import org.hawkinssoftware.azia.core.action.UserInterfaceDirective;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;

/**
 * DOC comment task awaits.
 * 
 * @author Byron Hawkins
 */
public class ReCopyCommand extends UserInterfaceDirective
{
	final ClipboardContents.Provider clipboardProvider;

	public ReCopyCommand(UserInterfaceActorDelegate actor, ClipboardContents.Provider clipboardProvider)
	{
		super(actor);
		this.clipboardProvider = clipboardProvider;
	}
}
