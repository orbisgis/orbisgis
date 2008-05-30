package org.gdms.sql.customQuery;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.gdms.sql.strategies.SemanticException;

public class CustomQueryTest extends TestCase {

	public void testRegister() throws Exception {
		RegisterCall rc = new RegisterCall();

		// from clause
		rc.validateTables(new Metadata[0]);
		try {
			rc.validateTables(new Metadata[1]);
		} catch (SemanticException e) {
		}

		// parameters
		Type dummy = TypeFactory.createType(Type.STRING);
		try {
			rc.validateTypes(new Type[] {});
		} catch (IncompatibleTypesException e) {
		}
		rc.validateTypes(new Type[] { dummy });
		rc.validateTypes(new Type[] { dummy, dummy });
		rc
				.validateTypes(new Type[] { dummy, dummy, dummy, dummy, dummy,
						dummy });
		rc.validateTypes(new Type[] { dummy, dummy, dummy, dummy, dummy, dummy,
				dummy, dummy });
		try {
			rc.validateTypes(new Type[] { dummy, dummy, dummy });
		} catch (IncompatibleTypesException e) {
		}
		try {
			rc.validateTypes(new Type[] { dummy, dummy, dummy, dummy });
		} catch (IncompatibleTypesException e) {
		}
		try {
			rc.validateTypes(new Type[] { dummy, dummy, dummy, dummy, dummy });
		} catch (IncompatibleTypesException e) {
		}
	}

	public void testRegisterDefaultSource() throws Exception {
		DataSourceFactory dsf = new DataSourceFactory();
		File resultDir = new File("src/test/resources/backup");
		dsf.setResultDir(resultDir);
		dsf.executeSQL("select register('toto')");
		Source src = dsf.getSourceManager().getSource("toto");
		assertTrue((src.getType() & SourceManager.GDMS) == SourceManager.GDMS);
		assertTrue(src.getFile().getParentFile().equals(resultDir));
	}
}
