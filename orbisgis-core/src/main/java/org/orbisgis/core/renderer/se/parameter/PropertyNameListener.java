/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

/**
 * Listens to change in the name of the field where to retrieve values, in a
 * {@link ValueReference}.
 * @author maxence, alexis
 */
public interface PropertyNameListener {

        /**
         * The name of a property has changed.
         * @param p
         */
	void propertyNameChanged(ValueReference p);
}
