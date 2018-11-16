package org.ngo.eide.host;

import java.awt.Color;
import java.util.HashMap;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.ngo.eide.commands.AddProjCommand;
import org.ngo.eide.commands.Command;
import org.ngo.eide.commands.ExitCommand;
import org.ngo.eide.commands.MarkCommand;
import org.ngo.eide.commands.MileStoneCommand;
import org.ngo.eide.commands.TestCommand;
import org.ngo.ether.endpoint.EndpointCallback;
import org.ngo.ether.endpoint.EndpointSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * https://sourceforge.net/p/editbox/code/HEAD/tree/plugin/pm.eclipse.editbox/Trunk/src/pm/eclipse/editbox/impl/BoxDecoratorImpl.java#l158
 * 
 * @author Administrator
 *
 */
public class NgoEndpoint implements EndpointCallback {

	private final static Logger LOGGER = LoggerFactory.getLogger(NgoEndpoint.class);
	public static final short NGO_ID = 9;
	public static final short NGO_REMOTE_PEER_ID = 1;
	
	private HashMap<String, Command> COMMAND_MAP = new HashMap<String, Command>();
	private EndpointSupport client;
	private IWorkbench workbench;
	public final static NgoEndpoint instance = new NgoEndpoint();
	
	protected static final String ID_NGO_PERSPECTIVE = "org.ngo.eide.perspectives.NgoEngPerspective";
	


	private NgoEndpoint() {
		
		//init command objects
		COMMAND_MAP.put(Command.EXIT, new ExitCommand());
		COMMAND_MAP.put(Command.ADDPROJ, new AddProjCommand());
		COMMAND_MAP.put(Command.MILESTONE, new MileStoneCommand());
		COMMAND_MAP.put(Command.MARK, new MarkCommand());
		COMMAND_MAP.put(Command.TEST, new TestCommand());

		// loop until the workbench is instantiated
		while (true) {
			workbench = PlatformUI.getWorkbench();
			if (workbench != null) {				
				break;
			} else
				try {
					LOGGER.debug("workbench is not available, loop again...");
					Thread.sleep(500);
				} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void connected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	
	/**
	*	https://stackoverflow.com/questions/33010029/eclipse-plugin-copy-file
	*	http://anglemoshao.iteye.com/blog/1714333
	*	https://stackoverflow.com/questions/8786089/how-to-get-path-of-current-selected-file-in-eclipse-plugin-development
	*	 
	*/
	@SuppressWarnings("restriction")
	@Override
	public void messageReceived(String message) {
		
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		IWorkbenchWindow window0 = windows[0];
		
		if (window0 != null) {
				DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
					public void run() {
						String eideCmd = message.substring(0, message.indexOf("="));
						String info = message.substring(eideCmd.length()+1);
						
						try {
							writeToConsole(eideCmd);
							writeToConsole(info);
							Command cmdObj = COMMAND_MAP.get(eideCmd);
							cmdObj.execute(info);
							client.sendMessage(new EideResponse(eideCmd, "OK", "Done").toString(), NgoEndpoint.NGO_ID , NgoEndpoint.NGO_REMOTE_PEER_ID);
						} catch (Exception e) {
							client.sendMessage(new EideResponse(eideCmd, "EXCEPTION",e.getMessage()).toString() , NgoEndpoint.NGO_ID , NgoEndpoint.NGO_REMOTE_PEER_ID);
						}
					}
				});
			
		} else {
			client.sendMessage(EideResponse.EIDE_NOT_READY.toString(), NgoEndpoint.NGO_ID , NgoEndpoint.NGO_REMOTE_PEER_ID);
		}
	}
	
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

	private static final String NGO_CONSOLE_NAME = "NGO_WebConsole";
	/**
	 * https://wiki.eclipse.org/FAQ_How_do_I_find_the_active_workbench_page%3F
	 * @param message
	 * @throws Exception 
	 */
	public void writeToConsole(String message) throws Exception {
		 MessageConsole myConsole = findConsole(NGO_CONSOLE_NAME);
		 MessageConsoleStream out = myConsole.newMessageStream();
		 if (message.startsWith("$"))
			 out.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
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

	@Override
	public void error(String message) {
		// TODO Auto-generated method stub

	}

	public void setClient(EndpointSupport client) {
		this.client = client;
		
	}

}
