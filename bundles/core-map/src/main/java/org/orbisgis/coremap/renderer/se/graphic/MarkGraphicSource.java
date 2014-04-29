/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.se.graphic;

import java.awt.Shape;
import java.io.IOException;
import java.util.Map;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;

/**
 * This interface allow to fetch a mark graphic for many sources,
 *
 * @author Maxence Laurent
 * @todo implement in InlineContent(for se InlineContent && GML), OnlineResource
 */
public interface MarkGraphicSource {
	/**
	 * Get the shape associated to this {@code MarkGraphicSource}
	 * @param viewBox
	 * @param feat
	 * @param scale
	 * @param dpi
	 * @param markIndex
	 *
	 * @return
	 *
	 * @throws ParameterException
	 * @throws IOException
	 */
    Shape getShape(ViewBox viewBox, Map<String,Object> map, Double scale, Double dpi,
            RealParameter markIndex, String format)
            throws ParameterException, IOException;

    //public void setJAXBSource(MarkGraphicType m);

    double getDefaultMaxWidth(Map<String,Object> map,
            Double scale, Double dpi, RealParameter markIndex, String mimeType)
            throws ParameterException, IOException;
}
