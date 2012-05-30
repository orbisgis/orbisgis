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
    private static final long serialVersionUID = 1316533289L;

    /**
     * Build a new ParameterException
     */
    public ParameterException(){
        super();
    }

    /**
     * Build a new ParameterException
     * @param arg0 
     */
    public ParameterException(String arg0){
        super(arg0);
    }

    /**
     * Build a new ParameterException
     * @param arg0
     * @param arg1 
     */
    public ParameterException(String arg0, Throwable arg1){
        super(arg0, arg1);
    }

    /**
     * Build a new ParameterException
     * @param arg0 
     */
    public ParameterException(Throwable arg0){
        super(arg0);
    }
}
