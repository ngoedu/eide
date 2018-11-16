package org.ngo.eide.commands;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class MarkCommand implements Command {

	@Override
	public int execute(String message) throws Exception {
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window =  workbench == null ? null : workbench.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window == null ? null : window.getActivePage();

		IEditorPart editor =  activePage == null ? null : activePage.getActiveEditor();
		
		StyledText boxText  = getStyledText(editor);;
		Rectangle r0 = boxText.getClientArea();

		if (r0.width < 1 || r0.height < 1)
			return 0;

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
			
		return 0;
	}
	
	protected StyledText getStyledText(final IWorkbenchPart editorPart) {
		if (editorPart != null) {
			Object obj = editorPart.getAdapter(Control.class);
			if (obj instanceof StyledText)
				return (StyledText) obj;
		}

		return null;
	}

}
