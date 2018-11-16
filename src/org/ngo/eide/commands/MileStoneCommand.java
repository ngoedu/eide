package org.ngo.eide.commands;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.ngo.eide.host.NgoEndpoint;
import org.ngo.eide.milestones.Parser;
import org.ngo.eide.milestones.Revision;
import org.ngo.eide.milestones.SingleEntry;

public class MileStoneCommand implements Command {

	@Override
	public int execute(String message) throws Exception {
		
		Revision revision = Parser.Deserilize2(message);
		
		// workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// root workspace
		IWorkspaceRoot wsroot = workspace.getRoot();
		
		for (SingleEntry entry : revision.getFiles()) {
			buildMileStoneEntires(wsroot, revision.getProject(), entry);
		}
		//send back response to remote peer				
		return NgoEndpoint.NGONAT_GUIDER_ID;
	}
	
	/**
	 * https://blog.csdn.net/dreajay/article/details/17119365
	 * @param wsroot
	 * @param projectName
	 * @param msEntry
	 * @throws CoreException
	 */
	protected void buildMileStoneEntires(IWorkspaceRoot wsroot, String projectName, SingleEntry msEntry) throws Exception {
		
		//create or open a project operation
		IProject project = wsroot.getProject(projectName);
		if (!project.exists())
	        project.create(null); //not sure if java project being created.
		if (!project.isOpen())
	        project.open(null);
		
		//check folder
		if (msEntry.getPath().equalsIgnoreCase(msEntry.getName())) {
			buildRootEntry(project, msEntry.getName(), msEntry);
		} else {
			String folderName = msEntry.getPath().replaceAll(msEntry.getName(), "");
			//create folder if needed
			IFolder folder = createFolderRecusive(folderName, project);
			
			//create file or update file 
			IFile targetFile = folder.getFile(msEntry.getName());
			byte[] bytes = msEntry.getContent().getBytes("UTF-8");
			ByteArrayInputStream source = new ByteArrayInputStream(bytes);
			if (targetFile.exists()) {
				targetFile.setContents(source, true, true, null);
			} else {
				targetFile.create(source, true, null);
			}	
		}	
	}
	
	private IFolder createFolderRecusive(String folderName, IProject project) throws Exception {
		String[] folders = folderName.split("\\\\");
		IFolder pFolder = null;
		for(int i=0; i< folders.length; i++) {
			if (i==0) {
				pFolder = project.getFolder(folders[0]);		
			} else {
				pFolder = pFolder.getFolder(folders[i]);
			}
			
			if (!pFolder.exists()) {
				pFolder.create(true, true, null);
			}
		}
		return pFolder;
	}
	
	private void buildRootEntry(IProject project, String fileName, SingleEntry entry) throws Exception {
		IFile file = project.getFile(fileName);
		byte[] bytes = entry.getContent().getBytes("UTF-8");
		ByteArrayInputStream source = new ByteArrayInputStream(bytes);
		if (file.exists()) {
			file.setContents(source, true, true, null);
		} else {
			file.create(source, true, null);
		}
	}

}
