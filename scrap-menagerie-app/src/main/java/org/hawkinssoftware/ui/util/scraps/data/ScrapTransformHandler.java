package org.hawkinssoftware.ui.util.scraps.data;

import java.io.File;

import org.hawkinssoftware.azia.core.action.UserInterfaceTransaction.ActorBasedContributor.PendingTransaction;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.input.clipboard.ClipboardContents;
import org.hawkinssoftware.azia.ui.component.UserInterfaceHandler;
import org.hawkinssoftware.azia.ui.component.transaction.clipboard.ClipboardEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyEventDispatch;
import org.hawkinssoftware.azia.ui.component.transaction.key.KeyboardInputNotification;
import org.hawkinssoftware.rns.core.publication.InvocationConstraint;
import org.hawkinssoftware.rns.core.validation.ValidateRead;
import org.hawkinssoftware.rns.core.validation.ValidateWrite;
import org.hawkinssoftware.ui.util.scraps.ScrapMenagerieKeyCommand;

@ValidateRead
@ValidateWrite
public class ScrapTransformHandler implements UserInterfaceHandler
{
	@InvocationConstraint(domains = AssemblyDomain.class)
	public static void install()
	{
		KeyEventDispatch.getInstance().installHandler(INSTANCE);
	}

	public static ScrapTransformHandler getInstance()
	{
		return INSTANCE;
	}

	private static final ScrapTransformHandler INSTANCE = new ScrapTransformHandler();

	public void keyEvent(KeyboardInputNotification key, PendingTransaction transaction)
	{
		switch (ScrapMenagerieKeyCommand.getCommand(key.event))
		{
			case CHANGE_LEADING_CASE:
				changeLeadingCase(transaction);
				break;
			case REDUCE_TO_PLAIN_TEXT:
				reduceToPlainText(transaction);
				break;
		}
	}

	private void changeLeadingCase(PendingTransaction transaction)
	{
		ClipboardContents currentClip = ClipboardHandler.getInstance().getCurrentClip();
		if (currentClip.hasImage() || currentClip.hasFiles())
		{
			return;
		}
		if ((currentClip.text.length() == 0) || !Character.isLetter(currentClip.text.charAt(0)))
		{
			return;
		}

		char firstLetter = currentClip.text.charAt(0);
		if (Character.isUpperCase(firstLetter))
		{
			firstLetter = Character.toLowerCase(firstLetter);
		}
		else
		{
			firstLetter = Character.toUpperCase(firstLetter);
		}
		String transformedText = String.valueOf(firstLetter);
		if (currentClip.text.length() > 1)
		{
			transformedText += currentClip.text.substring(1);
		}
		ClipboardContents.Provider clipboardProvider = ClipboardContents.createPlainTextProvider(transformedText);
		transaction.contribute(new TransformScrapCommand(ClipboardEventDispatch.getInstance(), clipboardProvider));
	}

	private void reduceToPlainText(PendingTransaction transaction)
	{
		ClipboardContents currentClip = ClipboardHandler.getInstance().getCurrentClip();

		String plainText;
		if (currentClip.hasFiles())
		{
			StringBuilder buffer = new StringBuilder();
			for (File file : currentClip.files)
			{
				buffer.append(file.getName());
				buffer.append(", ");
			}
			buffer.setLength(buffer.length() - 2);
			plainText = buffer.toString();
		}
		else
		{
			plainText = currentClip.text;
		}

		ClipboardContents.Provider clipboardProvider = ClipboardContents.createPlainTextProvider(plainText);
		transaction.contribute(new TransformScrapCommand(ClipboardEventDispatch.getInstance(), clipboardProvider));
	}
}
