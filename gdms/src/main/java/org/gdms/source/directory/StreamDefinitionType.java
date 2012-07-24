package org.gdms.source.directory;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.stream.StreamSourceDefinition;

/**
 *
 * @author Vincent Dépériers
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Stream-definition-type")
public class StreamDefinitionType extends DefinitionType {

        @XmlAttribute(required = true)
        protected String host;
        @XmlAttribute(required = true)
        protected String port;
        @XmlAttribute(name = "layer-name", required = true)
        protected String layerName;
        @XmlAttribute(required = true)
        protected String type;    
        @XmlAttribute(required = true)
        protected String format;
        @XmlAttribute(required = true)
        protected String srs;
        

        /**
         * Gets the value of the host property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHost() {
                return host;
        }

        /**
         * Sets the value of the host property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHost(String value) {
                this.host = value;
        }

        /**
         * Gets the value of the port property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPort() {
                return port;
        }

        /**
         * Sets the value of the port property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPort(String value) {
                this.port = value;
        }

        /**
         * Gets the value of the tableName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLayerName() {
                return layerName;
        }

        /**
         * Sets the value of the tableName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLayerName(String value) {
                this.layerName = value;
        }

      /**
         * Gets the value of the type property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
                return type;
        }

        /**
         * Sets the value of the type property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
                this.type = value;
        }
        
        /**
         * Gets the value of the format property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getImageFormat() {
                return format;
        }

        /**
         * Sets the value of the format property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setImageFormat(String value) {
                this.format = value;
        }
        
        /**
         * Gets the value of the srs property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSRS() {
                return srs;
        }

        /**
         * Sets the value of the srs property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSRS(String value) {
                this.srs = value;
        }

        @Override
        public DataSourceDefinition toDataSourceDefinition() {
                return StreamSourceDefinition.createFromXML(this);
        }
}
