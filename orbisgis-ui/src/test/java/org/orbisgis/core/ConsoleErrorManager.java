/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core;

import org.orbisgis.core.errorManager.ErrorListener;
import org.orbisgis.core.errorManager.ErrorManager;

/**
 *
 * @author cleglaun
 */
public class ConsoleErrorManager implements ErrorManager {
    @Override
    public void addErrorListener(ErrorListener listener) {
    }

    @Override
    public void error(String userMsg) {
        System.out.println("ERR: " + userMsg);
    }

    @Override
    public void error(String userMsg, Throwable exception) {
        System.out.println("ERR: " + userMsg + ": " + exception);
    }

    @Override
    public void removeErrorListener(ErrorListener listener) {
    }

    @Override
    public void warning(String userMsg, Throwable exception) {
        System.out.println("WARN: " + userMsg + ": " + exception);
    }

    @Override
    public void warning(String userMsg) {
        System.out.println("WARN: " + userMsg);
    }
}