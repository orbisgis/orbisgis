//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.04.14 at 03:50:39 PM CEST 
//

package org.orbisgis.plugins.core.renderer.legend.carto.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for raster-legend-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name=&quot;raster-legend-type&quot;&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base=&quot;{org.orbisgis.legend}legend-type&quot;&gt;
 *       &lt;sequence&gt;
 *         &lt;element name=&quot;color-model-component&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}int&quot; maxOccurs=&quot;unbounded&quot;/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name=&quot;opacity&quot; use=&quot;required&quot; type=&quot;{http://www.w3.org/2001/XMLSchema}float&quot; /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "raster-legend-type", propOrder = { "colorModelComponent" })
public class RasterLegendType extends LegendType {

	@XmlElement(name = "color-model-component", type = Integer.class)
	protected List<Integer> colorModelComponent;
	@XmlAttribute(required = true)
	protected float opacity;

	/**
	 * Gets the value of the colorModelComponent property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the colorModelComponent property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getColorModelComponent().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Integer }
	 * 
	 * 
	 */
	public List<Integer> getColorModelComponent() {
		if (colorModelComponent == null) {
			colorModelComponent = new ArrayList<Integer>();
		}
		return this.colorModelComponent;
	}

	/**
	 * Gets the value of the opacity property.
	 * 
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Sets the value of the opacity property.
	 * 
	 */
	public void setOpacity(float value) {
		this.opacity = value;
	}

}
