package org.haanstra.eclipse.plugin.handlers;

import java.util.Optional;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Select Javaproject of the selected element.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SelectJavaProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		Object selectedElement = getCurrentSelection(window);
				
		Optional<IJavaProject> project = getJavaProject(selectedElement);
		
		ISelectionProvider selProvider = getProjectsView().getSite().getSelectionProvider();
		if (project.isPresent()) {
			selProvider.setSelection(new StructuredSelection(project.get().getProject()));
		}
		return null;
	}
	
	Optional<IJavaProject> getJavaProject(Object selectedElement) {
		Optional<IJavaProject> project = Optional.empty();
		if (selectedElement instanceof IJavaElement) {
			IJavaElement javaElement = (IJavaElement) selectedElement;
			project = getJavaProject(javaElement);
		}
		return project;
	}

	Object getCurrentSelection(IWorkbenchWindow window) {
		IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();	
		return selection.getFirstElement();
	}
	
	IViewPart getProjectsView() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return page.findView("org.eclipse.ui.navigator.ProjectExplorer");
	}

	Optional<IJavaProject> getJavaProject(IJavaElement javaElement) {
		IJavaElement parentElement = javaElement.getParent();
		if (parentElement instanceof IJavaProject) {
			return Optional.of((IJavaProject) parentElement);
		} else if (parentElement == null) {
			return Optional.empty();
		} else {
			return getJavaProject(parentElement);
		}
	}
}
