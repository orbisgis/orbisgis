package org.orbisgis.plugin.view.layerModel;

import java.io.StringReader;

import org.gdms.data.SpatialDataSource;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class VectorLayer extends BasicLayer {

	private SpatialDataSource dataSource;

	public VectorLayer(String name, final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		String xml = "  <UserStyle>" + " <FeatureTypeStyle>" + "  <Rule>"
				+ "   <LineSymbolizer>" + "    <Stroke>"
				+ "     <CssParameter name=\"stroke\">#ff00ff</CssParameter>"
				+ "     <CssParameter name=\"width\">3.0</CssParameter>"
				+ "    </Stroke>" + "   </LineSymbolizer>" + "  </Rule>"
				+ "  <Rule>" + "   <LineSymbolizer>" + "    <Stroke>"
				+ "     <CssParameter name=\"stroke\">#ffffff</CssParameter>"
				+ "     <CssParameter name=\"width\">1.5</CssParameter>"
				+ "    </Stroke>" + "   </LineSymbolizer>" + "  </Rule>"
				+ " </FeatureTypeStyle>" + "</UserStyle>";
		StyleFactory sf = StyleFactoryFinder.createStyleFactory();
		SLDParser parser = new SLDParser(sf);
		parser.setInput(new StringReader(xml));
		setStyle(parser.readXML()[0]);
	}

	public void set(SpatialDataSource dataSource, Style style) throws Exception {
		this.dataSource = dataSource;
		setStyle(style);
	}

	public SpatialDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(SpatialDataSource dataSource) {
		this.dataSource = dataSource;
	}
}