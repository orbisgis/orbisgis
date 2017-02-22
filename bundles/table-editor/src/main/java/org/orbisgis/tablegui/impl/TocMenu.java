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
package org.orbisgis.tablegui.impl;

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.tablegui.icons.TableEditorIcon;
import org.orbisgis.tocapi.LayerAction;
import org.orbisgis.tocapi.TocActionFactory;
import org.orbisgis.tocapi.TocExt;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;
import org.orbisgis.tableeditorapi.TableEditableElementImpl;

/**
 * Menu items related to TableEditor in the GeoCatalog.
 * @author Nicolas Fortin
 */
@Component
public class TocMenu implements TocActionFactory {
    private static I18n I18N = I18nFactory.getI18n(TocMenu.class);
    private DataManager dataManager;
    private EditorManager editorManager;

    @Override
    public List<Action> createActions(TocExt target) {
        return Arrays.asList((Action) new OpenAttributes(target, dataManager, editorManager));
    }

    @Override
    public void disposeActions(TocExt target, List<Action> actions) {

    }

    /**
     * @param dataManager DataManager to open Editable content
     */
    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * @param dataManager DataManager to open Editable content
     */
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = null;
    }

    /**
     * @param editorManager Editor windows manager
     */
    @Reference
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }
    /**
     * @param editorManager Editor windows manager
     */
    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    private static class OpenAttributes extends LayerAction {

        private TocExt toc;
        private DataManager dataManager;
        private EditorManager editorManager;

        public OpenAttributes(TocExt toc, DataManager dataManager, EditorManager editorManager) {
            super(toc, TocActionFactory.A_OPEN_ATTRIBUTES, I18N.tr("Open the attributes"), TableEditorIcon.getIcon("table"));
            setToolTipText(I18N.tr("Open a spreadsheet view of the attributes."));
            this.toc = toc;
            this.dataManager = dataManager;
            this.editorManager = editorManager;
            setOnRealLayerOnly(true);
            setOnVectorSourceOnly(true);
            setLogicalGroup(TocActionFactory.G_ATTRIBUTES);
            setKeyStroke(KeyStroke.getKeyStroke("ctrl " + "T"));
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(toc instanceof EditorDockable) {
                EditableElement editableElement = ((EditorDockable) toc).getEditableElement();
                if(editableElement instanceof MapElement) {
                    MapContext mapContext = ((MapElement) editableElement).getMapContext();
                    for (ILayer layer : mapContext.getSelectedLayers()) {
                        String tableRef = layer.getTableReference();
                        if(!tableRef.isEmpty()) {
                            editorManager.openEditable(new TableEditableElementImpl(layer.getSelection() ,tableRef, dataManager));
                        }
                    }
                }
            }
        }
    }
}
