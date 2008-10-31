package org.orbisgis.geocognition;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.orbisgis.AbstractTest;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.geocognition.symbology.GeocognitionSymbolFactory;

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
		gc.addElementFactory(new GeocognitionActionElementFactory());
	}

	protected void saveAndLoad() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		gc.write(bos);
		gc.clear();
		gc.read(new ByteArrayInputStream(bos.toByteArray()));
	}

}
