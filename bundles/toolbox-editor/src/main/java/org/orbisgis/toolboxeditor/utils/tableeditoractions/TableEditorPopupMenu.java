package org.orbisgis.toolboxeditor.utils.tableeditoractions;

import org.orbisgis.tableeditorapi.SourceTable;
import org.orbisgis.tableeditorapi.TableEditorPopupActions;
import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu items related to ToolboxEditor in the TableEditor.
 * @author Sylvain PALOMINOS
 */
@Component
public class TableEditorPopupMenu implements TableEditorPopupActions{

    private ToolboxWpsClient toolboxWpsClient;

    @Override
    public List<Action> createActions(SourceTable target) {
        List<Action> actions = new ArrayList<>();
        actions.add(new ActionRemoveColumn(target, toolboxWpsClient));
        return actions;
    }

    @Reference
    public void setToolboxWpsClient(ToolboxWpsClient toolboxWpsClient) {
        this.toolboxWpsClient = toolboxWpsClient;
    }

    public void unsetToolboxWpsClient(ToolboxWpsClient toolboxWpsClient) {
        this.toolboxWpsClient = toolboxWpsClient;
    }

    @Override
    public void disposeActions(SourceTable target, List<Action> actions) {

    }
}
