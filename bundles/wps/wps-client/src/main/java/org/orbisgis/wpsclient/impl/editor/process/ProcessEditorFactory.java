package org.orbisgis.wpsclient.impl.editor.process;

import net.opengis.wps._2_0.ProcessOffering;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.edition.*;
import org.orbisgis.wpsclient.impl.WpsClientImpl;
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
public class ProcessEditorFactory implements EditorFactory {
    public static final String FACTORY_ID = "ProcessEditorFactory";
    private static final Logger LOGGER = LoggerFactory.getLogger("gui." + ProcessEditorFactory.class);
    protected final static I18n I18N = I18nFactory.getI18n(ProcessEditorFactory.class);
    private WpsClientImpl wpsClient = null;
    private EditorManager editorManager = null;

    public ProcessEditorFactory (EditorManager editorManager, WpsClientImpl wpsClient){
        this.editorManager = editorManager;
        this.wpsClient = wpsClient;
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
            return new ProcessPanelLayout(editableTable);
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
        return new ProcessPanelLayout(new ProcessEditableElement(new ProcessOffering(), new HashMap<URI, Object>()));
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

