package org.gdms.data.edition;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBSourceCreation;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.ScaleConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.FileDriver;

import com.hardcode.driverManager.DriverManager;

public class DriverMetadataTest extends SourceTest {

	public void testAddField() throws Exception {
		DataSource d = dsf.getDataSource("sort");

		d.open();
		int fc = d.getDataSourceMetadata().getFieldCount();

		d.addField("new", new DefaultTypeDefinition("STRING", Type.STRING)
				.createType());
		// d.addField("nuevo", "STRING");

		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.STRING);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("STRING"));
		d.commit();

		d = dsf.getDataSource("sort");
		d.open();
		assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
		assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.STRING);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("STRING"));
		d.cancel();

		d = dsf.getDataSource("hsqldbpersona");

		d.open();
		fc = d.getDataSourceMetadata().getFieldCount();
		d.addField("new", new DefaultTypeDefinition("BIT", Type.BINARY)
				.createType());
		// d.addField("nuevo", "BIT");
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.BOOLEAN);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("BIT"));
		d.commit();

		d = dsf.getDataSource("hsqldbpersona");
		d.open();
		assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.BOOLEAN);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("BOOLEAN"));
		d.cancel();

		d = dsf.getDataSource("hsqldbpersona");

		d.open();
		fc = d.getDataSourceMetadata().getFieldCount();
		d.addField("new2", TypeFactory.createType(Type.STRING));
		// d.addField("nuevo2", "CHAR");

		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.STRING);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("CHAR"));
		assertTrue(d.getDataSourceMetadata().getFieldType(fc)
				.getConstraintValue(ConstraintNames.LENGTH) == null);
		// assertTrue(d.getDataSourceMetadata().getFieldParam(fc, "LENGTH") ==
		// null);
		d.commit();

		d = dsf.getDataSource("hsqldbpersona");
		d.open();
		assertTrue(d.getDataSourceMetadata().getFieldCount() == fc + 1);
		// assertTrue(d.getDriverMetadata().getFieldCount() == fc + 1);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc)
				.getConstraintValue(ConstraintNames.LENGTH) != null);
		// assertTrue(d.getDataSourceMetadata().getFieldParam(fc, "LENGTH") !=
		// null);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.STRING);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("CHAR"));
		d.cancel();
	}

	public void testDriverMetadataEdition() throws Exception {
		DataSource d = dsf.getDataSource("hsqldbpersona");

		d.open();
		int fc = d.getDataSourceMetadata().getFieldCount();
		d.addField("new", TypeFactory.createType(Type.STRING, "CHAR",
				new Constraint[] { new LengthConstraint(5) }));
		// d.addField("nuevo", "CHAR", new String[] { "LENGTH" },
		// new String[] { "5" });
		assertTrue(d.getDataSourceMetadata().getFieldType(fc)
				.getConstraintValue(ConstraintNames.LENGTH).equals("5"));
		// assertTrue(d.getDataSourceMetadata().getFieldParam(fc, "LENGTH")
		// .equals("5"));
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.STRING);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("CHAR"));
		d.commit();

		d = dsf.getDataSource("hsqldbpersona");
		d.open();
		assertTrue(d.getDataSourceMetadata().getFieldType(fc)
				.getConstraintValue(ConstraintNames.LENGTH).equals("5"));
		// assertTrue(d.getDataSourceMetadata().getFieldParam(fc, "LENGTH")
		// .equals("5"));
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getTypeCode() == Type.STRING);
		assertTrue(d.getDataSourceMetadata().getFieldType(fc).getDescription()
				.equals("CHAR"));
		d.cancel();
	}

	public void testCheckInput() throws Exception {
		final int fc = 4;
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];

		// 1st field
		fieldsNames[0] = "text";
		fieldsTypes[0] = TypeFactory.createType(Type.STRING, "STRING",
				new Constraint[] { new LengthConstraint(5) });

		// 2nd field
		fieldsNames[1] = "tiny integer";
		fieldsTypes[1] = TypeFactory.createType(Type.INT, "TINYINT",
				new Constraint[] { new PrimaryKeyConstraint() });

		// 3rd field
		fieldsNames[2] = "decimal number";
		fieldsTypes[2] = TypeFactory.createType(Type.FLOAT, "DECIMAL",
				new Constraint[] { new ScaleConstraint(2),
						new PrecisionConstraint(5) });

		// 4th field
		fieldsNames[3] = "other decimal number";
		fieldsTypes[3] = TypeFactory.createType(Type.DOUBLE, "DECIMAL");

		DefaultMetadata ddm = new DefaultMetadata(fieldsTypes, fieldsNames);

		// 5th field
		ddm.addField("text2", Type.STRING, "CHAR");
		// fieldsNames[4] = "text2";
		// fieldsTypes[4] = TypeFactory.createType(Value.STRING, "CHAR");

		// DefaultDriverMetadata ddm = new DefaultDriverMetadata();
		// ddm.addField("texto", "VARCHAR", new String[] { "LENGTH" },
		// new String[] { "5" });
		// ddm.addField("entero", "TINYINT");
		// ddm.addField("decimal", "DECIMAL",
		// new String[] { "SCALE", "PRECISION" },
		// new String[] { "2", "5" });
		// ddm.addField("decimal2", "DECIMAL");
		// ddm.addField("texto2", "CHAR");
		// ddm.setPrimaryKey(new String[] { "entero" });

		DBSource dbsd = new DBSource(null, 0, "src/test/resources/testdb",
				"sa", "", "nuevo", "jdbc:hsqldb:file");
		dsf.createDataSource(new DBSourceCreation(dbsd, ddm));
		dsf.registerDataSource("nuevoDataSource", new DBTableSourceDefinition(
				dbsd));
		DataSource d = dsf.getDataSource("nuevoDataSource");
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
		final DriverManager dm = dsf.getDriverManager();
		final FileDriver fd = (FileDriver) dm.getDriver("csv string");
		final DBDriver dd = (DBDriver) dm.getDriver("GDBMS HSQLDB driver");

		TypeDefinition[] typesDef = fd.getTypesDefinitions();
		assertTrue(typesDef.length == 1);
		assertTrue(typesDef[0].getTypeName().equals("STRING"));
		assertTrue(typesDef[0].getConstraints() == null);

		typesDef = dd.getTypesDefinitions();
		assertTrue(typesDef.length > 0);

		for (int i = 0; i < typesDef.length; i++) {
			if (typesDef[i].equals("CHAR")) {
				assertTrue(null != dd.getMetadata().getFieldType(i)
						.getConstraintValue(ConstraintNames.LENGTH));
				// params = dd.getParameters(types[i]);
				// assertTrue(params[0].equals("LENGTH"));
				// assertTrue(params.length == 1);
			} else if (typesDef[i].equals("DECIMAL")) {
				assertTrue(null != dd.getMetadata().getFieldType(i)
						.getConstraintValue(ConstraintNames.PRECISION));
				assertTrue(null != dd.getMetadata().getFieldType(i)
						.getConstraintValue(ConstraintNames.SCALE));
				// params = dd.getParameters(types[i]);
				// assertTrue(params[0].equals("PRECISION"));
				// assertTrue(params[1].equals("SCALE"));
				// assertTrue(params.length == 2);
			}
		}
	}
}
