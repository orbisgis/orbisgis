package org.gdms.data.stream;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Image;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.ProgressMonitor;

/**
 *
 * 
 * @author Vincent Dépériers
 */


public interface GeoStream {
        
        /**
         * Get map from remot server
         * 
         * @param width
         * @param height
         * @param extent
         * @return 
         */
         public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException;
         
         /**
          * Get envelope
          * 
          * @return 
          */
         public Envelope getEnvelope();
         
         /**
          * Set envelope
          * 
          */
         public void setEnvelope(Envelope envelope);
         
         /**
          * Get the StreamSource
          * 
          * @return 
          */
         public StreamSource getStreamSource();
         
         /**
          * Set the StreamSource
          * 
          * @param streamSource 
          */
         public void setStreamSource(StreamSource streamSource);
}
