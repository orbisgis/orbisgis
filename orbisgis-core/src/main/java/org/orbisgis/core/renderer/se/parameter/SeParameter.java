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
     * return field names the parameter depends on.
     * Actually, it means that at least one child of this parameter access a feature attribute.
     *
     * @return field names the parameter depends on or null if parameter doesn't depends on the feature
     */
    String dependsOnFeature();

    ParameterValueType getJAXBParameterValueType();
   
    JAXBElement<? extends ExpressionType> getJAXBExpressionType();
}
