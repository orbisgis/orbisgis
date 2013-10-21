/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sif.components;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import org.orbisgis.sif.UIFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class SaveFilePanel extends OpenFilePanel {

        private static final I18n I18N = I18nFactory.getI18n(SaveFilePanel.class);
        private boolean fileMustNotExist;
        // By default, we confirm before overwriting files.
        private boolean confirmOverwrite = true;
        //Default size of the message for the confirm dialog
        private int messageWidth = 300;

        public SaveFilePanel(String id, String title) {
                super(id, title);
                getFileChooser().setDialogType(JFileChooser.SAVE_DIALOG);
                setAcceptAllFileFilterUsed(false);
        }

        /**
         * @return True if this dialog will ask a confirmation for overwriting file.
         */
        public boolean isConfirmOverwrite() {
            return confirmOverwrite;
        }

        /**
         * @param confirmOverwrite This dialog will ask a confirmation for overwriting file.
         */
        public void setConfirmOverwrite(boolean confirmOverwrite) {
            this.confirmOverwrite = confirmOverwrite;
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
        public String validateInput() {
                File file = getSelectedFile();
                if (file == null) {
                        return I18N.tr("A file must be selected");
                }
                boolean exists = getSelectedFile().exists();
                if (exists && fileMustNotExist) {
                    return UIFactory.getI18n().tr("The file already exists");
                } else if(exists && confirmOverwrite) {
                    if (JOptionPane.showConfirmDialog(
                            getComponent(),
                            "<html><body><p style='width: " + messageWidth + "px;'>"
                            + I18N.tr("The file {0} already exists. Overwrite?"
                            + "</body></html>", getSelectedFile()),
                            I18N.tr("Confirm overwrite"),
                            JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return I18N.tr("Overwrite canceled");
                    }
                }
                return null;
        }

        @Override
        public File[] getSelectedFiles() {
                return new File[]{getSelectedFile()};
        }

        public void setFileMustNotExist(boolean fileMustNotExist) {
                this.fileMustNotExist = fileMustNotExist;
        }
}
