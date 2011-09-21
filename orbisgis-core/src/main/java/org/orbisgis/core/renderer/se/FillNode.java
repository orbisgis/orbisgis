/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se;

import org.orbisgis.core.renderer.se.fill.Fill;

/**
 * Interface to be implemented by every node that can contain a <code>Fill</code> element.
 * @author maxence, alexis
 */
public interface FillNode {

        /**
         * Replace the current fill with the one given in argument.
         * @param f 
         * A {@link Fill} implementation. It's up to the realization to decide
         * if it can be null or not.
         */
	void setFill(Fill f);
        
        /**
         * Gets the {@code Fill} associated to this {@code FillNode}.
         * @return 
         * A {@link Fill} instance.
         */
	Fill getFill();
}
