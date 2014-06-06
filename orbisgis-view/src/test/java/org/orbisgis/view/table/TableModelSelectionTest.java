package org.orbisgis.view.table;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.viewapi.edition.EditableSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 * @author Nicolas Fortin
 */
public class TableModelSelectionTest {

    @Test
    public void selectionTest() throws SQLException {
            DataSource dataSource = SpatialH2UT.createDataSource("target/"+TableModelSelectionTest.class.getSimpleName(), true);
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
}
