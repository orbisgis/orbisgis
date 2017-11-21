package org.orbisgis.tablegui.impl.jobs;

import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.common.IntegerUnion;
import org.orbisgis.corejdbc.common.LongUnion;
import org.orbisgis.editorjdbc.EditableSource;
import org.orbisgis.sif.edition.EditableElementException;
import org.orbisgis.tableeditorapi.TableEditableElement;
import org.orbisgis.tablegui.impl.DataSourceTableModel;
import org.orbisgis.tablegui.impl.TableEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Nicolas Fortin
 */
public class ToggleGeomTableJob extends SwingWorkerPM<Boolean, Boolean> {
    protected final static I18n I18N = I18nFactory.getI18n(ToggleGeomTableJob.class);
    private static final Logger LOGGER = LoggerFactory.getLogger("gui." + ToggleGeomTableJob.class);
    private TableEditor tableEditor;

    public ToggleGeomTableJob(TableEditor tableEditor) {
        this.tableEditor = tableEditor;
        setTaskName(I18N.tr("Toggle geometries"));
    }

    @Override
    protected void done() {
        tableEditor.tableChange(new TableEditEvent(
                tableEditor.getTableEditableElement().getTableReference(),
                        TableModelEvent.ALL_COLUMNS,
                        null,
                        null,
                        TableModelEvent.DELETE));
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        EditableSource tableEditableElement = tableEditor.getTableEditableElement();
        tableEditableElement.setExcludeGeometry(!tableEditableElement.getExcludeGeometry());
        try {
            tableEditableElement.close(this.getProgressMonitor().startTask(I18N.tr("Close editable source"), 1));
            tableEditableElement.open(this.getProgressMonitor().startTask(I18N.tr("Open editable source"), 1));
            tableEditor.onMenuNoSort();
        } catch (EditableElementException e) {
            e.printStackTrace();
        }
        return true;
    }
}
