package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.MultiPolygon;

public class MultipolygonTool extends AbstractMultipolygonTool {

	@Override
	protected void multipolygonDone(MultiPolygon mp, ViewContext vc,
			ToolManager tm) throws TransitionException {
		try {
			vc.newGeometry(mp);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		return vc.getActiveThemeGeometryType().equals(
				Primitive.MULTIPOLYGON_GEOMETRY_TYPE)
				&& vc.isActiveThemeWritable();
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

}
