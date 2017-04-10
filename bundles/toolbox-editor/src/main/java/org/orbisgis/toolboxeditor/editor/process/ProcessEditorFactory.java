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
package org.orbisgis.toolboxeditor.editor.process;

import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.*;
import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.net.URI;
import java.util.HashMap;

/**
 *  This factory receive the {@link ProcessEditableElement} and open a new editor.
 *
 *  @author Sylvain PALOMINOS
 */
@Component(service = EditorFactory.class, immediate = true)
public class ProcessEditorFactory implements EditorFactory {
    public static final String FACTORY_ID = "ProcessEditorFactory";
    private static final Logger LOGGER = LoggerFactory.getLogger("gui." + ProcessEditorFactory.class);
    protected final static I18n I18N = I18nFactory.getI18n(ProcessEditorFactory.class);
    private WpsClientImpl wpsClient = null;
    private EditorManager editorManager = null;

    @Reference()
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = null;
    }

    @Reference()
    public void setInternalWpsClient(ToolboxWpsClient toolboxWpsClient) {
        this.wpsClient = (WpsClientImpl) toolboxWpsClient;
    }

    public void unsetInternalWpsClient(ToolboxWpsClient toolboxWpsClient) {
        this.wpsClient = (WpsClientImpl) toolboxWpsClient;
    }

    @Override
    public DockingPanelLayout makeEditableLayout(EditableElement editable) {
        if(editable instanceof ProcessEditableElement) {
            ProcessEditableElement editableTable = (ProcessEditableElement)editable;
            if(isEditableAlreadyOpened(editableTable)) { //Panel already created
                LOGGER.info(I18N.tr("This process ({0}) is already shown in an editor.",
                        editableTable.getProcess().getTitle().get(0).getValue()));
                return null;
            }
            return new ProcessPanelLayout(editableTable, wpsClient);
        } else {
            return null;
        }
    }

    private boolean isEditableAlreadyOpened(EditableElement editable) {
        for(Editor editor : editorManager.getEditors()) {
            if(editor instanceof ProcessEditor && editable.equals(editor.getEditableElement())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DockingPanelLayout makeEmptyLayout() {
        return new ProcessPanelLayout(new ProcessEditableElement(null, null, new HashMap<URI, Object>()), wpsClient);
    }

    @Override
    public boolean match(DockingPanelLayout layout) {
        return layout instanceof ProcessPanelLayout;
    }

    @Override
    public EditorDockable create(DockingPanelLayout layout) {
        ProcessEditableElement editableElement = ((ProcessPanelLayout)layout).getProcessEditableElement();
        //Check the DataSource state
        return new ProcessEditor(wpsClient, editableElement);
    }

    @Override
    public String getId() {
        return FACTORY_ID;
    }

    @Override
    public void dispose() {
        //
    }
}
