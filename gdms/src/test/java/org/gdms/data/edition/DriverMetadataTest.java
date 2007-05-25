package org.gdms.data.edition;

import org.gdms.SourceTest;
import org.gdms.data.InternalDataSource;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.FileDriver;

import com.hardcode.driverManager.DriverManager;

public class DriverMetadataTest extends SourceTest {

    public void testAddField() throws Exception {
        InternalDataSource d = dsf.getDataSource("sort");

        d.open();
        int fc = d.getDataSourceMetadata().getFieldCount();
        d.addField("nuevo", "STRING");
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.STRING);
        assertTrue(d.getDriverMetadata().getFieldType(fc) == "STRING");
        d.commit();

        d = dsf.getDataSource("sort");
        d.open();
        assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
        assertTrue(d.getDriverMetadata().getFieldCount() == fc + 1);
        assertTrue(d.getDriverMetadata().getFieldType(fc) == "STRING");
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.STRING);
        d.cancel();

        d = dsf.getDataSource("hsqldbpersona");

        d.open();
        fc = d.getDataSourceMetadata().getFieldCount();
        d.addField("nuevo", "BIT");
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.BOOLEAN);
        assertTrue(d.getDriverMetadata().getFieldType(fc).equals("BIT"));
        d.commit();

        d = dsf.getDataSource("hsqldbpersona");
        d.open();
        assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
        assertTrue(d.getDriverMetadata().getFieldCount() == fc + 1);
        assertTrue(d.getDriverMetadata().getFieldType(fc).equals("BOOLEAN"));
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.BOOLEAN);
        d.cancel();

        d = dsf.getDataSource("hsqldbpersona");

        d.open();
        fc = d.getDataSourceMetadata().getFieldCount();
        d.addField("nuevo2", "CHAR");
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.STRING);
        assertTrue(d.getDriverMetadata().getFieldType(fc).equals("CHAR"));
        assertTrue(d.getDriverMetadata().getFieldParam(fc, "LENGTH") == null);
        d.commit();

        d = dsf.getDataSource("hsqldbpersona");
        d.open();
        assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
        assertTrue(d.getDriverMetadata().getFieldCount() == fc + 1);
        assertTrue(d.getDriverMetadata().getFieldType(fc).equals("CHAR"));
        assertTrue(d.getDriverMetadata().getFieldParam(fc, "LENGTH") != null);
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.STRING);
        d.cancel();
    }

    public void testDriverMetadataEdition() throws Exception {
        InternalDataSource d = dsf.getDataSource("hsqldbpersona");

        d.open();
        int fc = d.getDataSourceMetadata().getFieldCount();
        d.addField("nuevo", "CHAR", new String[]{"LENGTH"}, new String[]{"5"});
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.STRING);
        assertTrue(d.getDriverMetadata().getFieldType(fc).equals("CHAR"));
        assertTrue(d.getDriverMetadata().getFieldParam(fc, "LENGTH").equals("5"));
        d.commit();

        d = dsf.getDataSource("hsqldbpersona");
        d.open();
        assertTrue(d.getDataSourceMetadata().getFieldType(fc) == Value.STRING);
        assertTrue(d.getDriverMetadata().getFieldType(fc).equals("CHAR"));
        assertTrue(d.getDriverMetadata().getFieldParam(fc, "LENGTH").equals("5"));
        d.cancel();
    }

    public void testCheckInput() throws Exception {
        DefaultDriverMetadata ddm = new DefaultDriverMetadata();
        ddm.addField("texto", "VARCHAR", new String[]{"LENGTH"}, new String[]{"5"});
        ddm.addField("entero", "TINYINT");
        ddm.addField("decimal", "DECIMAL", new String[]{"SCALE", "PRECISION"}, new String[]{"2", "5"});
        ddm.addField("decimal2", "DECIMAL");
        ddm.addField("texto2", "CHAR");
        ddm.setPrimaryKey(new String[]{"entero"});
        DBSource dbsd = new DBSource(null, 0, "src/test/resources/testdb", "sa", "",
        "nuevo", "jdbc:hsqldb:file");
        dsf.createDataSource(new DBSourceCreation(dbsd, ddm));
        dsf.registerDataSource("nuevoDataSource", new DBTableSourceDefinition(dbsd));
        InternalDataSource d = dsf.getDataSource("nuevoDataSource");
        d.open();
        assertTrue(d.check(0, ValueFactory.createNullValue()) == null);
        assertTrue(d.check(0, ValueFactory.createValue("")) == null);
        assertTrue(d.check(0, ValueFactory.createValue("aa")) == null);
        assertTrue(d.check(0, ValueFactory.createValue("aaaaaa")) != null);
        assertTrue(d.check(1, ValueFactory.createNullValue()) == null);
        assertTrue(d.check(1, ValueFactory.createValue((byte) 2)) == null);
        assertTrue(d.check(2, ValueFactory.createValue(2234.3)) == null);
        assertTrue(d.check(2, ValueFactory.createValue(23432.3)) != null);
        assertTrue(d.check(2, ValueFactory.createValue(2.323)) != null);
        d.cancel();
    }

    public void testDriverTypes() throws Exception {
        DriverManager dm = dsf.getDriverManager();
        FileDriver fd = (FileDriver) dm.getDriver("csv string");
        DBDriver dd = (DBDriver) dm.getDriver("GDBMS HSQLDB driver");

        String[] types = fd.getAvailableTypes();
        String[] params = fd.getParameters(types[0]);
        assertTrue(types.length == 1);
        assertTrue(types[0].equals("STRING"));
        assertTrue(params.length == 0);

        types = dd.getAvailableTypes();
        params = dd.getParameters("CHAR");
        assertTrue(types.length > 0);
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals("CHAR")) {
                params = dd.getParameters(types[i]);
                assertTrue(params[0].equals("LENGTH"));
                assertTrue(params.length == 1);
            } else if (types[i].equals("DECIMAL")) {
                params = dd.getParameters(types[i]);
                assertTrue(params[0].equals("PRECISION"));
                assertTrue(params[1].equals("SCALE"));
                assertTrue(params.length == 2);
            }
        }
    }
}
