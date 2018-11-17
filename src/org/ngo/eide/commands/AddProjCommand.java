package org.ngo.eide.commands;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.ngo.eide.host.NgoEndpoint;

public class AddProjCommand implements Command {

	@Override
	public int execute(String message) throws Exception{
		// workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// root workspace
		IWorkspaceRoot wsroot = workspace.getRoot();
		
		String projName = message.substring(message.lastIndexOf("\\")+1);
		IProject currProject = wsroot.getProject(projName);
		
		//add project into ws if not yet.
		if (!currProject.exists()) {
			addProjectToWS(message);
		}
		
		//open the target project
		if (!currProject.isOpen())
			currProject.open(null);
		
		//unrelated projects closure
		IProject[] allProjects = wsroot.getProjects();
		for(IProject p : allProjects) {
			if (p.getName() != currProject.getName() && p.isOpen()) {
				p.close(null);
			}
		}
	
		return NgoEndpoint.NGONAT_EIDECLIENT_ID;
	}
	
	
	private void addProjectToWS(String message) throws Exception{
	
		String projPath = message;
		IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(projPath+"\\.project"));

        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
        project.create(description, null);
        project.open(null);
        IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
            public String queryOverwrite(String file) {
                return ALL;
            }
        };

        ImportOperation importOperation = new ImportOperation(project.getFullPath(), new File(projPath),   FileSystemStructureProvider.INSTANCE, overwriteQuery);
        importOperation.setCreateContainerStructure(false);
        importOperation.setCreateLinks(false);
        importOperation.run(new NullProgressMonitor());
        
	}

}
