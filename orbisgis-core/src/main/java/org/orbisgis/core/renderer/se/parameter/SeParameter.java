/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

import java.util.HashSet;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ParameterValueType;


/**
 *
 * @author maxence
 */
public interface SeParameter{

    /**
     * Get a set containing the name of the features that are referenced in 
     * this {@code Style}. We use a {@code HashSet}. This way, we can be sure
     * that features are not referenced twice.
     * @return 
     * The names of all the needed features, in a {@code HashSet} instance.
     */
    HashSet<String> dependsOnFeature();

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
