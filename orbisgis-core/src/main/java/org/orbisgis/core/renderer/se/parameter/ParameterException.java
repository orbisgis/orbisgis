/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.parameter;

/**
 *
 * Exception thrown when evaluation of a parameter (Color, Real or string)
 * can not be done
 * @author maxence
 */
public class ParameterException extends Exception{

    public ParameterException(){
        super();
    }

    public ParameterException(String arg0){
        super(arg0);
    }

    public ParameterException(String arg0, Throwable arg1){
        super(arg0, arg1);
    }

    public ParameterException(Throwable arg0){
        super(arg0);
    }
}
