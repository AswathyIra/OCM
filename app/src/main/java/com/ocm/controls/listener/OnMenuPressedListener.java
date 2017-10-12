/**
 * 
 * Copyright(c) ULTA, Inc. All Rights reserved. 
 *
 *
 */
package com.ocm.controls.listener;



/**
 * The listener interface for receiving onMenuPressed events.
 * The class that is interested in processing a onMenuPressed
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addOnMenuPressedListener<code> method. When
 * the onMenuPressed event occurs, that object's appropriate
 * method is invoked.
 *
 */
public interface OnMenuPressedListener {
	 
 	/**
 	 * On menu pressed.
 	 */
 	public void onMenuPressed();
}
