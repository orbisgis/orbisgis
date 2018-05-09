/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import org.locationtech.jts.geom.Envelope;
import org.orbisgis.commons.progress.ProgressMonitor;

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
