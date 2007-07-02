package org.orbisgis.plugin.view.layerModel;

import java.io.StringReader;

import org.gdms.driver.DriverException;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class VectorLayer extends BasicLayer {

	private SpatialDataSourceDecorator dataSource;

	public VectorLayer(String name,
			final CoordinateReferenceSystem coordinateReferenceSystem) {
		super(name, coordinateReferenceSystem);
		String xml = "  <UserStyle>" + " <FeatureTypeStyle>" + "  <Rule>"
				+ "   <LineSymbolizer>" + "    <Stroke>"
				+ "     <CssParameter name=\"stroke\">#949494</CssParameter>"
				+ "     <CssParameter name=\"width\">1.0</CssParameter>"
				+ "    </Stroke>" + "   </LineSymbolizer>" + "  </Rule>"
				+ "<Rule><PointSymbolizer><Graphic><Mark>"
				+ "<WellKnownName>circle</WellKnownName><Fill>"
				+ "<CssParameter name=\"fill\">#000000</CssParameter>"
				+ "</Fill></Mark><Size>5.0</Size></Graphic>"
				+ "</PointSymbolizer></Rule>" + " </FeatureTypeStyle>"
				+ "</UserStyle>";
		StyleFactory sf = StyleFactoryFinder.createStyleFactory();
		SLDParser parser = new SLDParser(sf);
		parser.setInput(new StringReader(xml));
		setStyle(parser.readXML()[0]);
	}

	public void set(SpatialDataSourceDecorator dataSource, Style style)
			throws Exception {
		this.dataSource = dataSource;
		setStyle(style);
	}

	public SpatialDataSourceDecorator getDataSource() {
		return dataSource;
	}

	public void setDataSource(SpatialDataSourceDecorator dataSource) {
		this.dataSource = dataSource;
	}

	public Envelope getEnvelope() {
		Envelope result = new Envelope();

		if (null != dataSource) {
			try {
				dataSource.open();
				result = dataSource.getFullExtent();
				dataSource.cancel();
			} catch (DriverException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}