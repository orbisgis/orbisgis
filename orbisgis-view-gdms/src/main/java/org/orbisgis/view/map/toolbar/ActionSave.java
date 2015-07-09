/*
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

package org.orbisgis.view.map.toolbar;

import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.view.components.actions.ActionTools;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.main.frames.ext.ToolBarAction;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.map.ext.MapEditorExtension;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
import java.awt.event.ActionEvent;

/**
 * Save button on the Drawing ToolBar
 * @author Nicolas Fortin
 */
public class ActionSave extends ActionDataSource {
    private static final I18n I18N = I18nFactory.getI18n(ActionSave.class);
    private static final Logger LOGGER = Logger.getLogger(ActionSave.class);

    /**
     * Constructor
     * @param extension MapEditor instance
     */
    public ActionSave(MapEditorExtension extension) {
        super(ToolBarAction.DRAW_SAVE, I18N.tr("Save"), extension, OrbisGISIcon.getIcon("save"));
        putValue(SHORT_DESCRIPTION, I18N.tr("Save unsaved modifications of this layer"));
        setLogicalGroup(ToolBarAction.DRAWING_GROUP);
    }

    @Override
    protected void checkActionState() {
        super.checkActionState();
        // Active only if the DataSource is modified
        if(ActionTools.isVisible(this)) {
            DataSource dataSource = getExtension().getMapElement().getMapContext().getActiveLayer().getDataSource();
            setEnabled(dataSource!=null && dataSource.isModified());
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
            MapElement loadedMap = getExtension().getMapElement();
            if(loadedMap!=null) {
                try {
                    loadedMap.getMapContext().getActiveLayer().getDataSource().commit();
                } catch (Exception ex) {
                    LOGGER.error(ex.getLocalizedMessage(),ex);
                }
            }
    }
}
