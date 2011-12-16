/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ParameterValueType;


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

    /**
     * Get the JAXB type that would represent this {@code SeParameter}
     * @return 
     */
    ParameterValueType getJAXBParameterValueType();
   
    
    /**
     * Get the JAXBElement that would represent this {@code SeParameter}
     * @return 
     */
    JAXBElement<?> getJAXBExpressionType();
}
