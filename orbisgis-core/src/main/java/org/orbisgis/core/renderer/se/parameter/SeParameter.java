/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;


/**
 *
 * @author maxence
 */
public interface SeParameter{

    /**
     * return true if the parameter depends on a feature.
     * Actually, it means that at least one child of this parameter access a feature attribute.
     * When true, the expression should be computed for each features. When false, a cached value can be used
     * @return
     */
    boolean dependsOnFeature();

    ParameterValueType getJAXBParameterValueType();
   
    JAXBElement<? extends ExpressionType> getJAXBExpressionType();
}
