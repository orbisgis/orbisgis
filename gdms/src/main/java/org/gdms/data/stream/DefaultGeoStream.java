package org.gdms.data.stream;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.Image;
import org.gdms.driver.DriverException;
import org.gdms.driver.StreamDriver;
import org.orbisgis.progress.ProgressMonitor;

/**
 * Stream object that manages metadata between the datasource and the stream driver
 * 
 * @author Vincent Dépériers
 */


public class DefaultGeoStream implements GeoStream {

        private Image m_Image;
        private Envelope m_Envelope;
        private int m_Width;
        private int m_Height;
        private StreamDriver m_StreamDriver;
        private StreamSource m_StreamSource;
        
        public DefaultGeoStream(StreamDriver driver, StreamSource source) {
                this.m_StreamDriver = driver;
                this.m_StreamSource = source;
        }
        
        @Override
        public Image getMap(int width, int height, Envelope extent, ProgressMonitor pm) throws DriverException{
                this.m_Width = width;
                this.m_Height = height;
                this.m_Envelope = extent;
                
                this.m_Image = m_StreamDriver.getMap(width, height, extent, pm);
                
                return this.m_Image;
        }
        
        @Override
        public Envelope getEnvelope() {
                return m_Envelope;
        }

        @Override
        public void setEnvelope(Envelope envelope){
                this.m_Envelope = envelope;
        }

        @Override
        public StreamSource getStreamSource() {
                return this.m_StreamSource;
        }

        @Override
        public void setStreamSource(StreamSource streamSource) {
                this.m_StreamSource = streamSource;
        }
}
