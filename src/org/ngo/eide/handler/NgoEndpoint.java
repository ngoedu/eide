package org.ngo.eide.handler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.registry.PerspectiveDescriptor;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;
import org.ngo.eide.NgoStartup;
import org.ngo.ether.endpoint.EndpointCallback;
import org.ngo.ether.endpoint.EndpointSupport;
import org.ngo.milestone.MileStoneEntry;
import org.ngo.milestone.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;

/**
 * https://sourceforge.net/p/editbox/code/HEAD/tree/plugin/pm.eclipse.editbox/Trunk/src/pm/eclipse/editbox/impl/BoxDecoratorImpl.java#l158
 * 
 * @author Administrator
 *
 */
public class NgoEndpoint implements EndpointCallback {

	private final static Logger LOGGER = LoggerFactory.getLogger(NgoEndpoint.class);
	public static final short ngoID = 9;
	protected static final String ID_NGO_PERSPECTIVE = "org.ngo.eide.perspectives.NgoEngPerspective";
	
	private EndpointSupport client;
	public final static NgoEndpoint instance = new NgoEndpoint();
	protected static final String JAVA = "java"; //$NON-NLS-1$
	protected static final String JAVA_EXTENSION = ".java"; //$NON-NLS-1$
	protected static final String LAUNCHCONFIGURATIONS = "launchConfigurations"; //$NON-NLS-1$
	protected static final String LAUNCH_EXTENSION = ".launch"; //$NON-NLS-1$

	private IWorkbench workbench;

	public NgoEndpoint() {

		while (true) {
			// loop until the workbench is instantiated
			workbench = PlatformUI.getWorkbench();
			if (workbench != null) {				
				break;
			} else
				try {
					LOGGER.debug("workbench is not available, loop again...");
					Thread.sleep(1000);
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

	@Override
	public void messageReceived(String message) {
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		IWorkbenchWindow window0 = windows[0];

		// workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// root workspace
		IWorkspaceRoot wsroot = workspace.getRoot();
		// projects
		IProject[] projects = wsroot.getProjects();

		if (window0 != null && message.equalsIgnoreCase("$EXIT")) {
			DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
				public void run() {
					PlatformUI.getWorkbench().saveAllEditors(false);
					if (PlatformUI.getWorkbench().close())
						client.sendMessage("<EIDE status='closed'/>",ngoID , (short)1);
				}
			});
		}
		
		if (window0 != null && message.startsWith("$ADDPROJ")) {
			DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
				public void run() {

					try {
						String PROJECT_PATH = message.split("=")[1];
						IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(new Path(PROJECT_PATH+"\\.project"));

				        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
				        project.create(description, null);
				        project.open(null);
				        IOverwriteQuery overwriteQuery = new IOverwriteQuery() {
				            public String queryOverwrite(String file) {
				                return ALL;
				            }
				        };

				        ImportOperation importOperation = new ImportOperation(project.getFullPath(), new File(PROJECT_PATH),   FileSystemStructureProvider.INSTANCE, overwriteQuery);
				        importOperation.setCreateContainerStructure(false);
				        importOperation.setCreateLinks(false);
				        importOperation.run(new NullProgressMonitor());
					}
					catch (Exception e) {
						client.sendMessage("<EIDE proj='add-failed'/>",ngoID , (short)1);
						return;
					}
					
					client.sendMessage("<EIDE proj='added'/>",ngoID , (short)1);
						
				}
			});
		}
		
		//https://stackoverflow.com/questions/33010029/eclipse-plugin-copy-file
		if (window0 != null && message.startsWith("$MILESTONE:")) {

			MileStoneEntry msEntry = Parser.parseMileStone(message.replace("$MILESTONE:", ""));
			
			DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						//create or open a project
						IProject project = wsroot.getProject(msEntry.getProject());
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
						IFile fileToUpdate = folder.getFile(msEntry.getFile());
						byte[] bytes = msEntry.getContent().getBytes();
						ByteArrayInputStream source = new ByteArrayInputStream(bytes);
						if (fileToUpdate.exists()) {
							fileToUpdate.setContents(source, true, true, null);
						} else {
							fileToUpdate.create(source, true, null);
						}
						
						//send back response to remote peer
						client.sendMessage("<EIDE status='msdone'/>",ngoID , (short)1);
					} catch (Exception e) {
						e.printStackTrace();
						client.sendMessage("<EIDE status='msfailed' error="+e.getMessage()+"/>",ngoID , (short)1);
					}
				}
			});

		}
		
		/**
		 * https://stackoverflow.com/questions/8786089/how-to-get-path-of-current-selected-file-in-eclipse-plugin-development
		 */
		
		if (window0 != null && message.startsWith("mark:")) {

			DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {
				public void run() {
					
					IWorkbench workbench = PlatformUI.getWorkbench();
					IWorkbenchWindow window =  workbench == null ? null : workbench.getActiveWorkbenchWindow();
					IWorkbenchPage activePage = window == null ? null : window.getActivePage();

					IEditorPart editor =  activePage == null ? null : activePage.getActiveEditor();
					
					StyledText boxText  = getStyledText(editor);;
					Rectangle r0 = boxText.getClientArea();

					if (r0.width < 1 || r0.height < 1)
						return;

					int xOffset = boxText.getHorizontalPixel();
					int yOffset = boxText.getTopPixel();

					Image newImage = new Image(null, r0.width, r0.height);
					GC gc = new GC(newImage);

					// fill background
					Color bc= new Color(null, new RGB(181,230,29));
					if (bc!=null){
						Rectangle rec = newImage.getBounds();		
						//fillRectangle(bc, gc, rec.x, rec.y, rec.width, rec.height);
						gc.fillGradientRectangle(rec.x, rec.y, rec.width, rec.height, false);
						boxText.setBackgroundImage(newImage);
					}
				}
			});

		}

		// HXY: implement debug/run config
		if (message.equalsIgnoreCase("config")) {
			try {
				// 10. launch configuration
				IProject pro = ResourcesPlugin.getWorkspace().getRoot().getProject("test");
				if (pro.exists()) {
					// pro.delete(true, true, null);
				}
				// create project and import source
				// IJavaProject fJavaProject =
				// JavaProjectHelper.createJavaProject("DebugTests", "bin");
				// IPackageFragmentRoot src =
				// JavaProjectHelper.addSourceContainer(fJavaProject, "src");

				IFolder folder = pro.getFolder("launchConfigurations");
				if (folder.exists()) {
					folder.delete(true, null);
				}
				folder.create(true, true, null);

				// delete any existing launch configs
				ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations();
				for (int i = 0; i < configs.length; i++) {
					configs[i].delete();
				}

				createLaunchConfiguration(JavaCore.create(projects[0]), "SimpleTests");

			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	
	protected StyledText getStyledText(final IWorkbenchPart editorPart) {
		if (editorPart != null) {
			Object obj = editorPart.getAdapter(Control.class);
			if (obj instanceof StyledText)
				return (StyledText) obj;
		}

		return null;
	}

	/**
	 * Creates a shared launch configuration for the type with the given name.
	 */
	protected void createLaunchConfiguration(IJavaProject project, String mainTypeName) throws Exception {
		ILaunchConfigurationType type = getLaunchManager()
				.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy config = type.newInstance(project.getProject().getFolder(LAUNCHCONFIGURATIONS),
				mainTypeName);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainTypeName);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getElementName());
		// use 'java' instead of 'javaw' to launch tests (javaw is problematic
		// on JDK1.4.2)
		Map map = new HashMap(1);
		map.put(IJavaLaunchConfigurationConstants.ATTR_JAVA_COMMAND, JAVA);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP, map);
		config.doSave();
	}

	/**
	 * Returns the launch manager
	 * 
	 * @return launch manager
	 */
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	public void error(String message) {
		// TODO Auto-generated method stub

	}

	public void setClient(EndpointSupport client) {
		this.client = client;
		
	}

}
