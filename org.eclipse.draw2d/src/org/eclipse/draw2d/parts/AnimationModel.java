/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.draw2d.parts;


/**
 * Holds the count information, and notifies interested figures of changes in 
 * animation.  Created by a root, which loops through the animation process. 
 */
class AnimationModel {

protected long startTime;
protected long endTime;
protected long numberOfMilliSeconds;

/**
 * Default constructor taking in number of milliseconds the animation should 
 * take.
 * @param numberOfMilliSeconds the number of milliseconds the animation should last
 */
public AnimationModel(long numberOfMilliSeconds) {
	this.numberOfMilliSeconds = numberOfMilliSeconds;
}

/**
 * Called to notify the start of the animation process. Notifies all listeners 
 * to get ready for animation start.
 */
public void start() {
	startTime = System.currentTimeMillis();
	endTime = startTime + numberOfMilliSeconds;
}

/**
 * @return value of current position (between 0.0 and 1.0).
 */
public float getValue() {
	long presentTime = System.currentTimeMillis();
	if (presentTime > endTime) 
		return (float)1.0;
	long timePassed = (presentTime - startTime);
	float progress = ((float)timePassed) / ((float)numberOfMilliSeconds);
	return progress;
}

}