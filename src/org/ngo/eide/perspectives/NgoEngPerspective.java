package org.ngo.eide.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IViewLayout;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;
import org.eclipse.ui.progress.UIJob;

import java.util.ArrayList;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;

/**
 * This class is meant to serve as an example for how various contributions are
 * made to a perspective. Note that some of the extension point id's are
 * referred to as API constants while others are hardcoded and may be subject to
 * change.
 */
public class NgoEngPerspective implements IPerspectiveFactory {

	protected static final String ID_NGO_PERSPECTIVE = "org.ngo.eide.perspectives.NgoEngPerspective";
	protected static final ArrayList<String> PRESERVED_PERSPECTIVE = new ArrayList<String>();
	
	static {
		// initialize preserved perspective
		PRESERVED_PERSPECTIVE.add(ID_NGO_PERSPECTIVE);
		//PRESERVED_PERSPECTIVE.add("org.eclipse.ui.resourcePerspective");
		//PRESERVED_PERSPECTIVE.add("org.eclipse.debug.ui.DebugPerspective");
		//PRESERVED_PERSPECTIVE.add("org.eclipse.ui.JavaBrowsingPerspective");
	}
	
	private IPageLayout factory;

	public NgoEngPerspective() {
		super();
	}

	public void createInitialLayout(IPageLayout factory) {
		this.factory = factory;
		addViews();
		addActionSets();
		addNewWizardShortcuts();
		addPerspectiveShortcuts();
		addViewShortcuts();
		//removeCoolBar();
		configurePreferences();
		configuPerspective();
	}

	
	private void configurePreferences() {
		ResourcesPlugin.getPlugin().getPluginPreferences().setValue("encoding", "UTF-8");
		EditorsPlugin.getDefault().getPreferenceStore().setValue("lineNumberRuler", "true");
		hideSearchField();
	}

	private void hideSearchField() {
		UIJob jobH = new UIJob("hide quick access") {
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				if (window == null)
					return Status.CANCEL_STATUS;
				if (window instanceof WorkbenchWindow) {
					MTrimBar topTrim = ((WorkbenchWindow) window).getTopTrim();
					for (MTrimElement element : topTrim.getChildren()) {
						if ("SearchField".equals(element.getElementId())) {
							Control contorl = (Control) element.getWidget();
							contorl.setVisible(false);
							break;
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		jobH.schedule(0L);
	}
	
	private void addViews() {
		// Creates the overall folder layout.
		// Note that each new Folder uses a percentage of the remaining
		// EditorArea.

		IFolderLayout bottom = factory.createFolder("bottomRight", // NON-NLS-1
				IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView(IConsoleConstants.ID_CONSOLE_VIEW);

		// bottom.addView("org.eclipse.debug.ui.DebugView"); //NON-NLS-1
		// bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		bottom.addView("org.eclipse.debug.ui.VariableView"); // NON-NLS-1

		IFolderLayout topLeft = factory.createFolder("topLeft", // NON-NLS-1
				IPageLayout.LEFT, 0.25f, factory.getEditorArea());
		topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);// .ID_RES_NAV);

		// topLeft.addView("org.eclipse.jdt.junit.ResultView"); //NON-NLS-1

		// factory.addFastView("org.eclipse.team.ccvs.ui.RepositoriesView",0.50f);
		// //NON-NLS-1
		// factory.addFastView("org.eclipse.team.sync.views.SynchronizeView",
		// 0.50f); //NON-NLS-1

		// set project layout not movable & closable
		IViewLayout projectLayout = factory.getViewLayout(IPageLayout.ID_PROJECT_EXPLORER);
		projectLayout.setCloseable(false);
		projectLayout.setMoveable(false);

		IViewLayout problemView = factory.getViewLayout(IPageLayout.ID_PROBLEM_VIEW);
		problemView.setCloseable(false);
		problemView.setMoveable(false);

		IViewLayout consoleView = factory.getViewLayout(IConsoleConstants.ID_CONSOLE_VIEW);
		consoleView.setCloseable(false);
		consoleView.setMoveable(false);

		IViewLayout variableView = factory.getViewLayout("org.eclipse.debug.ui.VariableView");
		variableView.setCloseable(false);
		variableView.setMoveable(false);
	}

	private void addActionSets() {
		factory.addActionSet("org.eclipse.debug.ui.launchActionSet"); // NON-NLS-1
		factory.addActionSet("org.eclipse.debug.ui.debugActionSet"); // NON-NLS-1
		// factory.addActionSet("org.eclipse.debug.ui.profileActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.debug.ui.JDTDebugActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.jdt.junit.JUnitActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.team.ui.actionSet"); //NON-NLS-1
		// factory.addActionSet("org.eclipse.team.cvs.ui.CVSActionSet");
		// //NON-NLS-1
		// factory.addActionSet("org.eclipse.ant.ui.actionSet.presentation");
		// //NON-NLS-1
		factory.addActionSet(JavaUI.ID_ACTION_SET);
		factory.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
		// factory.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET); //NON-NLS-1
	}

	private void addPerspectiveShortcuts() {
		// factory.addPerspectiveShortcut("org.eclipse.team.ui.TeamSynchronizingPerspective");
		// //NON-NLS-1
		// factory.addPerspectiveShortcut("org.eclipse.team.cvs.ui.cvsPerspective");
		// //NON-NLS-1
		factory.addPerspectiveShortcut("org.ngo.eide.perspectives.NgoEngPerspective"); // NON-NLS-1
		// factory.addPerspectiveShortcut("org.eclipse.ui.resourcePerspective");
		// //NON-NLS-1

	}

	private void addNewWizardShortcuts() {
		// factory.addNewWizardShortcut("org.eclipse.team.cvs.ui.newProjectCheckout");//NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");// NON-NLS-1
		factory.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");// NON-NLS-1
	}

	private void addViewShortcuts() {
		// factory.addShowViewShortcut("org.eclipse.ant.ui.views.AntView");
		// //NON-NLS-1
		// factory.addShowViewShortcut("org.eclipse.team.ccvs.ui.AnnotateView");
		// //NON-NLS-1
		factory.addShowViewShortcut("org.eclipse.pde.ui.DependenciesView"); // NON-NLS-1
		factory.addShowViewShortcut("org.eclipse.jdt.junit.ResultView"); // NON-NLS-1
		// factory.addShowViewShortcut("org.eclipse.team.ui.GenericHistoryView");
		// //NON-NLS-1
		factory.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		factory.addShowViewShortcut(JavaUI.ID_PACKAGES);
		factory.addShowViewShortcut(IPageLayout.ID_RES_NAV);
		factory.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		factory.addShowViewShortcut(IPageLayout.ID_OUTLINE);
	}

	private void removeCoolBar() {
		IWorkbenchWindow window = Workbench.getInstance().getActiveWorkbenchWindow();

		if (window instanceof WorkbenchWindow) {
			
			
			ICoolBarManager coolBarManager = null;
			if (((WorkbenchWindow) window).getCoolBarVisible()) {
				coolBarManager = ((WorkbenchWindow) window).getCoolBarManager2();
			}
			MenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
			
			// hide all menu items
			Menu menu = menuManager.getMenu();
			// you'll need to find the id for the item
			String menuItemIds[] = { //"file", 
					"edit", "navigate",
					"org.eclipse.search.searchActionSet", "project", "org.eclipse.ui.run", 
					"org.eclipse.ui.externaltools.ExternalToolsSet","additions",
							"window", "help" };

			for (String mi : menuItemIds) {
				IContributionItem m = menuManager.find(mi);
				// int controlIdx = menu.indexOf(mySaveAction.getId());
				if (m != null) {
					menuManager.remove(m);
					menuManager.update(true);
				}
			}
			
			// remove unwanted tool bar
			IContributionItem[] toolbars = coolBarManager.getItems();
			String toolbarIds[] = { //"group.file",
					//"org.eclipse.ui.workbench.file",
					"additions",
					"org.eclipse.debug.ui.launchActionSet", 
					"org.eclipse.oomph.ui.toolbars",
					"org.eclipse.oomph.setup.toolbar", 
					"org.eclipse.debug.ui.launch.toolbar",
					"org.eclipse.debug.ui.main.toolbar", 
					"org.eclipse.search.searchActionSet", 
					"group.nav",
					"org.eclipse.ui.workbench.navigate", 
					"group.editor", "group.help", 
					"org.eclipse.ui.workbench.help",
					"org.eclipse.mylyn.tasks.ui.trim.container", 
					"org.eclipse.oomph.setup.status" };
			for (String item : toolbarIds) {
				IContributionItem tb = coolBarManager.find(item);
				if (tb != null)
					//tb.setVisible(false);
					coolBarManager.remove(tb);
					coolBarManager.update(true);
			}
		}
	}

	private void configuPerspective() {
		// get all the perspectives
		Workbench workbench = Workbench.getInstance();
		IPerspectiveDescriptor[] prs = workbench.getPerspectiveRegistry().getPerspectives();
		workbench.getPerspectiveRegistry().setDefaultPerspective(ID_NGO_PERSPECTIVE);
		for (IPerspectiveDescriptor p : prs) {
			 if(!PRESERVED_PERSPECTIVE.contains(p.getId())) {
				 workbench.getPerspectiveRegistry().deletePerspective(p);
			 }	
		}	
		System.out.println("all other perspective removed.");
	}
}
