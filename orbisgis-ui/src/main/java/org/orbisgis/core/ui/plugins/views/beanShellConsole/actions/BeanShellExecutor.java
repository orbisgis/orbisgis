/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.beanShellConsole.actions;

import bsh.EvalError;
import bsh.Interpreter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.ui.BshConsolePanel;
import org.orbisgis.core.ui.plugins.views.output.OutputManager;
import org.orbisgis.utils.I18N;

/**
 *
 * @author ebocher
 */
public class BeanShellExecutor {

        private BeanShellExecutor() {
        }

        /**
         * Execute the beanshell script and return some results in the output console.
         * @param interpreter
         * @param outputStream
         * @param text
         * @throws EvalError
         */
        public static void execute(BshConsolePanel panel, String text) {
                OutputManager outputManager = (OutputManager) Services.getService(OutputManager.class);
                try {
                        MapContext mc = ((MapContextManager) Services.getService(MapContextManager.class)).getActiveMapContext();

                        Interpreter interpreter = panel.getInterpreter();
                        if (mc != null) {
                                interpreter.set("mc", mc);
                        }
                        interpreter.eval(text);
                        String out = getOutput(panel.getScriptOutput());
                        if (out.length() > 0) {
                                outputManager.println(I18N.getString("orbisgis.org.orbisgis.beanshell.result"),
                                        Color.GREEN);

                                outputManager.println(out, Color.blue);
                                outputManager.println("--------------------", Color.GREEN);
                        }

                } catch (IllegalArgumentException e) {
                        ErrorMessages.error(
                                I18N.getString("orbisgis.org.orbisgis.beanshell.CannotExecuteScript"),
                                e);

                } catch (EvalError e) {
                        outputManager.println(I18N.getString("orbisgis.org.orbisgis.beanshell.error"),
                                Color.RED);
                        outputManager.println(e.getErrorText(), Color.RED);
                        outputManager.println("--------------------", Color.RED);
                }
        }

        private static String getOutput(ByteArrayOutputStream outputStream) {
                String ret = new String(outputStream.toByteArray());
                outputStream.reset();
                return ret;
        }
}
