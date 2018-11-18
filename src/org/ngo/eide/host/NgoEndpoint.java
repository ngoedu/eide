package org.ngo.eide.host;

import java.awt.Color;
import java.util.HashMap;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
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
import org.ngo.eide.commands.WriteConsoleCommand;
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
	public static final short NGOPUBLIC_EIDE_ID = 9;
	public static final short NGOPUBLIC_PAD_ID = 1;
	
	public static final short  NGONAT_EIDECLIENT_ID = 11;
	public static final short  NGONAT_GUIDER_ID = 12;
	public static final short  NGONAT_SWEB_ID = 15;
	
	private HashMap<String, Command> COMMAND_MAP = new HashMap<String, Command>();
	private EndpointSupport client;
	public final static NgoEndpoint instance = new NgoEndpoint();
	

	private NgoEndpoint() {
		//init command objects
		COMMAND_MAP.put(Command.EXIT, new ExitCommand());
		COMMAND_MAP.put(Command.ADDPROJ, new AddProjCommand());
		COMMAND_MAP.put(Command.MILESTONE, new MileStoneCommand());
		COMMAND_MAP.put(Command.MARK, new MarkCommand());
		COMMAND_MAP.put(Command.TEST, new TestCommand());
		COMMAND_MAP.put(Command.CONSOLE, new WriteConsoleCommand());	
	}
	
	private void workbenchActiveNotify() {
		DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
			public void run() {
				while (true) {
					Display display = PlatformUI.getWorkbench().getDisplay();
					if (display == null) {
						try {Thread.sleep(100);	} catch (InterruptedException e) {}
					} else {
						Shell shell = display.getActiveShell();
						if (shell != null) {
							//shell ready, send out notification to specific remote peer. 
							client.sendMessage(new EideResponse("$WBSREADY", "OK",NgoEndpoint.NGONAT_EIDECLIENT_ID, "workbench shell is ready").toString(), NgoEndpoint.NGOPUBLIC_EIDE_ID, NgoEndpoint.NGOPUBLIC_PAD_ID);
							return;
						} else {
							try {Thread.sleep(100);	} catch (InterruptedException e) {}
						}
					}
				}
			}
		});
	}


	@Override
	public void connected() {
		// A REGA from bridge will trigger callback's connected which indicated - 
		// that the aether channel between this peer and the remote bridge are connected and ready for DAT transmission.
		
		// notify when workbench is active
		workbenchActiveNotify();
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	public void sendMessageTo(String message, short remote) {
		client.sendMessage(message, NgoEndpoint.NGOPUBLIC_EIDE_ID , remote);
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
		DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
			public void run() {
				//exact cmd and parameters
				String eideCmd = message.substring(0, message.indexOf("="));
				String info = message.substring(eideCmd.length()+1);

				//check workbench status
				if (PlatformUI.getWorkbench().getDisplay().getShells() != null) {
					try {						
						Command cmdObj = COMMAND_MAP.get(eideCmd);
						int natId = cmdObj.execute(info);
						client.sendMessage(new EideResponse(eideCmd, "OK", natId, "Done").toString(), NgoEndpoint.NGOPUBLIC_EIDE_ID , NgoEndpoint.NGOPUBLIC_PAD_ID);
					} catch (Exception e) {
						client.sendMessage(new EideResponse(eideCmd, "EXCEPTION",NgoEndpoint.NGOPUBLIC_PAD_ID, e.getMessage()).toString() , NgoEndpoint.NGOPUBLIC_EIDE_ID , NgoEndpoint.NGOPUBLIC_PAD_ID);
					}
				//not ready.
				} else {
					client.sendMessage(EideResponse.EIDE_NOT_READY.toString(), NgoEndpoint.NGOPUBLIC_EIDE_ID , NgoEndpoint.NGOPUBLIC_PAD_ID);
				}
			}
		});
	}
	
	

	

	@Override
	public void error(String message) {
		// TODO Auto-generated method stub

	}

	public void setClient(EndpointSupport client) {
		this.client = client;
		
	}

}
