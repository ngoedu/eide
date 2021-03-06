package org.ngo.eide.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.ngo.activator.NgoActivator;
import org.ngo.eide.host.EideResponse;
import org.ngo.eide.host.NgoEndpoint;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class NgoHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		
		NgoActivator.logInfo(this, "trigger tomcat deploy");
		
		/*MessageDialog.openInformation(
				window.getShell(),
				"NGO.eclipse.IDE",
				"Hello, NGO Eclipse");
		*/
		
		NgoEndpoint.instance.sendMessageTo(new EideResponse("$RESWEB", "NA", NgoEndpoint.NGONAT_SWEB_ID, "NA").toString(), NgoEndpoint.NGOPUBLIC_PAD_ID);

		
		return null;
	}
}
