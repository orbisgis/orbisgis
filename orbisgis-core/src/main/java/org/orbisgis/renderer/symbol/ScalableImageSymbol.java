package org.orbisgis.renderer.symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.net.URL;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.gdms.driver.DriverException;
import org.orbisgis.renderer.RenderPermission;
import org.orbisgis.renderer.liteShape.LiteShape;
import org.w3c.dom.svg.SVGDocument;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class ScalableImageSymbol extends ImageSymbol {

	private GVTBuilder builder;
	private BridgeContext context;
	private SVGDocument document;
	private double width, height;
	private boolean selected;

	public ScalableImageSymbol() {
		UserAgent userAgent = new UserAgentAdapter();
		context = new BridgeContext(userAgent, new DocumentLoader(userAgent));
		context.setDynamicState(BridgeContext.DYNAMIC);
		builder = new GVTBuilder();

		// TODO parametrize
		width = height = 10;
		selected = false;
	}

	public Envelope draw(Graphics2D g, Geometry geom, AffineTransform at,
			RenderPermission permission) throws DriverException {
		if (document == null) {
			return null;
		}

		LiteShape ls = new LiteShape(geom, at, false);
		PathIterator pi = ls.getPathIterator(null);
		double[] coords = new double[6];
		while (!pi.isDone()) {
			pi.currentSegment(coords);

			GraphicsNode rootGN = builder.build(context, document);
			double scaledWidth = width * at.getScaleX();
			double scaledHeight = -height * at.getScaleY();

			double scaleX = scaledWidth / rootGN.getBounds().getWidth();
			double scaleY = scaledHeight / rootGN.getBounds().getHeight();
			AffineTransform transform = AffineTransform.getTranslateInstance(
					coords[0] - scaledWidth / 2, coords[1] - scaledHeight / 2);
			transform.concatenate(AffineTransform.getScaleInstance(scaleX,
					scaleY));
			rootGN.setTransform(transform);
			rootGN.paint(g);

			if (selected) {
				Color c = g.getColor();
				g.setColor(Color.red);
				int outsideX = (int) Math.round(coords[0] - scaledWidth * 8
						/ 14);
				int outsideY = (int) Math.round(coords[1] - scaledHeight * 8
						/ 14);
				int outsideWidth = (int) Math.round((2 * scaledWidth / 14)
						+ scaledWidth);
				int outsideHeight = (int) Math.round((2 * scaledHeight / 14)
						+ scaledHeight);
				g.drawRect(outsideX, outsideY, outsideWidth, outsideHeight);
				g.setColor(c);
			}

			pi.next();
		}

		return null;
	}

	@Override
	public void setImageURL(URL url) throws IOException {
		this.url = url;
		try {
			SAXSVGDocumentFactory documentFactory = new SAXSVGDocumentFactory(
					XMLResourceDescriptor.getXMLParserClassName());
			document = documentFactory.createSVGDocument(url.toString());
		} catch (IOException e) {
			document = null;
		}
	}

	public Symbol cloneSymbol() {
		ScalableImageSymbol ret = new ScalableImageSymbol();
		ret.img = this.img;
		ret.url = this.url;
		return ret;
	}

	public String getClassName() {
		return "Scalable image on point";
	}

	public String getId() {
		return "org.orbisgis.symbols.point.ScalableImage";
	}
}
