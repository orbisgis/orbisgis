package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.orbisgis.core.renderer.persistance.se.AreaSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.LineSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.PointSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.RasterSymbolizerType;
import org.orbisgis.core.renderer.persistance.se.SymbolizerType;
import org.gdms.data.feature.Feature;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.Drawer;
import org.orbisgis.core.renderer.persistance.se.TextSymbolizerType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;

/**
 * Entry point for all kind of symbolizer
 * This abstract class contains only the name, the geom and a description of the symbolizer
 * @todo Add a general draw method that fit well for vectors and raster; implement fetch default geometry
 * @author maxence
 */
public abstract class Symbolizer implements SymbolizerNode, Comparable {

    protected static final String DEFAULT_NAME = "Default Symbolizer";
    protected static final String VERSION = "1.9";

 	protected String name;
    protected String desc;
    protected GeometryAttribute the_geom;

    private SymbolizerNode parent;

    protected long level;

    public Symbolizer() {
        name = Symbolizer.DEFAULT_NAME;
        desc = "";
		level = -1;
    }

	@Override
	public String toString(){
		return name;
	}

    public Symbolizer(JAXBElement<? extends SymbolizerType> st) {
        SymbolizerType t = st.getValue();

        if (t.getName() != null) {
            this.name = t.getName();
        } else {
            this.name = Symbolizer.DEFAULT_NAME;
        }

        if (t.getVersion() != null && ! t.getVersion().equals(Symbolizer.VERSION)) {
            System.out.println("Unsupported Style version!");
        }

        if (t.getDescription() != null){
            // TODO  implement ows:Description
        }
		level = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
		if (name == null || name.equalsIgnoreCase(""))
			this.name = Symbolizer.DEFAULT_NAME;
		else
        	this.name = name;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String description) {
        desc = description;
    }

    public long getLevel() {
        return level;
    }

    public void setLevel(long level) {
        this.level = level;
    }

    public GeometryAttribute getGeometry() {
        return the_geom;
    }

    public void setGeometry(GeometryAttribute the_geom) {
        this.the_geom = the_geom;
    }

    public Geometry getTheGeom(Feature feat) throws DriverException, ParameterException {
        if (the_geom != null) {
            return the_geom.getTheGeom(feat);
        } else {
			return feat.getGeometry();
        }
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        this.parent = node;
    }

    public void setJAXBProperty(SymbolizerType s) {
		// TODO Load description from XML
        s.setDescription(null);
        s.setName(name);
        s.setVersion("1.9");
    }

    public void setPropertiesFromJAXB(SymbolizerType st) {
        if (st.getName() != null) {
            this.name = st.getName();
        }

        if (st.getDescription() != null) {
			// TODO Load description from XML
        }

        /*if (st.getVersion() != null){
         * // TODO IMplement
        }*/
    }

    public static Symbolizer createSymbolizerFromJAXBElement(JAXBElement<? extends SymbolizerType> st) throws InvalidStyle {
        if (st.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.AreaSymbolizerType.class) {
            return new AreaSymbolizer((JAXBElement<AreaSymbolizerType>) st);
        } else if (st.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.LineSymbolizerType.class) {
            return new LineSymbolizer((JAXBElement<LineSymbolizerType>) st);
        } else if (st.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.PointSymbolizerType.class) {
            return new PointSymbolizer((JAXBElement<PointSymbolizerType>) st);
        } else if (st.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.TextSymbolizerType.class) {
            return new TextSymbolizer((JAXBElement<TextSymbolizerType>) st);
        } else if (st.getDeclaredType() == org.orbisgis.core.renderer.persistance.se.RasterSymbolizerType.class) {
            return new RasterSymbolizer((JAXBElement<RasterSymbolizerType>) st);
        } else {
            System.out.println("NULL => " + st.getDeclaredType());
            return null;
        }
    }

    @Override
    public int compareTo(Object o) {
         Symbolizer s = (Symbolizer)o;

         if (s.level < this.level)
             return 1;
         else if (s.level == this.level)
             return 0;
         else
             return -1;
    }

	/**
	 * Go through parents and return the rule
	 */
    public Rule getRule(){
        SymbolizerNode pIt = this.parent;
        while (! (pIt instanceof Rule)){
            pIt = pIt.getParent();
        }

        return (Rule)pIt;
    }

    public abstract void draw(Graphics2D g2, Feature feat, boolean selected, MapTransform mt) throws ParameterException, IOException, DriverException;

	public abstract void draw(Drawer drawer, long fid, boolean selected);

    public abstract JAXBElement<? extends SymbolizerType> getJAXBElement();
}
