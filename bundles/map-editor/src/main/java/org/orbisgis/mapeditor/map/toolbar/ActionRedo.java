/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

package org.orbisgis.mapeditor.map.toolbar;

import org.orbisgis.mainframe.api.ToolBarAction;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditorapi.MapEditorExtension;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.components.actions.ActionTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.undo.CannotRedoException;
import java.awt.event.ActionEvent;

/**
 * Cancel button on the Drawing ToolBar
 * @author Nicolas Fortin
 */
public class ActionRedo extends ActionActiveLayer {
    private static final I18n I18N = I18nFactory.getI18n(ActionRedo.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionRedo.class);

    /**
     * Constructor
     * @param extension MapEditor instance
     */
    public ActionRedo(MapEditorExtension extension) {
        super(ToolBarAction.DRAW_REDO, I18N.tr("Redo"), extension, MapEditorIcons.getIcon("edit-redo"));
        putValue(SHORT_DESCRIPTION,I18N.tr("Redo the last modification"));
        setLogicalGroup(ToolBarAction.DRAWING_GROUP);
        setTrackDataManager(true);
    }

    @Override
    protected void checkActionState() {
        super.checkActionState();
        // Active only if the DataSource is modified
        if(ActionTools.isVisible(this)) {
            setEnabled(getExtension().getMapElement().getMapUndoManager().canRedo());
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        MapElement loadedMap = getExtension().getMapElement();
        if(loadedMap!=null) {
            try {
                getExtension().getMapElement().getMapUndoManager().redo();
            } catch (CannotRedoException ex) {
                LOGGER.error(ex.getLocalizedMessage(),ex);
            }
        }
    }
}
