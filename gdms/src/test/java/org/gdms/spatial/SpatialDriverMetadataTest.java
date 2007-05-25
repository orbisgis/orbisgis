package org.gdms.spatial;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.PTTypes;

public class SpatialDriverMetadataTest extends SourceTest {

	public void testHasSpatialField() throws Exception {
		String[] resources = super.getSpatialResources();
		for (String resource : resources) {
			testHasSpatialField(resource);
		}
	}

	private void testHasSpatialField(String dsName) throws Exception {
		SpatialDataSource sds = new SpatialDataSourceDecorator(dsf
				.getDataSource(dsName));
		sds.open();
		DriverMetadata sdm = sds.getDriverMetadata();
		boolean has = false;
		for (int i = 0; i < sdm.getFieldCount(); i++) {
			if (sdm.getFieldType(i).equals(PTTypes.STR_GEOMETRY)) {
				has = true;
				break;
			}
		}

		assertTrue(has);
		sds.cancel();
	}

	public void testSeveralGeometriesInOneSource() throws Exception {
		DataSource ds = dsf.getDataSource(new SeveralSpatialFieldsDriver());
		SpatialDataSource sds = new SpatialDataSourceDecorator(ds);
		sds.open();
		sds.setDefaultGeometry("geom1");
		assertTrue(sds.getGeometry(0).equals(sds.getGeometry("geom1", 0)));
		assertFalse(sds.getGeometry(0).equals(sds.getGeometry("geom2", 0)));
		assertFalse(sds.getGeometry(0).equals(sds.getGeometry("geom3", 0)));
		sds.setDefaultGeometry("geom2");
		assertFalse(sds.getGeometry(0).equals(sds.getGeometry("geom1", 0)));
		assertTrue(sds.getGeometry(0).equals(sds.getGeometry("geom2", 0)));
		assertFalse(sds.getGeometry(0).equals(sds.getGeometry("geom3", 0)));
		sds.setDefaultGeometry("geom3");
		assertFalse(sds.getGeometry(0).equals(sds.getGeometry("geom1", 0)));
		assertFalse(sds.getGeometry(0).equals(sds.getGeometry("geom2", 0)));
		assertTrue(sds.getGeometry(0).equals(sds.getGeometry("geom3", 0)));
		sds.cancel();

		sds.open();
		sds.buildIndex("geom2");
		Metadata metadata = sds.getDataSourceMetadata();
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			if (sds.isIndexed(metadata.getFieldName(i))) {
				assertTrue(sds.queryIndex(metadata.getFieldName(i), sds.getFullExtent()) != null);
			} else {
				try {
					sds.queryIndex(metadata.getFieldName(i), sds.getFullExtent());
					assertTrue(false);
				} catch (NullPointerException e) {
					assertTrue(true);
				}
			}
		}

		for (int i = 0; i < metadata.getFieldCount(); i++) {
			sds.setFieldValue(0, i, ValueFactory.createNullValue());
		}
		sds.commit();

		sds.open();
		for (int j = 0; j < sds.getDataSourceMetadata().getFieldCount(); j++) {
			assertTrue(sds.isNull(0, j));
		}
		sds.cancel();
	}

	@Override
	protected void setUp() throws Exception {
		this.setWritingTests(false);
		super.setUp();
	}

}
