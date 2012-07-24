package org.gdms.data.stream;

import java.awt.Image;

import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.driver.DriverException;

/**
 *
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
         * @param pm 
         * @return the resulting image
         * @throws DriverException  
         */
         public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException;
         
         /**
          * Get envelope
          * 
          * @return 
          */
         public Envelope getEnvelope();
         
         /**
          * Get the StreamSource
          * 
          * @return 
          */
         public StreamSource getStreamSource();
}
