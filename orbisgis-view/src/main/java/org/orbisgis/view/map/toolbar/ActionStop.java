/*
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

package org.orbisgis.view.map.toolbar;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.frames.ext.ToolBarAction;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

/**
 * Close the drawing toolbar
 * @author Nicolas Fortin
 */
public class ActionStop extends ActionActiveLayer {
    private static final I18n I18N = I18nFactory.getI18n(ActionStop.class);
    private static final Logger LOGGER = Logger.getLogger(ActionStop.class);

    /**
     * Constructor
     * @param extension MapExtension instance
     */
    public ActionStop(MapEditorExtension extension) {
        super(ToolBarAction.DRAW_STOP, I18N.tr("Stop"), extension, OrbisGISIcon.getIcon("stop"));
        setToolTipText(I18N.tr("Close the drawing toolbar"));
        setLogicalGroup(ToolBarAction.DRAWING_GROUP);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ILayer activeLayer = getActiveLayer();
        if(activeLayer!=null) {
            DataSource source = activeLayer.getDataSource();
            if(source.isModified()) {
                    int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                            I18N.tr("The edited layer has unsaved changes. Do you want to keep theses modifications ?"),
                            I18N.tr("Save modifications"),
                            JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
                    if(response == JOptionPane.YES_OPTION) {
                            try {
                                    source.commit();
                            } catch (Exception ex) {
                                    LOGGER.error(I18N.tr("Unable to save the modifications"),ex);
                            }
                    } else if(response == JOptionPane.NO_OPTION) {
                            try {
                                    source.syncWithSource();
                            } catch (DriverException ex) {
                                    LOGGER.error(ex.getLocalizedMessage(),ex);
                            }
                    } else if(response==JOptionPane.CANCEL_OPTION) {
                            return;
                    }
            }
            getExtension().getMapElement().getMapContext().setActiveLayer(null);
        }
    }
}
