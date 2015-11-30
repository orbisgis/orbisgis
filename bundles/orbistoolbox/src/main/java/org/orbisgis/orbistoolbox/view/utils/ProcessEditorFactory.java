/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.view.utils;

import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.Filter.ProcessPanelLayout;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.*;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

/**
 * EditorFactory for the WPS processes.
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessEditorFactory implements EditorFactory{
    public static final String FACTORY_ID = "WPSProcessEditorFactory";
    private EditorManager editorManager;
    private DataManager dataManager;
    private ToolBox toolBox;

    public ProcessEditorFactory(DataManager dataManager, EditorManager editorManager, ToolBox toolBox){
        this.dataManager = dataManager;
        this.editorManager = editorManager;
        this.toolBox = toolBox;
    }

    @Override
    public String getId() {
        return FACTORY_ID;
    }

    @Override
    public void dispose() {

    }

    @Override
    public DockingPanelLayout makeEditableLayout(EditableElement editableElement) {
        if(editableElement instanceof ProcessEditableElement) {
            ProcessEditableElement editableProcess= (ProcessEditableElement)editableElement;
            if(isEditableAlreadyOpened(editableProcess)) { //Panel already created
                LoggerFactory.getLogger(ProcessEditorFactory.class)
                        .info("This process ("+editableProcess.getProcessReference()+") is already shown in an editor.");
                return null;
            }
            return new ProcessPanelLayout(editableProcess);
        } else {
            return null;
        }
    }

    /**
     * Set editor manager instance in order to check if a table editor is already opened
     * @param editorManager
     */
    @Reference
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = null;
    }

    private boolean isEditableAlreadyOpened(EditableElement editable) {
        for(Editor editor : editorManager.getEditors()) {
            if(editor instanceof ProcessEditor && editable.equals(editor.getEditableElement())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param dataManager JDBC DataManager factory
     */
    @Reference
    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }
    /**
     * @param dataManager JDBC DataManager factory
     */
    public void unsetDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public DockingPanelLayout makeEmptyLayout() {
        return new ProcessPanelLayout();
    }

    @Override
    public boolean match(DockingPanelLayout dockingPanelLayout) {
        return dockingPanelLayout instanceof ProcessPanelLayout;
    }

    @Override
    public EditorDockable create(DockingPanelLayout layout) {
        ProcessEditableElement editableProcess = ((ProcessPanelLayout)layout).getProcessEditableElement();
        //Check the DataSource state
        ProcessEditor pe = new ProcessEditor(toolBox);
        pe.setEditableElement(editableProcess);
        return pe;
    }
}
