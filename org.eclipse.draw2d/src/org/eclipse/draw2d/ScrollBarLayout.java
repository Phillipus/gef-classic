package org.eclipse.draw2d;
/*
 * Licensed Material - Property of IBM
 * (C) Copyright IBM Corp. 2001, 2002 - All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 */

import org.eclipse.draw2d.geometry.*;

/**
 * Private class that lays out the Figures that make up
 * a ScrollBar.
 */
class ScrollBarLayout
	extends AbstractLayout
{

public static final String 
	UP_ARROW   = "up arrow",   //$NON-NLS-1$
	DOWN_ARROW = "down arrow", //$NON-NLS-1$
	THUMB      = "thumb",      //$NON-NLS-1$
	PAGE_UP    = "page_up",    //$NON-NLS-1$
	PAGE_DOWN  = "page_down";  //$NON-NLS-1$

IFigure up, down, thumb, pageUp, pageDown;

/**
 * Transposes values if the ScrollBar is horizontally oriented.
 * When used properly, the layout manager just needs to code for one
 * case: vertical orientation.
 */
final protected Transposer transposer;

/**
 * Constructs a ScrollBarLayout.
 * 
 * @param t If enabled, Scrollbar will be horizontally oriented.
 *           If disabled, ScrollBar will be vertically oriented.
 * 
 * @since 2.0
 */
public ScrollBarLayout(Transposer t){
	transposer = t;
}

public void setConstraint(IFigure figure, Object constraint) {
	if (constraint.equals(UP_ARROW))
		up = figure;
	else if (constraint.equals(DOWN_ARROW))
		down = figure;
	else if (constraint.equals(THUMB))
		thumb = figure;
	else if (constraint.equals(PAGE_UP))
		pageUp = figure;
	else if (constraint.equals(PAGE_DOWN))
		pageDown = figure;
}

/**
 * Calculates and returns the preferred size of the container 
 * given as input.
 * 
 * @param parent  Figure whose preferred size is required.
 * @return  The preferred size of the figure input.
 * 
 * @since 2.0
 */
protected Dimension calculatePreferredSize(IFigure parent, int w, int h) {
	Insets insets = transposer.t(parent.getInsets());
	Dimension d = new Dimension(16, 16 * 4);
	d.expand(insets.getWidth(), insets.getHeight());
	return transposer.t(d);
}

public void layout(IFigure parent) {
	ScrollBar scrollBar = (ScrollBar) parent;

	Rectangle trackBounds = layoutButtons(scrollBar);

	int extent = scrollBar.getExtent();
	int max = scrollBar.getMaximum();
	int min = scrollBar.getMinimum();
	int totalRange =  max-min;
	int valueRange = totalRange - extent;
	if ((valueRange < 1) || (!scrollBar.isEnabled())){
		Rectangle boundsUpper = new Rectangle(trackBounds),
		          boundsLower = new Rectangle(trackBounds);
		boundsUpper.height /= 2;
		boundsLower.y += boundsUpper.height;
		boundsLower.height = trackBounds.height-boundsUpper.height;
		if(pageUp!=null)
			pageUp.setBounds(transposer.t(boundsUpper));
		if(pageDown!=null)
			pageDown.setBounds(transposer.t(boundsLower));
		return;
	}

	if (totalRange == 0)
		return;
	int thumbHeight = Math.max(
		thumb==null?0:thumb.getMinimumSize().height,
		trackBounds.height * extent / totalRange);

	if(thumb!=null)
		thumb.setVisible(trackBounds.height > thumbHeight);

	int thumbY = trackBounds.y +
		(trackBounds.height - thumbHeight) * (scrollBar.getValue()-min) / valueRange;

	Rectangle thumbBounds =  new Rectangle(
		trackBounds.x,
		thumbY,
		trackBounds.width,
		thumbHeight);

	if(thumb!=null)
		thumb.setBounds(transposer.t(thumbBounds));

	if(pageUp!=null)
		pageUp.setBounds(transposer.t(new Rectangle(
			trackBounds.x,
			trackBounds.y,
			trackBounds.width,
			thumbBounds.y - trackBounds.y)));

	if(pageDown!=null)
		pageDown.setBounds(transposer.t(new Rectangle(
			trackBounds.x ,
			thumbBounds.y + thumbHeight,
			trackBounds.width,
			trackBounds.bottom() - thumbBounds.bottom())));
}

/**
 * Places the buttons and returns the Rectangle into which the 
 * Track should be placed.
 * The track consists of the pageup, pagedown, and thumb figures.
 * The Rectangle returned should be transposed correctly, 
 * that is, it should be vertically oriented.  
 * Users of the rectangle will re-transpose it for horizontal use.
 * 
 * @since 2.0
 */
protected Rectangle layoutButtons(ScrollBar scrollBar){
	Rectangle bounds = transposer.t(scrollBar.getClientArea());
	Dimension buttonSize = new Dimension(
		bounds.width,
		Math.min(bounds.width,bounds.height/2));

	if (up != null)
		up.setBounds(transposer.t(
			new Rectangle(bounds.getTopLeft(), buttonSize)));
	if (down != null){
		Rectangle r = new Rectangle (
			bounds.x, bounds.bottom()-buttonSize.height,
			buttonSize.width, buttonSize.height);
		down.setBounds(transposer.t(r));
	}

	Rectangle trackBounds = bounds.getCropped(
		new Insets(
			(up   == null) ? 0 : buttonSize.height, 0,
			(down == null) ? 0 : buttonSize.height, 0));

	return trackBounds;
}

public void remove(IFigure child) {
	if (child == up) {
		up = null;
	} else if (child == down) {
		down = null;
	} else if (child == thumb) {
		thumb = null;
	} else if (child == pageUp) {
		pageUp = null;
	} else if (child == pageDown) {
		pageDown = null;
	}
}
}