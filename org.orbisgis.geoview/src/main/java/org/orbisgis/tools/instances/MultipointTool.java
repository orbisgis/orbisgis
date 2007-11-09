package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.TransitionException;

import com.vividsolutions.jts.geom.MultiPoint;

public class MultipointTool extends AbstractMultipointTool {

	@Override
	protected void multipointDone(MultiPoint mp) throws TransitionException {
		try {
			ec.newGeometry(mp);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

	public boolean isEnabled() {
		return ec.getActiveThemeGeometryType().equals(
				Primitive.MULTIPOINT_GEOMETRY_TYPE)
				&& ec.isActiveThemeWritable();
	}

	public boolean isVisible() {
		return true;
	}

}
