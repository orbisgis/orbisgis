package org.orbisgis.view.table.filters;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.commons.progress.NullProgressMonitor;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.view.table.TableEditableElementImpl;
import org.orbisgis.viewapi.table.TableEditableElement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test of org.orbisgis.view.table.filters classes
 * @author Nicolas Fortin
 */
public class FilterTest {
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUp() throws SQLException {
        dataManager = new DataManagerImpl(SpatialH2UT.createDataSource("FilterTest",true));
    }

    @Test
    public void testFieldEquals() throws SQLException {
        try(Connection connection = dataManager.getDataSource().getConnection();
            Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS TABLE_TO_FILTER");
            st.execute("CREATE TABLE TABLE_TO_FILTER(id BIGINT PRIMARY KEY AUTO_INCREMENT, type VARCHAR(15))");
            st.execute("INSERT INTO TABLE_TO_FILTER(type) VALUES ('office')");
            st.execute("INSERT INTO TABLE_TO_FILTER(type) VALUES ('hotel')");
            st.execute("INSERT INTO TABLE_TO_FILTER(type) VALUES ('industry')");
            st.execute("INSERT INTO TABLE_TO_FILTER(type) VALUES ('school')");
            st.execute("INSERT INTO TABLE_TO_FILTER(type) VALUES ('industry')");
            st.execute("INSERT INTO TABLE_TO_FILTER(type) VALUES ('office')");
        }
        TableEditableElement editable = new TableEditableElementImpl("TABLE_TO_FILTER", dataManager);
        FieldsContainsFilterFactory factory = new FieldsContainsFilterFactory(null);
        final int columnId = 1;
        final String searchedChars = "office";
        final boolean matchCase = false;
        final boolean wholeWord = false;
        DefaultActiveFilter filterParameters = new FieldsContainsFilterFactory.FilterParameters(columnId, searchedChars,
                matchCase, wholeWord);
        TableSelectionFilter filter = factory.getFilter(filterParameters);
        filter.initialize(new NullProgressMonitor(), editable);
        assertTrue(filter.isSelected(0, editable));
        assertFalse(filter.isSelected(1, editable));
        assertTrue(filter.isSelected(5, editable));
    }
}
