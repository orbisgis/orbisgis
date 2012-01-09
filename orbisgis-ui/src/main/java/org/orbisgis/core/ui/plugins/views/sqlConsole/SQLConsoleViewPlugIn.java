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
package org.orbisgis.core.ui.plugins.views.sqlConsole;

import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.sif.OpenFilePanel;
import org.orbisgis.core.sif.SaveFilePanel;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.core.ui.plugins.views.sqlConsole.actions.ExecuteScriptProcess;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ui.SQLConsolePanel;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;


/*
 *
 */
public class SQLConsoleViewPlugIn extends ViewPlugIn {

        private SQLConsolePanel panel;
        private final String EOL = System.getProperty("line.separator");
        private JMenuItem menuItem;
        private JButton btn;

        public SQLConsoleViewPlugIn() {
                btn = new JButton(OrbisGISIcon.SQLCONSOLE_ICON);
                btn.setToolTipText(Names.SQLCONSOLE);
        }

        @Override
        public void delete() {
                if (panel != null) {
                        panel.freeResources();
                }
        }

        public void initialize(PlugInContext context) throws Exception {
                panel = new SQLConsolePanel(new ConsoleListener() {

                        public boolean save(String text) throws IOException {
                                final SaveFilePanel outfilePanel = new SaveFilePanel(
                                        "org.orbisgis.core.ui.views.sqlConsoleOutFile", I18N.getString("orbisgis.org.orbisgis.saveScript"));
                                outfilePanel.addFilter("sql", "SQL script (*.sql)");

                                if (UIFactory.showDialog(outfilePanel)) {
                                        final BufferedWriter out = new BufferedWriter(
                                                new FileWriter(outfilePanel.getSelectedFile()));
                                        out.write(text);
                                        out.close();
                                        return true;
                                }
                                return false;
                        }

                        public String open() throws IOException {
                                final OpenFilePanel inFilePanel = new OpenFilePanel(
                                        "org.orbisgis.plugins.core.ui.views.sqlConsoleInFile",
                                        I18N.getString("orbisgis.org.orbisgis.openScript"));
                                inFilePanel.addFilter("sql", "SQL script (*.sql)");

                                if (UIFactory.showDialog(inFilePanel)) {
                                        File selectedFile = inFilePanel.getSelectedFile();
                                        final BufferedReader in = new BufferedReader(
                                                new FileReader(selectedFile));
                                        String line;
                                        StringBuilder ret = new StringBuilder();
                                        while ((line = in.readLine()) != null) {
                                                ret.append(line).append(EOL);
                                        }
                                        in.close();

                                        return ret.toString();
                                } else {
                                        return null;
                                }
                        }

                        public void execute(String text) {
                                BackgroundManager bm = Services.getService(BackgroundManager.class);
                                bm.backgroundOperation(new ExecuteScriptProcess(text, panel));
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

                menuItem = context.getFeatureInstaller().addMainMenuItem(this,
                        new String[]{Names.VIEW}, Names.SQLCONSOLE, true,
                        OrbisGISIcon.SQLCONSOLE_ICON, null, panel, context);

                WorkbenchContext wbcontext = context.getWorkbenchContext();
                wbcontext.getWorkbench().getFrame().getViewToolBar().addPlugIn(this,
                        btn, context);

        }

        @Override
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
                return I18N.getString("orbisgis.org.orbisgis.sql.view");
        }
}
