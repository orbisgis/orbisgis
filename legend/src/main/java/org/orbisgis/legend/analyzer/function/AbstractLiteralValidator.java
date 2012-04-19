/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.legend.analyzer.function;

import org.orbisgis.legend.analyzer.function.CategorizeValidator;
import org.orbisgis.legend.analyzer.function.RecodeValidator;
import org.orbisgis.core.renderer.se.parameter.Categorize;
import org.orbisgis.core.renderer.se.parameter.Literal;
import org.orbisgis.core.renderer.se.parameter.Recode;
import org.orbisgis.legend.AbstractAnalyzer;

/**
 * Specializations of this class will be both instances of {@link
 * CategorizeValidator} and {@link RecodeValidator}. A {@code Categorize}
 * instance will be valid if its classes and threshold values are {@code Literal}
 * instances. A {@code Recode} instance will be valid if its item values are
 * {@code Literal} instances.
 * @author alexis
 */
public abstract class AbstractLiteralValidator extends AbstractAnalyzer implements
                CategorizeValidator, RecodeValidator {

        /**
         * Validate a {@code Recode} instance. It will be valid if and only if
         * its map values are all {@code Literal} instances.
         * @param rc
         * @return
         */
        @Override
        public boolean validateRecode(Recode rc) {
                for(int i = 0; i < rc.getNumMapItem(); i++){
                        if(!(rc.getMapItemValue(i) instanceof Literal)){
                                return false;
                        }
                }
                return true;
        }

        /**
         * Validate a {@code Categorize} instance. It will be valid if and only
         * if its class and threshold values are all {@code Literal} instances.
         * @param cg
         * @return
         */
        @Override
        public boolean validateCategorize(Categorize cg){
                if(!(cg.getClassValue(0) instanceof Literal)){
                        return false;
                }
                for(int i=1; i<cg.getNumClasses();i++){
                        if (!((cg.getClassValue(i) instanceof Literal)
                                && (cg.getClassThreshold(i-1) instanceof Literal))) {
                                return false;
                        }
                }
                return true;
        }

}
