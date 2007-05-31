package org.gdms.utility;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.geotoolsAdapter.FeatureCollectionAdapter;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.map.DefaultMapContext;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLDParser;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleAttributeExtractor;
import org.geotools.styling.StyleFactoryFinder;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

/**
 * <p>
 * The <span style="color: green;">How to render a shape file, style with SLD,
 * zoom, pan</span> geotools snippet.
 * </p>
 *
 * <p>
 * The example I wished I had had when I started to work with Geotools. It tries
 * to show the basic things needed to render a shape file. I adapted the
 * <code>LiteRendererTest2.java</code> example. This example
 * <ul>
 * <li> reads polylines from a shape file, </li>
 * <li> uses a {@link org.geotools.renderer.lite.LiteRenderer} to render the
 * polylines, </li>
 * <li> uses SLD (Styled Layer Descriptor) to describe the
 * {@link org.geotools.styling.Style} to use, </li>
 * <li> includes a few buttons to zoom and pan the rendered image, and </li>
 * <li> additionally renders the image to a .png file. </li>
 * </ul>
 * </p>
 *
 * <p>
 * I developed this snippet with <span style="color: gray; font-weight:
 * bold;">jdk 1.5</span> and <span style="color: gray; font-weight:
 * bold;">geotools 2.1.M4</span>.
 * </p>
 *
 * <p>
 * Compile with
 *
 * <pre style="color: blue;">
 *     javac -cp JTS-1.6.jar:geoapi-20050403.jar:gt2-main.jar:gt2-shapefile.jar LiteRendererJFrame.java
 * </pre>
 *
 * </p>
 *
 * <p>
 * Run with
 *
 * <pre style="color: blue;">
 *     java -cp JTS-1.6.jar:units-0.01.jar:geoapi-20050403.jar:gt2-main.jar:gt2-shapefile.jar:. LiteRendererJFrame
 * </pre>
 *
 * </p>
 *
 * <p>
 * Good luck.
 * </p>
 *
 * @author Arjan Vermeij
 */
public class DatasourceRenderer extends JFrame {
	private static CoordinateReferenceSystem currentCRS = null;

	public static void main(final String[] arguments) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new DatasourceRenderer();
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private final LiteRendererJPanel liteRendererJPanel;

	public DatasourceRenderer() throws Exception {
		this.liteRendererJPanel = new LiteRendererJPanel(null, null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().add(this.liteRendererJPanel, BorderLayout.CENTER);
		getContentPane().add(new JButton(new AbstractAction("<<") {
			public void actionPerformed(final ActionEvent actionEvent) {
				DatasourceRenderer.this.liteRendererJPanel.panLeft();
			}
		}), BorderLayout.WEST);
		getContentPane().add(new JButton(new AbstractAction(">>") {
			public void actionPerformed(final ActionEvent actionEvent) {
				DatasourceRenderer.this.liteRendererJPanel.panRight();
			}
		}), BorderLayout.EAST);
		getContentPane().add(new JButton(new AbstractAction("Zoom Out") {
			public void actionPerformed(final ActionEvent actionEvent) {
				DatasourceRenderer.this.liteRendererJPanel.zoomOut();
			}
		}), BorderLayout.NORTH);
		getContentPane().add(new JButton(new AbstractAction("Zoom In") {
			public void actionPerformed(final ActionEvent actionEvent) {
				DatasourceRenderer.this.liteRendererJPanel.zoomIn();
			}
		}), BorderLayout.SOUTH);
		setSize(400, 400);
		setVisible(true);
	}

	private class LiteRendererJPanel extends JPanel {
		private final GTRenderer liteRenderer;

		private final Envelope envelope;

		private final Coordinate center;

		private double zoomFactor = 1.0;

		/**
		 * @throws IOException
		 * @throws DataSourceCreationException
		 * @throws DataSourceCreationException
		 * @throws DriverException
		 * @throws FactoryException
		 * @throws TransformException
		 * @throws ExecutionException 
		 * @throws NoSuchTableException 
		 * @throws DriverLoadException 
		 * @throws SyntaxException 
		 * @throws DriverLoadException
		 * @throws UnsupportedImageModelException 
		 */
		public LiteRendererJPanel(DataSource ds, Style style) throws IOException,
				DataSourceCreationException, DriverException, TransformException, FactoryException, SyntaxException, DriverLoadException, NoSuchTableException, ExecutionException {
			

			final DefaultMapContext mapContext = new DefaultMapContext();
			
			currentCRS = mapContext.getCoordinateReferenceSystem();

			if (ds instanceof SpatialDataSourceDecorator) {
				
				FeatureCollectionAdapter fc = new FeatureCollectionAdapter(new SpatialDataSourceDecorator(ds));
			
				mapContext.addLayer(fc, style);
				
				
			}
			
						

			this.liteRenderer = new StreamingRenderer();
			this.liteRenderer.setContext(mapContext);
			this.envelope = new Envelope() {
				{
					//for (final MapLayer mapLayer : mapContext.getLayers()) {
						expandToInclude(mapContext.getLayer(0).getFeatureSource()
								.getFeatures().getBounds());
					//}
				}
			};
			this.center = new Coordinate(
					((this.envelope.getMinX() + this.envelope.getMaxX()) / 2),
					((this.envelope.getMinY() + this.envelope.getMaxY()) / 2));
		}

		public void panLeft() {
			this.center.x -= ((this.envelope.getWidth() / 3) / this.zoomFactor);
			repaint();
		}

		public void panRight() {
			this.center.x += ((this.envelope.getWidth() / 3) / this.zoomFactor);
			repaint();
		}

		public void zoomIn() {
			this.zoomFactor *= 1.25;
			repaint();
		}
		
		

		public void zoomOut() {
			this.zoomFactor /= 1.25;
			repaint();
		}

		private double getZoomFactor() {
			return this.zoomFactor;
		}

		public void paintComponent(final Graphics graphics) {
			if (isOpaque()) {
				graphics.setColor(getBackground());
				graphics.fillRect(0, 0, getWidth(), getHeight());
			}
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
					final double scaleFactor = (Math.min(
							(getWidth() / LiteRendererJPanel.this.envelope
									.getWidth()),
							(getHeight() / LiteRendererJPanel.this.envelope
									.getHeight())) * getZoomFactor());

					// Translate to the center of the JPanel.
					translate((getWidth() / 2), (getHeight() / 2));

					// Scale with negative y factor to correct the orientation.
					scale(scaleFactor, -scaleFactor);

					// Translate to the center of the feature collection.
					translate(-LiteRendererJPanel.this.center.x,
							-LiteRendererJPanel.this.center.y);
				}
			};

			this.liteRenderer.paint(graphics2D, getBounds(), affineTransform);

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
					LiteRendererJPanel.this.liteRenderer.paint(graphics2D,
							new Rectangle(0, 0, getWidth(), getHeight()),
							affineTransform);
				}
			};
			
		}

		private Style loadStyle(final String xml) throws IOException {
			return new SLDParser(StyleFactoryFinder.createStyleFactory()) {
				{
					setInput(new StringReader(xml));
				}
			}.readXML()[0];
		}

		private void visit(final Style style) {
			style.accept(new StyleAttributeExtractor() {
				public void visit(final Stroke stroke) {
					System.out.println("start of stroke " + stroke);
					super.visit(stroke);
					System.out.println("end of stroke " + stroke);
				}
			});
		}
	};
		
	
}