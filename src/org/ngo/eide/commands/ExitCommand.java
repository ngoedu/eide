package org.ngo.eide.commands;

import org.eclipse.ui.PlatformUI;
import org.ngo.eide.host.NgoEndpoint;

public class ExitCommand implements Command {

	@Override
	public int execute(String message) throws Exception{
		PlatformUI.getWorkbench().saveAllEditors(false);
		if (PlatformUI.getWorkbench().close()){
			//
		} else {
			throw new Exception("workbench can NOT be closed.");
		}
		return NgoEndpoint.NGONAT_EIDECLIENT_ID;
	}

}
