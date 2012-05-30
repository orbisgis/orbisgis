/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

/**
 * Parameter of a unique symbol that embeds a numeric value. Consequently, its
 * use can be dependant upon a minimum and a maximum values.
 * @author alexis
 */
public abstract class USNumericParameter<A extends Number> extends USParameter<A> {

        /**
         * Build the parameter with the given name.
         * @param name
         */
        public USNumericParameter(String name) {
                super(name);
        }

        /**
         * Get the maximum value authorized for this {@code USParameter}.
         * @return
         */
        public abstract A getMaxValue();

        /**
         * Get the minimum value authorized for this {@code USParameter}.
         * @return
         */
        public abstract A getMinValue();

}
