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
package org.orbisgis.view.toc.actions.cui.legends;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.TypeFactory;
import org.gdms.driver.DriverException;

import javax.swing.*;

/**
 * Some useful methods that will be available for all thematic panels.
 * @author alexis
 */
public class AbstractFieldPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger("gui."+AbstractFieldPanel.class);

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * not spatial fields of {@code ds}.
     * @param ds
     * @return
     */
    public JComboBox getFieldCombo(DataSource ds){
        JComboBox combo = new JComboBox();
        if(ds != null){
            try {
                Metadata md = ds.getMetadata();
                int fc = md.getFieldCount();
                for (int i = 0; i < fc; i++) {
                    if(!TypeFactory.isSpatial(md.getFieldType(i).getTypeCode())){
                        combo.addItem(md.getFieldName(i));
                    }
                }
            } catch (DriverException ex) {
                LOGGER.error(ex);
            }
        }
        return combo;
    }

    /**
     * Initialize a {@code JComboBo} whose values are set according to the
     * numeric fields of {@code ds}.
     * @param ds
     * @return
     */
    public JComboBox getNumericFieldCombo(DataSource ds){
        JComboBox combo = new JComboBox();
        if(ds != null){
            try {
                Metadata md = ds.getMetadata();
                int fc = md.getFieldCount();
                for (int i = 0; i < fc; i++) {
                    if(TypeFactory.isNumerical(md.getFieldType(i).getTypeCode())){
                        combo.addItem(md.getFieldName(i));
                    }
                }
            } catch (DriverException ex) {
                LOGGER.error(ex);
            }
        }
        return combo;
    }
}
