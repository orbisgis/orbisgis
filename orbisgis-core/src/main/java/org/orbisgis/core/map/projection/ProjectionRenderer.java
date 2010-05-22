package org.orbisgis.core.map.projection;

import javax.swing.Icon;

public interface ProjectionRenderer {

	Icon getIcon(ProjectionManager projectionManager, String projection);
	
	String getText(ProjectionManager projectionManager, String projection);
	
}
