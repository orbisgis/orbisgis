/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.thematic.constant;

/**
 * Even a unique symbol has parameters, at least from the symbol point of view.
 * This class will give a representation of a parameter of a unique symbol,
 * giving access to its name and to its value. For instance, {@code
 * UniqueSymbolArea} will have a {@code USParameter} for its width.
 * @author alexis, antoine
 */
public abstract class USParameter<E> {

        private String name;

        public USParameter(String name){
                this.name = name;
        }

        /**
         * Get the name of the parameter.
         * @return
         */
        public String getName(){
                return name;
        }

        /**
         * Get the Value of the parameter.
         */
        public abstract E getValue();

        /**
         * Set the value of the parameter.
         */
        public  abstract void setValue(E obj);

}
