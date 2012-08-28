/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui.legend;

import java.util.ArrayList;
import org.orbisgis.view.toc.actions.cui.LegendContext;
import org.orbisgis.view.toc.actions.cui.legends.*;

public class EPLegendHelper {

    private EPLegendHelper(){}

	/**
	 * Create an arraylist with all legend panels used by the edit legend plugin. 
	 */
	public static ILegendPanel[] getLegendPanels(LegendContext legendContext) {

		ArrayList<ILegendPanel> legends = new ArrayList<ILegendPanel>();
                //UniqueLine
                ILegendPanel pnlUniqueLine = new PnlUniqueLineSE();
                pnlUniqueLine.initialize(legendContext);
                legends.add(pnlUniqueLine);
                //UniquePoint
                ILegendPanel pnlUniquePoint = new PnlUniquePointSE();
                pnlUniquePoint.initialize(legendContext);
                legends.add(pnlUniquePoint);
                //UniqueArea
                ILegendPanel pnlUniqueArea = new PnlUniqueAreaSE();
                pnlUniqueArea.initialize(legendContext);
                legends.add(pnlUniqueArea);
                //ProportionalPoint
                ILegendPanel proportionalPoint = new PnlProportionalPointSE();
                proportionalPoint.initialize(legendContext);
                legends.add(proportionalPoint);
                //ProportionalLine
                ILegendPanel proportionalLine = new PnlProportionalLine();
                proportionalLine.initialize(legendContext);
                legends.add(proportionalLine);
		return legends.toArray(new ILegendPanel[legends.size()]);
	}

}
