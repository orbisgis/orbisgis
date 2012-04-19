/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.function;

import org.orbisgis.core.renderer.se.parameter.Recode;

/**
 * Used to validate a {@code Recode} instance against a particular condition.
 * @author alexis
 */
public interface RecodeValidator {

        /**
         * If true, {@code rc} is considered to be valid according to this
         * validator.
         * @param rc
         * @return
         */
        boolean validateRecode(Recode rc);

}
