package org.orbisgis.tablegui.impl;

import org.h2gis.h2spatial.CreateSpatialExtension;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.utilities.SFSUtilities;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.editorjdbc.EditableSource;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.tablegui.api.TableEditableElement;
import org.orbisgis.tablegui.impl.filters.FieldsContainsFilterFactory;
import org.orbisgis.tablegui.impl.filters.TableSelectionFilter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Nicolas Fortin
 */
public class TableModelSelectionTest {
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SFSUtilities.wrapSpatialDataSource(SpatialH2UT.createDataSource(TableModelSelectionTest.class.getSimpleName()
                , false));
        try(Connection connection = dataSource.getConnection()) {
            CreateSpatialExtension.initSpatialExtension(connection);
        }
    }

    @Test
    public void selectionTest() throws SQLException {
            DataManager dataManager = new DataManagerImpl(dataSource);
            // Create table without index
            try(Connection connection = dataSource.getConnection();
                Statement st = connection.createStatement()) {
                st.execute("DROP TABLE IF EXISTS TEST");
                st.execute("CREATE TABLE TEST(the_geom POINT, id integer auto_increment)");
                st.execute("INSERT INTO TEST VALUES ('POINT(1 1)', 1), ('POINT(2 2)', 2), ('POINT(3 3)', 3)");
                EditableSource editableSource = new TableEditableElementImpl("TEST", dataManager);
                DataSourceTableModel dataSourceTableModel = new DataSourceTableModel(editableSource);
                assertEquals("POINT (1 1)", dataSourceTableModel.getValueAt(0, 0).toString());
            }
    }

    @Test
    public void testSelectSameCells() throws SQLException {

        DataManager dataManager = new DataManagerImpl(dataSource);
        // Create table without index
        try(Connection connection = dataSource.getConnection();
            Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TEST");
            st.execute("CREATE TABLE TEST(the_geom POINT, id integer primary key, searchval VARCHAR(50))");
            st.execute("INSERT INTO TEST VALUES ('POINT(1 1)', 1, 'test'), ('POINT(2 2)', 5, 'hello')," +
                    " ('POINT(3 3)', 10, 'hello home')");
            EditableSource editableSource = new TableEditableElementImpl("TEST", dataManager);
            DataSourceTableModel dataSourceTableModel = new DataSourceTableModel(editableSource);
            assertEquals("POINT (1 1)", dataSourceTableModel.getValueAt(0, 0).toString());
        }
        DefaultActiveFilter filter = new FieldsContainsFilterFactory.
                FilterParameters(2, "hello", true, false);
        FieldsContainsFilterFactory fieldsContainsFilterFactory = new FieldsContainsFilterFactory(null);
        TableSelectionFilter tableSelectionFilter = fieldsContainsFilterFactory.getFilter(filter);
        TableEditableElement editableElement = new TableEditableElementImpl("TEST", dataManager);
        tableSelectionFilter.initialize(new NullProgressMonitor(), editableElement);
        assertFalse(tableSelectionFilter.isSelected(0, editableElement));
        assertTrue(tableSelectionFilter.isSelected(1, editableElement));
        assertTrue(tableSelectionFilter.isSelected(2, editableElement));
    }
}
