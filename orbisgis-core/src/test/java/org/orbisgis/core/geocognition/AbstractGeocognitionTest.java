package org.orbisgis.core.geocognition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.orbisgis.core.AbstractTest;
import org.orbisgis.plugins.core.geocognition.DefaultGeocognition;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.plugins.core.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.plugins.core.geocognition.symbology.GeocognitionSymbolFactory;

public class AbstractGeocognitionTest extends AbstractTest {
	protected Geocognition gc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		gc = new DefaultGeocognition();
		gc.addElementFactory(new GeocognitionSymbolFactory());
		gc.addElementFactory(new GeocognitionFunctionFactory());
		gc.addElementFactory(new GeocognitionCustomQueryFactory());
		gc.addElementFactory(new GeocognitionLegendFactory());
		gc.addElementFactory(new GeocognitionMapContextFactory());
	}

	protected void saveAndLoad() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos);
		gc.clear();
		gc.read(new ByteArrayInputStream(bos.toByteArray()));
	}

}
