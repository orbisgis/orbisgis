package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.MultiPolygon;

public class MultipolygonTool extends AbstractMultipolygonTool  {

	@Override
	protected void multipolygonDone(MultiPolygon mp) throws TransitionException {
		try {
			ec.newGeometry(mp);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

	public boolean isEnabled() {
		return ec.getActiveThemeGeometryType().equals(
				Primitive.MULTIPOLYGON_GEOMETRY_TYPE)
				&& ec.isActiveThemeWritable();
	}

	public boolean isVisible() {
		return true;
	}

}
