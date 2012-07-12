/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
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
package org.orbisgis.sif.components;

import org.orbisgis.sif.components.OpenFilePanel;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import org.orbisgis.sif.SIFMessage;
import org.orbisgis.sif.SimplePanel;
import org.orbisgis.sif.UIFactory;

public class SaveFilePanel extends OpenFilePanel {

        private boolean fileMustNotExist;

        public SaveFilePanel(String id, String title) {
                super(id, title);
                getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
        }

        @Override
        public File getSelectedFile() {
                File ret;
                JFileChooser fc = getFileChooser();
                FileChooserUI ui = fc.getUI();
                if (ui instanceof BasicFileChooserUI) {
                        BasicFileChooserUI basicUI = (BasicFileChooserUI) ui;
                        String fileName = basicUI.getFileName();
                        if ((fileName == null) || (fileName.length() == 0)) {
                                ret = null;
                        } else {
                                ret = autoComplete(new File(fileName));
                        }
                } else {
                        ret = autoComplete(super.getSelectedFile());
                }
                if ((ret != null) && !ret.isAbsolute()) {
                        ret = new File(fc.getCurrentDirectory(), ret.getName());
                }
                return ret;
        }

        private File autoComplete(File selectedFile) {
                FileFilter ff = getFileChooser().getFileFilter();
                if (ff instanceof FormatFilter) {
                        FormatFilter filter = (FormatFilter) ff;
                        return filter.autoComplete(selectedFile);
                } else {
                        return selectedFile;
                }
        }

        @Override
        public SIFMessage validateInput() {
                File file = getSelectedFile();
                if (file == null) {
                        return new SIFMessage(i18n.tr("A file must be selected"), SIFMessage.ERROR);
                } else if (fileMustNotExist) {
                        if (getSelectedFile().exists()) {
                                return new SIFMessage(i18n.tr("The file already exists"),SIFMessage.ERROR);
                        } else {
                                return new SIFMessage();
                        }
                } else {
                        return new SIFMessage();
                }
        }

        @Override
        public File[] getSelectedFiles() {
                return new File[]{getSelectedFile()};
        }

        public void setFileMustNotExist(boolean fileMustNotExist) {
                this.fileMustNotExist = fileMustNotExist;
        }

        @Override
        public SIFMessage postProcess() {
                if (getSelectedFile().exists()) {
                        int ret = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(), i18n.tr("sif.file.fileAlreadyExists")
                                + " " + i18n.tr("sif.file.overwrite"), i18n.tr("sif.file.existing"),
                                JOptionPane.YES_NO_OPTION);
                        if (ret == JOptionPane.NO_OPTION || ret == JOptionPane.CLOSED_OPTION) {
                                // just do nothing...
                                // still, this isn't clean code...
                                //return new SIFMessage(SimplePanel.CANCELED_ACTION, SIFMessage.OK);
                        }
                }
                return new SIFMessage();
        }
}
