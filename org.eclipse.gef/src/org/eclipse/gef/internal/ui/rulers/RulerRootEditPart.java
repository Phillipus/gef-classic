/*******************************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.internal.ui.rulers;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;

import org.eclipse.gef.*;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * @author Pratik Shah
 */
public class RulerRootEditPart
	extends AbstractGraphicalEditPart
	implements RootEditPart
{
	
private boolean horizontal;	
private EditPart contents;
private EditPartViewer viewer;

public RulerRootEditPart(boolean isHorzontal) {
	super();
	horizontal = isHorzontal;
}

protected void addChildVisual(EditPart childEditPart, int index) {
	IFigure child = ((GraphicalEditPart)childEditPart).getFigure();
	getViewport().setContents(child);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
 */
protected IFigure createFigure() {
	return new RulerViewport(horizontal);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
 */
protected void createEditPolicies() {
}

/* (non-Javadoc)
 * @see org.eclipse.gef.RootEditPart#getContents()
 */
public EditPart getContents() {
	return contents;
}

/* (non-Javadoc)
 * @see org.eclipse.gef.EditPart#getRoot()
 */
public RootEditPart getRoot() {
	return this;
}

public EditPartViewer getViewer() {
	return viewer;
}

protected void removeChildVisual(EditPart childEditPart) {
	getViewport().setContents(null);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.RootEditPart#setContents(org.eclipse.gef.EditPart)
 */
public void setContents(EditPart editpart) {
	if (contents == editpart) {
		return;
	}
	if (contents != null)
		removeChild(contents);
	contents = editpart;
	if (contents != null)
		addChild(contents, 0);
}

/* (non-Javadoc)
 * @see org.eclipse.gef.RootEditPart#setViewer(org.eclipse.gef.EditPartViewer)
 */
public void setViewer(EditPartViewer editPartViewer) {
	viewer = editPartViewer;
}

protected Viewport getViewport() {
	return (Viewport)getFigure();
}

public class RulerViewport extends Viewport {
	private boolean horizontal;
	public RulerViewport(boolean isHorizontal) {
		super(true);
		horizontal = isHorizontal;
		setLayoutManager(null);
	}
	protected void doLayout(boolean force) {
		repaint();
		if (force) {
			RangeModel rModel;
			if (horizontal) {
				rModel = getHorizontalRangeModel();
			} else {
				rModel = getVerticalRangeModel();
			}
			Rectangle contentBounds = Rectangle.SINGLETON;
			if (horizontal) {
				contentBounds.y = 0;
				contentBounds.x = rModel.getMinimum();
				contentBounds.height = this.getContents().getPreferredSize().height;
				contentBounds.width = rModel.getMaximum() - rModel.getMinimum();
			} else {
				contentBounds.y = rModel.getMinimum();
				contentBounds.x = 0;
				contentBounds.height = rModel.getMaximum() - rModel.getMinimum();
				contentBounds.width = this.getContents().getPreferredSize().width;
			}
			if (!this.getContents().getBounds().equals(contentBounds)) {
				this.getContents().setBounds(contentBounds);
				this.getContents().revalidate();
			}						
		}
		getUpdateManager().performUpdate();
	}
	public Dimension getPreferredSize(int wHint, int hHint) {
		if (this.getContents() == null) {
			return new Dimension();
		}
		Dimension prefSize = this.getContents().getPreferredSize(wHint, hHint);
		if (horizontal) {
			RangeModel rModel = getHorizontalRangeModel();
			prefSize.width = rModel.getMaximum() - rModel.getMinimum();
		} else {
			RangeModel rModel = getVerticalRangeModel();
			prefSize.height = rModel.getMaximum() - rModel.getMinimum();
		}
		return prefSize.expand(getInsets().getWidth(), getInsets().getHeight());
	}
	protected void readjustScrollBars() {
		// since the range model is shared with the editor, the ruler viewports should
		// not touch it
	}
	public void propertyChange(PropertyChangeEvent event) {
		if (this.getContents() != null && event.getSource() instanceof RangeModel) {
			String property = event.getPropertyName();
			doLayout(RangeModel.PROPERTY_MAXIMUM.equals(property)
					|| RangeModel.PROPERTY_MINIMUM.equals(property)
					|| RangeModel.PROPERTY_VALUE.equals(property));
		}
	}
	public void setContents(IFigure figure) {
		super.setContents(figure);
		// Need to layout when contents change
		doLayout(true);
	}
	protected boolean useLocalCoordinates() {
		return true;
	}
}

}