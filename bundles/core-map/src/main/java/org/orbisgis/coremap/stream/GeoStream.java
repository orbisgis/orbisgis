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
package org.orbisgis.coremap.stream;

import java.awt.Image;
import java.io.IOException;

import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.progress.ProgressMonitor;

/**
 * A stream object that can be queried in order to get a specific image.
 * 
 * @author Vincent Dépériers
 */
public interface GeoStream {
        
        /**
         * Gets an image from the stream.
         * 
         * @param width the width
         * @param height the height
         * @param extent the required extent
         * @param pm Progress monitor
         * @return the resulting image
         * @throws IOException Communication exception
         */
         Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws IOException;
         
         /**
          * @return the full extend of the data behind this stream
          */
         Envelope getEnvelope();
         
         /**
          * @return the source of this stream
          */
         WMSStreamSource getStreamSource();
}
