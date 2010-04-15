package org.orbisgis.core.renderer.se;

import java.awt.geom.AffineTransform;

import com.vividsolutions.jts.geom.Geometry;

public interface Symbolizer {

	Uom getUom();

	void setUom(Uom uom);

	Geometry getGeometry();

	void setGeometry(Geometry geometry);

	AffineTransform getAffineTransform();

	void setAffineTransform(AffineTransform at);
}
