package org.gdms.data.stream;

import java.awt.Image;

import com.vividsolutions.jts.geom.Envelope;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.driver.DriverException;
import org.gdms.driver.StreamDriver;

/**
 * Stream object that manages metadata between the datasource and the stream driver
 *
 * @author Antoine Gourlay
 * @author Vincent Dépériers
 */
public class DefaultGeoStream implements GeoStream {

        private Envelope envelope;
        private StreamDriver streamDriver;
        private StreamSource streamSource;

        public DefaultGeoStream(StreamDriver driver, StreamSource source, Envelope env) {
                streamDriver = driver;
                streamSource = source;
                envelope = env;
        }

        @Override
        public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException {
                return streamDriver.getMap(width, height, extent, pm);
        }

        /**
         * @return the envelope of the GeoStream
         */
        @Override
        public Envelope getEnvelope() {
                return envelope;
        }

        /**
         * Returns the Source of the Stream
         *
         * @return
         */
        @Override
        public StreamSource getStreamSource() {
                return this.streamSource;
        }
}
