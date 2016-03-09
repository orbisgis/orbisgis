package org.orbisgis.wpsclient.view.utils;

import org.orbisgis.corejdbc.ReadRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.sql.SQLException;

/**
 * This class extends AbstractListModel and is used as a JList model.
 * It particularity is to use a ReadRowSet to retrieve the element to display in the list.
 *
 * @author Sylvain PALOMINOS
 */
public class FieldValueListModel extends AbstractListModel<String> {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(FieldValueListModel.class);

    /** ReadRowSet used to retrieve the element list to display. */
    private ReadRowSet readRowSet;
    /** Name of the column which the element will be taken. */
    private String columnName;

    /** Main construction
     *
     * @param readRowSet RowSet to use to retrieve the elements.
     * @param columnName Column from which the element are taken.
     */
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
