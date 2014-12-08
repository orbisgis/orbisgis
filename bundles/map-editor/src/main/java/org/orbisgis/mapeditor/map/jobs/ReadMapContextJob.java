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
package org.orbisgis.mapeditor.map.jobs;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.edition.EditorManager;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Open the MapContext and 
 * @author Nicolas Fortin
 */
public class ReadMapContextJob extends SwingWorkerPM {

    private static final I18n I18N = I18nFactory.getI18n(ReadMapContextJob.class);
    private MapElement editableMap;
    private EditorManager editorManager;

    /**
     * Constructor.
     * @param editableMap Instance of editable map.
     * @param editorManager Instance of editor manager.
     */
    public ReadMapContextJob(MapElement editableMap, EditorManager editorManager) {
        this.editableMap = editableMap;
        this.editorManager = editorManager;
        setTaskName(I18N.tr("Open the map context"));
    }

    @Override
    protected Object doInBackground() throws Exception {
        editableMap.open(this);
        editorManager.openEditable(editableMap);
        return null;
    }
}
