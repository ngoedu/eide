package org.ngo.eide.commands;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
        
        return NgoEndpoint.NGONAT_EIDECLIENT_ID;
        
	}

}
