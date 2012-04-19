/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.function;

import org.orbisgis.core.renderer.se.parameter.Categorize;


/**
 * Used to validate a {@code Categorize} instance against a particular property.
 * @author alexis
 */
public interface CategorizeValidator {

        /**
         * If true, the {@code Categorize} instance is considered to be valid.
         * @param cg 
         * @return
         */
        boolean validateCategorize(Categorize cg);
        
}
