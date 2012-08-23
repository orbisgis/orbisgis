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
package org.orbisgis.core.renderer.se.parameter.color;

import java.awt.Color;
import java.util.Map;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;


/**
 * Represent a color parameter
 * According to XSD, this color should not embed any <code>alpha</code> value !
 * Consequently, if <code>alpha</code> is defined within a ColorParameter, the value will be loosed
 * at serialization time !
 * @author Maxence Laurent
 */
public interface ColorParameter extends SeParameter {

    /**
     * Retrieve the colour associated to this parameter, from the datasource sds, at index fid.
     * @param sds
     * @param fid
     * @return
     * @throws ParameterException 
     */
    Color getColor(DataSource sds, long fid) throws ParameterException;

    /**
     * Retrieve the colour associated to this parameter, from the datasource sds, at index fid.
     * @param sds
     * @param fid
     * @return
     * @throws ParameterException
     */
    Color getColor(Map<String,Value> map) throws ParameterException;
}
