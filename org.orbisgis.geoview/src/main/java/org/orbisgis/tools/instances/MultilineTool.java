package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.MultiLineString;

public class MultilineTool extends AbstractMultilineTool {

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		return ((vc.getActiveThemeGeometryType() == Primitive.MULTILINE_GEOMETRY_TYPE))
				&& vc.isActiveThemeWritable();
	}

	@Override
	protected void multilineDone(MultiLineString mls, ViewContext vc,
			ToolManager tm) throws TransitionException {
		try {
			vc.newGeometry(mls);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

}
