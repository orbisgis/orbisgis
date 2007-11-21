package org.orbisgis.tools.instances;

import org.orbisgis.tools.EditionContextException;
import org.orbisgis.tools.Primitive;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.MultiPoint;

public class MultipointTool extends AbstractMultipointTool {

	@Override
	protected void multipointDone(MultiPoint mp, ViewContext vc, ToolManager tm)
			throws TransitionException {
		try {
			vc.newGeometry(mp);
		} catch (EditionContextException e) {
			throw new TransitionException(e);
		}
	}

	public boolean isEnabled(ViewContext vc, ToolManager tm) {
		return vc.getActiveThemeGeometryType().equals(
				Primitive.MULTIPOINT_GEOMETRY_TYPE)
				&& vc.isActiveThemeWritable();
	}

	public boolean isVisible(ViewContext vc, ToolManager tm) {
		return true;
	}

}
