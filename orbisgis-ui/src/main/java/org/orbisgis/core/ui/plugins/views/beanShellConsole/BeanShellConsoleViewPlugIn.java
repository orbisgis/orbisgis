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
package org.orbisgis.core.ui.plugins.views.beanShellConsole;

import java.awt.datatransfer.Transferable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BeanShellExecutor;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.actions.BshConsoleListener;
import org.orbisgis.core.ui.plugins.views.beanShellConsole.ui.BshConsolePanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class BeanShellConsoleViewPlugIn extends ViewPlugIn {

        private BshConsolePanel panel;
        private JMenuItem menuItem;
        private JButton btn;

        public BeanShellConsoleViewPlugIn() {
                btn = new JButton(OrbisGISIcon.BEANSHELL_ICON);
                btn.setToolTipText(Names.BEANSHELL);
        }

        @Override
        public void initialize(PlugInContext context) throws Exception {

                panel = new BshConsolePanel(new BshConsoleListener() {

                        @Override
                        public boolean save(String text) throws IOException {
                                final SaveFilePanel outfilePanel = new SaveFilePanel(
                                        "org.orbisgis.cores.BeanShellConsoleOutFile", I18N.getString("orbisgis.org.orbisgis.saveScript"));
                                outfilePanel.addFilter("bsh", "BeanShell Script (*.bsh)");

                                if (UIFactory.showDialog(outfilePanel)) {
                                        final BufferedWriter out = new BufferedWriter(
                                                new FileWriter(outfilePanel.getSelectedFile()));
                                        out.write(text);
                                        out.close();
                                        return true;
                                }
                                return false;
                        }

                        @Override
                        public String open() throws IOException {
                                final OpenFilePanel inFilePanel = new OpenFilePanel(
                                        "org.orbisgis.plugins.views.BeanShellConsoleInFile",
                                        I18N.getString("orbisgis.org.orbisgis.openScript"));
                                inFilePanel.addFilter("bsh", "BeanShell Script (*.bsh)");

                                if (UIFactory.showDialog(inFilePanel)) {
                                        File selectedFile = inFilePanel.getSelectedFile();
                                        long fileLength = selectedFile.length();
                                        if (fileLength > 1048576) {
                                                throw new IOException(
                                                        I18N.getString("orbisgis.org.orbisgis.CannotReadScript"));
                                        } else {
                                                FileReader fr = new FileReader(selectedFile);
                                                char[] buff = new char[(int) fileLength];
                                                fr.read(buff, 0, (int) fileLength);
                                                String string = new String(buff);
                                                fr.close();
                                                return string;
                                        }
                                } else {
                                        return null;
                                }
                        }

                        @Override
                        public void execute(String text) {
                                if (text.trim().length() > 0) {
                                        BeanShellExecutor.execute(panel, text);
                                } else {
                                }
                        }

                        @Override
                        public void change() {
                        }

                        @Override
                        public boolean showControlButtons() {
                                return true;
                        }

                        @Override
                        public String doDrop(Transferable t) {
                                return null;
                        }
                });
                panel.setText("print(\"" + "Hello world !\"" + ");");


                menuItem = context.getFeatureInstaller().addMainMenuItem(this,
                        new String[]{Names.VIEW}, Names.BEANSHELL, true,
                        OrbisGISIcon.BEANSHELL_ICON, null, panel, context);
                WorkbenchContext wbcontext = context.getWorkbenchContext();
                wbcontext.getWorkbench().getFrame().getViewToolBar().addPlugIn(this,
                        btn, context);
        }

        public boolean execute(PlugInContext context) throws Exception {
                getPlugInContext().loadView(getId());
                return true;
        }

        public boolean isEnabled() {
                return true;
        }

        public boolean isSelected() {
                boolean isSelected = false;
                isSelected = getPlugInContext().viewIsOpen(getId());
                menuItem.setSelected(isSelected);
                return isSelected;
        }

        public String getName() {
                return I18N.getString("orbisgis.org.orbisgis.beanshell.view");
        }
}
