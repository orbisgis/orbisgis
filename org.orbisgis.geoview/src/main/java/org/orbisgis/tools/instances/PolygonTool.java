package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.Geometry;

public class PolygonTool extends AbstractPolygonTool {

	@Override
	protected void polygonDone(com.vividsolutions.jts.geom.Polygon pol)
			throws TransitionException {
		Geometry g = pol;
		if (ec.getActiveThemeGeometryType() == Primitive.MULTIPOLYGON_GEOMETRY_TYPE) {
			g = ToolManager.toolsGeometryFactory
					.createMultiPolygon(new com.vividsolutions.jts.geom.Polygon[] { pol });
		}

		try {
			ec.newGeometry(g);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

	public boolean isEnabled() {
		return (ec.getActiveThemeGeometryType().equals(
				Primitive.POLYGON_GEOMETRY_TYPE) || ec
				.getActiveThemeGeometryType().equals(
						Primitive.MULTIPOLYGON_GEOMETRY_TYPE))
				&& ec.isActiveThemeWritable();
	}

	public boolean isVisible() {
		return true;
	}

}
