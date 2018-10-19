package org.ngo.eide.commands;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.ngo.eide.host.NgoEndpoint;
import org.ngo.eide.milestones.Parser;
import org.ngo.eide.milestones.Revision;
import org.ngo.eide.milestones.SingleEntry;

public class MileStoneCommand implements Command {

	@Override
	public void execute(String message) throws Exception {
		
		Revision revision = Parser.Deserilize2(message);
		
		// workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// root workspace
		IWorkspaceRoot wsroot = workspace.getRoot();
		
		for (SingleEntry entry : revision.getFiles()) {
			buildMileStoneEntires(wsroot, revision.getProject(), entry);
		}
		//send back response to remote peer				

	}
	
	/**
	 * https://blog.csdn.net/dreajay/article/details/17119365
	 * @param wsroot
	 * @param projectName
	 * @param msEntry
	 * @throws CoreException
	 */
	protected void buildMileStoneEntires(IWorkspaceRoot wsroot, String projectName, SingleEntry msEntry) throws CoreException {
		
		//create or open a project operation
		IProject project = wsroot.getProject(projectName);
		if (!project.exists())
	        project.create(null); //not sure if java project being created.
		if (!project.isOpen())
	        project.open(null);
		
		IResource[] rs = wsroot.members();
		/*
		IFolder src = projects[0].getFolder("src");
		IFolder package1 = src.getFolder("package1");
		class1 = package1.getFile("class1.java");
		*/
		
		
		//create folder if needed
		IFolder folder = project.getFolder(msEntry.getPath());
		if (!folder.exists()) {
	        folder.create(true, true, null);
		}
	
		//create file or update file 
		IFile targetFile = folder.getFile(msEntry.getName());
		byte[] bytes = msEntry.getContent().getBytes();
		ByteArrayInputStream source = new ByteArrayInputStream(bytes);
		if (targetFile.exists()) {
			targetFile.setContents(source, true, true, null);
		} else {
			targetFile.create(source, true, null);
		}
	}

}
