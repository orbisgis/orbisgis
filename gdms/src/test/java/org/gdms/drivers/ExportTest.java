package org.gdms.drivers;

import org.gdms.data.DataSource;
import org.gdms.data.DigestUtilities;
import org.gdms.data.ExecutionException;
import org.gdms.driver.DriverException;
import org.gdms.sql.parser.ParseException;
import org.gdms.sql.strategies.Instruction;
import org.gdms.sql.strategies.SQLProcessor;
import org.gdms.sql.strategies.SemanticException;

public class ExportTest extends AbstractDBTest {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		deleteTable(getH2Source("h2landcoverfromshp"));
		deleteTable(getPostgreSQLSource("pglandcoverfromshp"));
	}

	public void testSHP2H22PostgreSQL2SHP() throws Exception {
		String script = "select register('../../datas2tests/shp/mediumshape2D/landcover2000.shp', "
				+ "'landcover2000');";

		script += "select register('h2','', '0', "
				+ "'src/test/resources/backup/h2landcoverfromshp',"
				+ "'sa','','h2landcoverfromshp', 'h2landcoverfromshp');";
		script += "create table h2landcoverfromshp as select * from landcover2000;";

		script += "select register('postgresql','127.0.0.1', '5432', "
				+ "'gdms','postgres','postgres','pglandcoverfromshp', 'pglandcoverfromshp');";
		script += "create table pglandcoverfromshp as select * from h2landcoverfromshp;";

		script += "select register('src/test/resources/backup/landcoverfrompg.shp', 'res');";
		script += "create table res as select * from pglandcoverfromshp;";
		executeGDMSScript(script);

		DataSource dsRes = dsf
				.getDataSourceFromSQL("select the_geom, type, gid "
						+ "from res order by gid");
		DataSource ds = dsf.getDataSourceFromSQL("select the_geom, type, gid "
				+ "from landcover2000 order by gid");
		ds.open();
		dsRes.open();
		String d1 = DigestUtilities.getBase64Digest(ds);
		String d2 = DigestUtilities.getBase64Digest(dsRes);
		ds.cancel();
		dsRes.cancel();
		assertTrue(d1.equals(d2));
	}

	public void testSHP2PostgreSQL2H22SHP() throws Exception {
		String script = "select register('../../datas2tests/shp/mediumshape2D/landcover2000.shp', "
				+ "'landcover2000');";

		script += "select register('postgresql','127.0.0.1', '5432', "
				+ "'gdms','postgres','postgres','pglandcoverfromshp', 'pglandcoverfromshp');";
		script += "create table pglandcoverfromshp as select * from landcover2000;";

		script += "select register('h2','', '0', "
				+ "'src/test/resources/backup/h2landcoverfromshp',"
				+ "'sa','','h2landcoverfromshp', 'h2landcoverfromshp');";
		script += "create table h2landcoverfromshp as select * from pglandcoverfromshp;";

		script += "select register('src/test/resources/backup/landcoverfrompg.shp', 'res');";
		script += "create table res as select * from h2landcoverfromshp;";
		executeGDMSScript(script);

		DataSource dsRes = dsf
				.getDataSourceFromSQL("select the_geom, type, gid "
						+ "from res order by gid");
		DataSource ds = dsf.getDataSourceFromSQL("select the_geom, type, gid "
				+ "from landcover2000 order by gid");
		ds.open();
		dsRes.open();
		String d1 = DigestUtilities.getBase64Digest(ds);
		String d2 = DigestUtilities.getBase64Digest(dsRes);
		ds.cancel();
		dsRes.cancel();
		assertTrue(d1.equals(d2));
	}

	private void executeGDMSScript(String script) throws SemanticException,
			DriverException, ParseException {
		SQLProcessor sqlProcessor = new SQLProcessor(dsf);
		Instruction[] instructions = sqlProcessor.prepareScript(script);
		for (Instruction instruction : instructions) {
			try {
				instruction.execute(null);
			} catch (ExecutionException e) {
				throw new RuntimeException("Error in " + instruction.getSQL(),
						e);
			} catch (SemanticException e) {
				throw new RuntimeException("Error in " + instruction.getSQL(),
						e);
			} catch (DriverException e) {
				throw new RuntimeException("Error in " + instruction.getSQL(),
						e);
			}
		}
	}
}
