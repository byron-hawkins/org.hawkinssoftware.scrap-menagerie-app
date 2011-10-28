package org.hawkinssoftware.ui.util.scraps;

import org.hawkinssoftware.azia.core.action.TransactionRegistry;
import org.hawkinssoftware.azia.core.action.UserInterfaceTask;
import org.hawkinssoftware.azia.core.log.AziaLogging.Tag;
import org.hawkinssoftware.azia.core.role.UserInterfaceDomains.AssemblyDomain;
import org.hawkinssoftware.azia.ui.AziaUserInterfaceInitializer;
import org.hawkinssoftware.rns.core.log.Log;
import org.hawkinssoftware.rns.core.role.CoreDomains.InitializationDomain;
import org.hawkinssoftware.rns.core.role.DomainRole;
import org.hawkinssoftware.ui.util.scraps.data.ClipboardHandler;
import org.hawkinssoftware.ui.util.scraps.data.ReCopyHandler;
import org.hawkinssoftware.ui.util.scraps.fragment.ScrapMenagerieFragmentProcessor;
import org.hawkinssoftware.ui.util.scraps.list.ScrapMenagerieListFocusManager;

@DomainRole.Join(membership = { AssemblyDomain.class, InitializationDomain.class })
public class ScrapMenagerieMain
{
	private final ScrapMenagerieAssemblyTask assemblyTask = new ScrapMenagerieAssemblyTask();

	private void start()
	{
		AziaUserInterfaceInitializer.initialize();
		Log.addTagFilter(Tag.NO_SUBSYSTEMS_UP_TO_DEBUG);
		Log.addTagFilter(org.hawkinssoftware.rns.core.util.RNSLogging.Tag.EVERYTHING);

		try
		{
			TransactionRegistry.executeTask(assemblyTask);
		}
		catch (UserInterfaceTask.ConcurrentAccessException e)
		{
			Log.out(Tag.CRITICAL, e, "Failed to assemble the Scrap Menagerie application.");
		}

		ClipboardHandler.install();
		ScrapMenagerieLayoutHandler.install();
		ScrapMenagerieFragmentProcessor.install();
		ReCopyHandler.install();
		ScrapMenagerieListFocusManager.install();

		ScrapMenagerieComponents.getInstance().setConsole(new ScrapMenagerieConsole(assemblyTask.window));
		ScrapMenagerieComponents.getInstance().getConsole().assemble();
		ScrapMenagerieComponents.getInstance().getConsole().display(true);
	}

	public static void main(String[] args)
	{
		try
		{
			ScrapMenagerieMain app = new ScrapMenagerieMain();
			app.start();
		}
		catch (Throwable t)
		{
			Log.out(Tag.CRITICAL, t, "Failed to start the scrap menagerie application.");
		}
	}
}
