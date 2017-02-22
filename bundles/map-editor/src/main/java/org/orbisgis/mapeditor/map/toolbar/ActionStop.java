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

import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.mainframe.api.ToolBarAction;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditorapi.MapEditorExtension;
import org.orbisgis.sif.UIFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.sql.DataSource;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;

/**
 * Close the drawing toolbar
 * @author Nicolas Fortin
 */
public class ActionStop extends ActionActiveLayer {
    private static final I18n I18N = I18nFactory.getI18n(ActionStop.class);
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionStop.class);

    /**
     * Constructor
     * @param extension MapExtension instance
     */
    public ActionStop(MapEditorExtension extension) {
        super(ToolBarAction.DRAW_STOP, I18N.tr("Stop"), extension, MapEditorIcons.getIcon("stop"));
        setToolTipText(I18N.tr("Close the drawing toolbar"));
        setLogicalGroup(ToolBarAction.DRAWING_GROUP);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        ILayer activeLayer = getActiveLayer();
        if(activeLayer!=null) {
            getExtension().getMapElement().getMapContext().setActiveLayer(null);
        }
    }
}
