package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.Geometry;

public class PolygonTool extends AbstractPolygonTool {

	@Override
	protected void polygonDone(com.vividsolutions.jts.geom.Polygon pol,
			ViewContext vc, ToolManager tm) throws TransitionException {
		Geometry g = pol;
		if (vc.getActiveThemeGeometryType() == Primitive.MULTIPOLYGON_GEOMETRY_TYPE) {
			g = ToolManager.toolsGeometryFactory
					.createMultiPolygon(new com.vividsolutions.jts.geom.Polygon[] { pol });
		}

		try {
			vc.newGeometry(g);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		return (vc.getActiveThemeGeometryType().equals(
				Primitive.POLYGON_GEOMETRY_TYPE) || vc
				.getActiveThemeGeometryType().equals(
						Primitive.MULTIPOLYGON_GEOMETRY_TYPE))
				&& vc.isActiveThemeWritable();
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

}
