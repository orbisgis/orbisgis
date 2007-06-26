package org.gdms.utility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.driver.DriverException;
import org.gdms.geotoolsAdapter.FeatureCollectionAdapter;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.map.DefaultMapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.StyleFactoryFinder;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class Utility extends JPanel {

	private Envelope envelope;

	private Coordinate center;

	private StreamingRenderer liteRenderer;

	private static Style defaultStyle;

	public static Style defaultStyleBis;
	static {
		try {
			defaultStyle = loadStyle("  <UserStyle>"
					+ " <FeatureTypeStyle>"
					+ "  <Rule>"
					+ "   <LineSymbolizer>"
					+ "    <Stroke>"
					+ "     <CssParameter name=\"stroke\">#ff00ff</CssParameter>"
					+ "     <CssParameter name=\"width\">1.0</CssParameter>"
					+ "    </Stroke>"
					+ "   </LineSymbolizer>"
					+ "  </Rule>"
					+ "  <Rule>"
					+ "   <LineSymbolizer>"
					+ "    <Stroke>"
					+ "     <CssParameter name=\"stroke\">#ffff00</CssParameter>"
					+ "     <CssParameter name=\"width\">1.5</CssParameter>"
					+ "    </Stroke>" + "   </LineSymbolizer>" + "  </Rule>"
					+ " </FeatureTypeStyle>" + "</UserStyle>");
			defaultStyleBis = loadStyle("  <UserStyle>"
					+ " <FeatureTypeStyle>"
					+ "  <Rule>"
					+ "   <LineSymbolizer>"
					+ "    <Stroke>"
					+ "     <CssParameter name=\"stroke\">#ff00ff</CssParameter>"
					+ "     <CssParameter name=\"width\">1.0</CssParameter>"
					+ "    </Stroke>"
					+ "   </LineSymbolizer>"
					+ "  </Rule>"
					+ "  <Rule>"
					+ "   <LineSymbolizer>"
					+ "    <Stroke>"
					+ "     <CssParameter name=\"stroke\">#ff0000</CssParameter>"
					+ "     <CssParameter name=\"width\">1.5</CssParameter>"
					+ "    </Stroke>" + "   </LineSymbolizer>" + "  </Rule>"
					+ " </FeatureTypeStyle>" + "</UserStyle>");
		} catch (IOException e) {
		}
	}

	public void show(final DataSource[] dataSource) throws DriverException,
			IOException {
		show(dataSource, null);
	}

	public void show(final DataSource[] dataSource, Style[] style)
			throws DriverException, IOException {

		final DefaultMapContext mapContext = new DefaultMapContext();
		final CoordinateReferenceSystem currentCRS = mapContext
				.getCoordinateReferenceSystem();
		liteRenderer = new StreamingRenderer();

		if (null == style) {
			style = new Style[dataSource.length];
		}

		for (int i = 0; i < dataSource.length; i++) {
			if ((style.length < i) || (null == style[i])) {
				style[i] = defaultStyle;
			}

			if (dataSource[i] instanceof SpatialDataSource) {
				dataSource[i].open();
				FeatureCollectionAdapter fc = new FeatureCollectionAdapter(
						new SpatialDataSourceDecorator(dataSource[i]));
				mapContext.addLayer(fc, style[i]);

				if (null == envelope) {
					envelope = new Envelope() {
						{
							expandToInclude(mapContext.getLayer(0)
									.getFeatureSource().getFeatures()
									.getBounds());
						}
					};
				} else {
					envelope.expandToInclude(new Envelope() {
						{
							expandToInclude(mapContext.getLayer(0)
									.getFeatureSource().getFeatures()
									.getBounds());
						}
					});
				}
			}
		}
		center = new Coordinate(
				((envelope.getMinX() + envelope.getMaxX()) / 2), ((envelope
						.getMinY() + envelope.getMaxY()) / 2));

		liteRenderer.setContext(mapContext);

		JFrame frame = new JFrame("My simple GDMS viewer");
		frame.setSize(500, 500);
		frame.add(this);

		frame.show();
	}

	private static Style loadStyle(final String xml) throws IOException {
		return new SLDParser(StyleFactoryFinder.createStyleFactory()) {
			{
				setInput(new StringReader(xml));
			}
		}.readXML()[0];
	}

	public void paintComponent(final Graphics graphics) {
		_paintComponent((Graphics2D) graphics);
	}

	private void _paintComponent(final Graphics2D graphics2D) {
		graphics2D.setRenderingHints(new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON));

		// Create an affine transform that maps the feature collection onto
		// the center of the JPanel, maintaining aspect ratio.
		final AffineTransform affineTransform = new AffineTransform() {
			{
				final double scaleFactor = (Math.min((getWidth() / envelope
						.getWidth()), (getHeight() / envelope.getHeight())) * 1);

				// Translate to the center of the JPanel.
				translate((getWidth() / 2), (getHeight() / 2));

				// Scale with negative y factor to correct the orientation.
				scale(scaleFactor, -scaleFactor);

				// Translate to the center of the feature collection.
				translate(-center.x, -center.y);
			}
		};

		liteRenderer.paint(graphics2D, getBounds(), affineTransform);

		// Render to an image buffer.
		BufferedImage bufferedImage = new BufferedImage(getWidth(),
				getHeight(), BufferedImage.TYPE_INT_RGB) {
			{
				paint((Graphics2D) getGraphics());
			}

			private void paint(final Graphics2D graphics2D) {
				graphics2D.setRenderingHints(new RenderingHints(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON));
				graphics2D.setColor(Color.white);
				graphics2D.fillRect(0, 0, getWidth(), getHeight());
				liteRenderer.paint(graphics2D, new Rectangle(0, 0, getWidth(),
						getHeight()), affineTransform);
			}
		};

	}

	public static void main(String[] args) throws Exception {

		File src1 = new File(
				"../../datas2tests/shp/mediumshape2D/bzh5_communes.shp");

		File src2 = new File(
				"../../datas2tests/shp/mediumshape2D/landcover2000.shp");

		DataSourceFactory dsf = new DataSourceFactory();
		new Utility().show(new DataSource[] {
				new SpatialDataSourceDecorator(dsf.getDataSource(src1)),
				new SpatialDataSourceDecorator(dsf.getDataSource(src2)) },
				new Style[]{null, defaultStyleBis});

		System.out.println("Passage");

	}

	public static Style loadStyleFromXml(String url) throws Exception {

		StyleFactory factory = StyleFactoryFinder.createStyleFactory();

		SLDParser parser = new SLDParser(factory, url);

		Style style = parser.readXML()[0];

		return style;
	}
}
