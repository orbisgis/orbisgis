package org.orbisgis.wpsclient.view.utils;

import org.orbisgis.corejdbc.ReadRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.RowSet;
import javax.swing.*;
import java.sql.SQLException;

/**
 * @author Sylvain PALOMINOS
 */
public class FieldValueListModel extends AbstractListModel<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldValueListModel.class);

    private ReadRowSet readRowSet;
    private String columnName;

    public FieldValueListModel(ReadRowSet readRowSet, String columnName){
        this.readRowSet = readRowSet;
        this.columnName = columnName;
    }

    @Override
    public int getSize() {
        try {
            return (int)readRowSet.getRowCount();
        } catch (SQLException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return 0;
        }
    }

    @Override
    public String getElementAt(int row) {
        try {
            readRowSet.absolute(row + 1);
            return readRowSet.getString(columnName);
        } catch (SQLException e) {
            return ""; //Cannot log the error, this method is called several times
        }
    }
}
