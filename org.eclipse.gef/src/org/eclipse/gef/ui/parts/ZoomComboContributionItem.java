package org.eclipse.gef.ui.parts;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.*;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.util.Assert;

import org.eclipse.draw2d.ZoomListener;
import org.eclipse.draw2d.ZoomManager;

import org.eclipse.gef.ui.actions.GEFActionConstants;

/**
 * A ControlContribution that uses a {@link org.eclipse.swt.widgets.Combo} as its control
 * 
 * @author Eric Bordeau
 */
public class ZoomComboContributionItem 
	extends ContributionItem
	implements ZoomListener
{

private Combo combo;
private String initString = "8888%"; //$NON-NLS-1$
private ToolItem toolitem;
private ZoomManager zoomManager;
private IPartService service;
private final IPartListener partListener;

/**
 * Constructor for ComboToolItem.
 * @param id
 */
public ZoomComboContributionItem(IPartService partService) {
	super(GEFActionConstants.ZOOM_TOOLBAR_WIDGET);
	service = partService;
	Assert.isNotNull(partService);
	partService.addPartListener(partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
			setZoomManager((ZoomManager) part.getAdapter(ZoomManager.class));
		}
		public void partBroughtToTop(IWorkbenchPart p) { }
		public void partClosed(IWorkbenchPart p) { }
		public void partDeactivated(IWorkbenchPart p) { }
		public void partOpened(IWorkbenchPart p) { }
	});
}

void refresh() {
	if (combo == null || combo.isDisposed())
		return;
	if (zoomManager == null) {
		getCombo().setEnabled(false);
		getCombo().removeAll();
	} else {
		getCombo().setItems(getZoomManager().getZoomLevelsAsText());
		getCombo().setText(getZoomManager().getZoomAsText());
		getCombo().setEnabled(true);
	}
}

/**
 * Computes the width required by control
 * @param control The control to compute width * @return int The width required */
protected int computeWidth(Control control) {
	return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
}

/**
 * @see org.eclipse.jface.action.ControlContribution#createControl(Composite)
 */
protected Control createControl(Composite parent) {
	combo = new Combo(parent, SWT.DROP_DOWN);
	combo.addSelectionListener(new SelectionListener() {
		public void widgetSelected(SelectionEvent e) {
			handleWidgetSelected(e);
		}
		public void widgetDefaultSelected(SelectionEvent e) {
			handleWidgetDefaultSelected(e);
		}
	});
	
	// Initialize width of combo
	combo.setText(initString);
	toolitem.setWidth(computeWidth(combo));
	refresh();
	return combo;
}

/**
 * @see org.eclipse.jface.action.ContributionItem#dispose()
 */
public void dispose() {
	service.removePartListener(partListener);
	zoomManager.removeZoomListener(this);
	zoomManager = null;
	combo = null;
}

/**
 * The control item implementation of this <code>IContributionItem</code>
 * method calls the <code>createControl</code> framework method.
 * Subclasses must implement <code>createControl</code> rather than
 * overriding this method.
 * 
 * @param parent The parent of the control to fill
 */
public final void fill(Composite parent) {
	createControl(parent);
}

/**
 * The control item implementation of this <code>IContributionItem</code>
 * method throws an exception since controls cannot be added to menus.
 * 
 * @param parent The menu
 * @param index Menu index
 */
public final void fill(Menu parent, int index) {
	Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
}

/**
 * The control item implementation of this <code>IContributionItem</code>
 * method calls the <code>createControl</code> framework method to
 * create a control under the given parent, and then creates
 * a new tool item to hold it.
 * Subclasses must implement <code>createControl</code> rather than
 * overriding this method.
 * 
 * @param parent The ToolBar to add the new control to
 * @param index Index
 */
public void fill(ToolBar parent, int index) {
	toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
	Control control = createControl(parent);
	toolitem.setControl(control);	
}

/**
 * Returns the Combo
 * @return Combo The Combo */
public Combo getCombo() {
	return combo;
}

/**
 * Returns the zoomManager.
 * @return ZoomManager
 */
public ZoomManager getZoomManager() {
	return zoomManager;
}

/**
 * Sets the initString. This is the string used to initialize the size of the combo. The
 * combo's width is set to the width of this String. 
 * @param initString The initString to set
 */
public void setInitString(String initString) {
	this.initString = initString;
	
	//If the combo exists, update its width
	if (combo != null) {
		combo.setText(initString);
		toolitem.setWidth(computeWidth(combo));
		combo.removeAll();
	}	

}

/**
 * Sets the ZoomManager
 * @param zm The ZoomManager
 */
public void setZoomManager(ZoomManager zm) {
	if (zoomManager == zm)
		return;
	if (zoomManager != null)
		zoomManager.removeZoomListener(this);

	zoomManager = zm;
	refresh();

	if (zoomManager != null)
		zoomManager.addZoomListener(this);
}

/**
 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
 */
private void handleWidgetDefaultSelected(SelectionEvent event) {
	zoomManager.setZoom(combo.getText());
}

/**
 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
 */
private void handleWidgetSelected(SelectionEvent event) {
	zoomManager.setZoom(combo.getText());
}

/**
 * @see org.eclipse.draw2d.ZoomListener#zoomChanged(double)
 */
public void zoomChanged(double zoom) {
	refresh();
}

}