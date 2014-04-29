/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Set;
import org.apache.log4j.Logger;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.viewapi.docking.DockingPanelLayout;
import org.orbisgis.viewapi.table.TableEditableElement;
import org.orbisgis.viewapi.util.XElement;
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

        private DataManager dataManager;

        public TablePanelLayout(DataManager dataManager) {
                this.tableEditableElement = null;
                this.dataManager = dataManager;
        }

        
        
        public TablePanelLayout(TableEditableElement tableEditableElement) {
                this.tableEditableElement = tableEditableElement;
                this.dataManager = tableEditableElement.getDataManager();
        }

        public TableEditableElement getTableEditableElement() {
                return tableEditableElement;
        }
        
        @Override
        public void writeStream(DataOutputStream out) throws IOException {
                //DataSource
                out.writeUTF(tableEditableElement.getTableReference());
                //Selection
                writeSelection(out);
        }

        private void writeSelection(OutputStream out) throws IOException {
                ObjectOutputStream selectionOut = new ObjectOutputStream(out);
                //Do not save byte consuming selection
                IntegerUnion mergedSelection;
                Set<Integer> selection = tableEditableElement.getSelection();
                if(selection instanceof IntegerUnion) {
                        mergedSelection = (IntegerUnion) selection;
                } else {
                        mergedSelection = new IntegerUnion(selection);
                }
                if(mergedSelection.getValueRanges().size()>MAX_SELECTION_SERIALISATION_SIZE) {
                        selectionOut.writeObject(new IntegerUnion());
                } else {
                        selectionOut.writeObject(mergedSelection);
                }
                selectionOut.flush();
                selectionOut.close();                
        }
        
        private IntegerUnion readSelection(InputStream in) {
                try {
                        ObjectInputStream selectionIn = new ObjectInputStream(in);
                        return (IntegerUnion)selectionIn.readObject();
                } catch (ClassNotFoundException | IOException ex) {
                        LOGGER.error(I18N.tr("Selection deserialisation failed"),ex);
                }
            return new IntegerUnion();
        }
        @Override
        public void readStream(DataInputStream in) throws IOException {
                //DataSource
                String dataSourceName = in.readUTF();
                tableEditableElement = new TableEditableElementImpl(
                readSelection(in),dataSourceName, dataManager);
        }
        
        @Override
        public void writeXML(XElement element) {
                element.addString(PROP_DATA_SOURCE_NAME,tableEditableElement.getTableReference());
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
                tableEditableElement = new TableEditableElementImpl(readSelection(in),
                                element.getString(PROP_DATA_SOURCE_NAME), dataManager);

        }
}
