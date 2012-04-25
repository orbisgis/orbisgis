/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.raster.create;

import java.awt.geom.Rectangle2D;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.Crop;

import com.vividsolutions.jts.geom.Envelope;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.spatial.raster.AbstractScalarRasterFunction;

/**
 * Crops the raster in the first argument with the
 * geometry in the second one. The result is the cropped raster
 */
public final class ST_CropRaster extends AbstractScalarRasterFunction {

        @Override
        public Value evaluate(DataSourceFactory dsf, Value[] args) throws FunctionException {
                GeoRaster gr = args[0].getAsRaster();
                Envelope g = args[1].getAsGeometry().getEnvelopeInternal();

                Envelope intersection = gr.getMetadata().getEnvelope().intersection(g);
                Rectangle2D rect = new Rectangle2D.Double(intersection.getMinX(),
                        intersection.getMinY(), intersection.getWidth(), intersection.getHeight());
                try {
                        GeoRaster ret = gr.doOperation(new Crop(rect));
                        return ValueFactory.createValue(ret);
                } catch (OperationException e) {
                        throw new FunctionException("Cannot crop", e);
                }
        }

        @Override
        public String getDescription() {
                return "Crops the raster in the first argument with the "
                        + "geometry in the second one. The result is the cropped raster";
        }

        @Override
        public String getName() {
                return "ST_CropRaster";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_CropRaster(r.raster, f.the_geom) as raster from mytif r, fence f;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.GEOMETRY)
                        };
        }
}
