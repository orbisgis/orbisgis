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
package org.orbisgis.coremap.renderer.se.parameter.real;

import java.sql.ResultSet;
import java.util.Map;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;
import org.orbisgis.coremap.renderer.se.parameter.SeParameter;

/**
 * A {@code RealParameter} is a SE node that contains a numeric value, or that 
 * provides a direct mechanism to retrieve one in an external source.
 * @author Maxence Laurent, Alexis Guéganno
 */
public interface RealParameter extends SeParameter, Comparable {


        /**
         * Associates a new RealParameterContext to this RealParameter. Realizations of
         * this interface should modify (if possible) their embedded value (if any),
         * or the value returned with <code>getValue()</code> if it does not fit
         * in the new RealParameterContext.
         * @param ctx 
         */
        void setContext(RealParameterContext ctx);

        /**
         * Retrieve the context (if any) associated to this RealParameter. The context 
         * can be used to determine the range of valid values.
         * @return 
         */
        RealParameterContext getContext();

        /**
         * Tries to retrieve the value associated to this RealParameter in sds, at line fid.
         * Note that it is up to the realization of this method (or containing class) to provide the name or index
         * of the column where to look in in the datasource.
         * @param sds
         * @param fid
         * @return
         * @throws ParameterException 
         */
        Double getValue(ResultSet sds, long fid) throws ParameterException;

        /**
         * Tries to retrieve the value associated to this RealParameter in the map.
         * Note that it is up to the realization to know the key used to retrieve
         * the value in the map.
         * @param map
         * @return
         * @throws ParameterException
         */
        Double getValue(Map<String,Object> map) throws ParameterException;

        @Override
        String toString();
}
