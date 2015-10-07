/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.controller.processexecution.dataprocessing;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.*;
import org.orbisgis.orbistoolbox.controller.processexecution.ExecutionWorker;
import org.orbisgis.orbistoolbox.controller.processexecution.utils.FormatFactory;
import org.orbisgis.orbistoolbox.model.*;

import java.net.URI;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

public class GeometryProcessing implements ProcessingData {
    @Override
    public Class<? extends DataDescription> getDataClass() {
        return GeometryData.class;
    }

    @Override
    public void preProcessing(DescriptionType inputOrOutput, ExecutionWorker executionWorker) throws Exception {
        Map<URI, Object> dataMap = executionWorker.getDataMap();
        if(inputOrOutput instanceof Input) {
            Input input = (Input) inputOrOutput;
            GeometryData geometryData = (GeometryData)input.getDataDescription();
            for (Format f : geometryData.getFormats()){
                if(f.isDefaultFormat()){
                    if(f.getMimeType().equals(FormatFactory.WKT_MIMETYPE)){
                        WKTReader reader = new WKTReader();
                        String wkt = dataMap.get(input.getIdentifier()).toString();
                        dataMap.put(input.getIdentifier(), reader.read(wkt));
                    }
                }
            }
        }
    }

    @Override
    public void postProcessing(DescriptionType inputOrOutput, ExecutionWorker executionWorker) throws Exception {
        Map<URI, Object> dataMap = executionWorker.getDataMap();
        if(inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            GeometryData geometryData = (GeometryData)output.getDataDescription();
            for (Format f : geometryData.getFormats()){
                if(f.isDefaultFormat()){
                    if(f.getMimeType().equals(FormatFactory.WKT_MIMETYPE)){
                        WKTWriter writer = new WKTWriter();
                        Geometry geom = (Geometry)dataMap.get(output.getIdentifier());
                        dataMap.put(output.getIdentifier(), writer.write(geom));
                    }
                }
            }
        }
    }
}
