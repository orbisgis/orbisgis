package org.orbisgis.plugin.view.layerModel;

import java.io.StringReader;

import org.gdms.spatial.SpatialDataSource;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class VectorLayer extends BasicLayer {

	private SpatialDataSource dataSource;

	public VectorLayer(String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		String xml = "  <UserStyle>" + " <FeatureTypeStyle>" + "  <Rule>"
				+ "   <LineSymbolizer>" + "    <Stroke>"
				+ "     <CssParameter name=\"stroke\">#949494</CssParameter>"
				+ "     <CssParameter name=\"width\">1.0</CssParameter>"
				+ "    </Stroke>" + "   </LineSymbolizer>" + "  </Rule>"
				+ "<Rule><PointSymbolizer><Graphic><Mark>"
				+ "<WellKnownName>star</WellKnownName><Fill>"
				+ "<CssParameter name=\"fill\">#ff0000</CssParameter>"
				+ "</Fill></Mark><Size>10.0</Size></Graphic>"
				+ "</PointSymbolizer></Rule>" + " </FeatureTypeStyle>"
				+ "</UserStyle>";
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