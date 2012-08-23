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
package org.orbisgis.view.table;

import bibliothek.util.xml.XElement;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.view.docking.DockingPanelLayout;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * When the application close and start this layout retrieve/save
 * the state of this window.
 */

public class TablePanelLayout implements DockingPanelLayout {
        private TableEditableElement tableEditableElement;
        private static final Logger LOGGER = Logger.getLogger(TablePanelLayout.class);
        private static final I18n I18N = I18nFactory.getI18n(TablePanelLayout.class);
        private static final int MAX_SELECTION_SERIALISATION_SIZE = 100;
        
        //Fields name (xml)
        private static final String PROP_DATA_SOURCE_NAME = "datasource";
        private static final String PROP_SELECTION = "selection";

        public TablePanelLayout() {
                this.tableEditableElement = null;
        }

        
        
        public TablePanelLayout(TableEditableElement tableEditableElement) {
                this.tableEditableElement = tableEditableElement;
        }

        public TableEditableElement getTableEditableElement() {
                return tableEditableElement;
        }
        
        @Override
        public void writeStream(DataOutputStream out) throws IOException {
                //DataSource
                out.writeUTF(tableEditableElement.getSourceName());
                //Selection
                writeSelection(out);
        }

        private void writeSelection(OutputStream out) throws IOException {
                ObjectOutputStream selectionOut = new ObjectOutputStream(out);
                //Do not save byte consuming selection
                if(((IntegerUnion)tableEditableElement.getSelection()).getValueRanges().size()>MAX_SELECTION_SERIALISATION_SIZE) {
                        selectionOut.writeObject(new IntegerUnion());
                } else {
                        selectionOut.writeObject(tableEditableElement.getSelection());
                }
                selectionOut.flush();
                selectionOut.close();                
        }
        
        private IntegerUnion readSelection(InputStream in) {
                try {
                        ObjectInputStream selectionIn = new ObjectInputStream(in);
                        return (IntegerUnion)selectionIn.readObject();
                } catch (ClassNotFoundException ex) {
                        LOGGER.error(I18N.tr("Selection deserialisation failed"),ex);
                }  catch (IOException ex) {
                        LOGGER.error(I18N.tr("Selection deserialisation failed"),ex);
                }
                return new IntegerUnion();
        }
        @Override
        public void readStream(DataInputStream in) throws IOException {
                //DataSource
                String dataSourceName = in.readUTF();
                tableEditableElement = new TableEditableElement(
                readSelection(in),dataSourceName);
        }
        
        @Override
        public void writeXML(XElement element) {
                element.addString(PROP_DATA_SOURCE_NAME,tableEditableElement.getSourceName());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                try {
                        writeSelection(bytes);
                } catch (IOException ex) {
                        LOGGER.error(I18N.tr("Selection serialisation failed"),ex);
                }                
                element.addByteArray(PROP_SELECTION, bytes.toByteArray());
        }

        @Override
        public void readXML(XElement element) {
                ByteArrayInputStream in = new ByteArrayInputStream(element.getByteArray(PROP_SELECTION));
                tableEditableElement = new TableEditableElement(readSelection(in),
                                element.getString(PROP_DATA_SOURCE_NAME));

        }
}
