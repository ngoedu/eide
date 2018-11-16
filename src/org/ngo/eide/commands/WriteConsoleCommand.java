package org.ngo.eide.commands;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.ngo.eide.host.NgoEndpoint;

public class WriteConsoleCommand implements Command {

	@Override
	public int execute(String message) throws Exception{
		String color = message.substring(1, message.indexOf("]"));
		String info = message.substring(message.indexOf("]")+1);
		writeToConsole(color, info);
		
		return NgoEndpoint.NGONAT_SWEB_ID;

	}
	
	private static final String NGO_CONSOLE_NAME = "NGO_Web_Console";
	/**
	 * https://wiki.eclipse.org/FAQ_How_do_I_find_the_active_workbench_page%3F
	 * @param message
	 * @throws Exception 
	 */
	
	private MessageConsole findConsole(String name) {
	      ConsolePlugin plugin = ConsolePlugin.getDefault();
	      IConsoleManager conMan = plugin.getConsoleManager();
	      IConsole[] existing = conMan.getConsoles();
	      for (int i = 0; i < existing.length; i++)
	         if (name.equals(existing[i].getName()))
	            return (MessageConsole) existing[i];
	      //no console found, so create a new one
	      MessageConsole myConsole = new MessageConsole(name, null);
	      conMan.addConsoles(new IConsole[]{myConsole});
	      return myConsole;
	}
	
	public void writeToConsole(String color, String message) throws Exception {
		 MessageConsole myConsole = findConsole(NGO_CONSOLE_NAME);
		 MessageConsoleStream out = myConsole.newMessageStream();
		 if (color.equals("red"))
			 out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		 else if (color.equals("green"))
			 out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
		 else if (color.equals("blue"))
			 out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
		 else if (color.equals("yellow"))
			 out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
		 else 
			 out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			 
		 out.println(message);
		 /*
		 IWorkbench wb = PlatformUI.getWorkbench();
		 IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
		  // on new versions it may need to be changed to:
		 IWorkbenchPage page = win.getActivePage();// obtain the active page
		 String id = NGO_CONSOLE_NAME;//IConsoleConstants.ID_CONSOLE_VIEW;
		 IConsoleView view = (IConsoleView) page.showView(id);
		 view.display(myConsole);
		 */
	}
}
