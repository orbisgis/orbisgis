/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

/**
 *
 * @author maxence
 */
public interface CategorizeListener {

	// fired when the class i has been revoved
	void classRemoved(int i);


	// the iest class is a new one
	void classAdded(int i);

	// the iest has been moven at jest position
	void classMoved(int i, int j);
}
