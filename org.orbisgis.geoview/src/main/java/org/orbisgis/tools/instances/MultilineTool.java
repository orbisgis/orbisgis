package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.instances.generated.AbstractMultilineTool;

import com.vividsolutions.jts.geom.MultiLineString;

public class MultilineTool extends AbstractMultilineTool {

	public boolean isVisible() {
		return true;
	}

	public boolean isEnabled() {
		return ((ec.getActiveThemeGeometryType() == Primitive.MULTILINE_GEOMETRY_TYPE))
				&& ec.isActiveThemeWritable();
	}

	@Override
	protected void multilineDone(MultiLineString mls) throws TransitionException {
		try {
			ec.newGeometry(mls);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

}
