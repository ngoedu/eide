package org.ngo.eide.commands;

import org.eclipse.ui.PlatformUI;

public class ExitCommand implements Command {

	@Override
	public void execute(String message) throws Exception{
		PlatformUI.getWorkbench().saveAllEditors(false);
		if (PlatformUI.getWorkbench().close()){
			//
		} else {
			throw new Exception("workbench can NOT be closed.");
		}

	}

}
