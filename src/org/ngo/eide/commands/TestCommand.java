package org.ngo.eide.commands;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

public class TestCommand implements Command {

	@Override
	public int execute(String message) throws Exception {
		
		// workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// root workspace
		IWorkspaceRoot wsroot = workspace.getRoot();
		// projects
		IProject[] projects = wsroot.getProjects();

		IProject project = wsroot.getProject("tms_sweb-a01");
		
		if (!project.exists())
	        project.create(null); //not sure if java project being created.
		if (!project.isOpen())
	        project.open(null);
		
		/*
		IResource[] rs = wsroot.members();
		try {
			IResource[] members = project.members();
			for (IResource member : members) {
				if (member instanceof IFolder) {
					//
				} else if (member instanceof IFile) {
					IFile file = (IFile) member;
					project.getFile("fn1.js")
				}
			}
		} catch (CoreException e) {
			
		}
		*/
		
		
		
		//create file at root folder
		IFile rootFile = project.getFile("fn.js");
		byte[] bytes1 = "function fn() {alert('a');}".getBytes();
		ByteArrayInputStream source1 = new ByteArrayInputStream(bytes1);						
		if (!rootFile.exists()) {
			rootFile.create(source1, true, null);
		} else {
			 rootFile.setContents(source1, true, true, null);
		}
		
		
		//create or set content of file on specific folder
		IFolder folder = project.getFolder("src");
		if (!folder.exists()) {
	        folder.create(false, true, null);
		}
						
		IFile targetFile = folder.getFile("test.js");
		byte[] bytes = "alert('a');".getBytes();
		ByteArrayInputStream source = new ByteArrayInputStream(bytes);
		if (targetFile.exists()) {
			targetFile.setContents(source, true, true, null);
		} else {
			targetFile.create(source, true, null);
		}

		return 0;
	}

}
