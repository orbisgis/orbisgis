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
package org.orbisgis.mapeditor.map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.orbisgis.editorjdbc.EditableSource;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.edition.EditorTransferHandler;
import org.orbisgis.sif.edition.EditableElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Swing Handler for dragging EditableElement.
 * Supports MapElement and EditableSource.
 */
public class MapTransferHandler  extends EditorTransferHandler {
    private static final long serialVersionUID = 1L;

    static final private Logger GUILOGGER = LoggerFactory.getLogger("gui." + MapTransferHandler.class);
    static final private I18n I18N = I18nFactory.getI18n(MapTransferHandler.class);

    /**
     * If this method return true, this transfer handler fire the transfer editable event
     * @param editableElement Droped editable element.
     * @return True if this handler accept the editable element
     */
    protected boolean canImportEditableElement(EditableElement editableElement) {
        boolean canImport = editableElement.getTypeId().equals(EditableSource.EDITABLE_RESOURCE_TYPE) ||
                editableElement.getTypeId().equals(MapElement.EDITABLE_TYPE);
        if(!canImport) {
            GUILOGGER.error(I18N.tr("The map editor accept only map and geometry data source."));
        }
        return canImport;
    }
}
