/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core;

import java.awt.Color;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;

/**
 *
 * @author cleglaun
 */
public class ConsoleOutputManager implements OutputManager {

        @Override
        public void print(String out) {
            System.out.println ("OUTPUT: " + out);
        }

        @Override
        public void print(String text, Color color) {
            System.out.println ("OUTPUT: " + text);
        }

        @Override
        public void println(String out) {
            System.out.println ("OUTPUT: " + out);
        }

        @Override
        public void println(String text, Color color) {
            System.out.println ("OUTPUT: " + text);
        }
}
