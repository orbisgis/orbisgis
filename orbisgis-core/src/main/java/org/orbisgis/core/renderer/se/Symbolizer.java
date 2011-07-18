package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.AreaSymbolizerType;
import net.opengis.se._2_0.core.ExtensionParameterType;
import net.opengis.se._2_0.core.LineSymbolizerType;
import net.opengis.se._2_0.core.PointSymbolizerType;
import net.opengis.se._2_0.core.SymbolizerType;
import net.opengis.se._2_0.raster.RasterSymbolizerType;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import net.opengis.se._2_0.core.ExtensionType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.TextSymbolizerType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.ShapeHelper;
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
    protected static final String VERSION = "2.0.0";
    protected String name;
    protected String desc;
    protected GeometryAttribute the_geom;
    private SymbolizerNode parent;
    protected int level;

    public Symbolizer() {
        name = Symbolizer.DEFAULT_NAME;
        desc = "";
        level = -1;
    }

    @Override
    public String toString() {
        return name;
    }

    public Symbolizer(JAXBElement<? extends SymbolizerType> st) throws InvalidStyle {
        SymbolizerType t = st.getValue();

        if (t.getName() != null) {
            this.name = t.getName();
        } else {
            this.name = Symbolizer.DEFAULT_NAME;
        }

        if (t.getVersion() != null && !t.getVersion().equals(Symbolizer.VERSION)) {
            System.out.println("Unsupported Style version!");
            throw new InvalidStyle("Unsupported version !");
        }

        if (t.getDescription() != null) {
            // TODO  implement ows:Description
        }

        if (t.getExtension() != null) {
            for (ExtensionParameterType param : t.getExtension().getExtensionParameter()) {
                if (param.getName().equalsIgnoreCase("level")) {
                    level = Integer.parseInt(param.getContent());
                    break;
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.equalsIgnoreCase("")) {
            this.name = Symbolizer.DEFAULT_NAME;
        } else {
            this.name = name;
        }
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String description) {
        desc = description;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public GeometryAttribute getGeometry() {
        return the_geom;
    }

    public void setGeometry(GeometryAttribute the_geom) {
        this.the_geom = the_geom;
    }

    public Geometry getTheGeom(SpatialDataSourceDecorator sds, long fid) throws DriverException, ParameterException {
        if (the_geom != null) {
            return the_geom.getTheGeom(sds, fid);
        } else {
            int fieldId = ShapeHelper.getGeometryFieldId(sds);
            return sds.getFieldValue(fid, fieldId).getAsGeometry();
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
        ObjectFactory of = new ObjectFactory();

        // TODO Load description from XML
        s.setDescription(null);
        s.setName(name);
        s.setVersion(Symbolizer.VERSION);

        ExtensionType exts = of.createExtensionType();
        ExtensionParameterType param = of.createExtensionParameterType();
        param.setName("level");
        param.setContent("" + level);
        exts.getExtensionParameter().add(param);

        s.setExtension(exts);
    }

    /*public void setPropertiesFromJAXB(SymbolizerType st) {
    if (st.getName() != null) {
    this.name = st.getName();
    }

    if (st.getDescription() != null) {
    // TODO Load description from XML
    }

    if (st.getVersion() != null){
    // TODO IMplement
    }
    }*/
    public static Symbolizer createSymbolizerFromJAXBElement(JAXBElement<? extends SymbolizerType> st) throws InvalidStyle {
        if (st.getDeclaredType() == AreaSymbolizerType.class) {
            return new AreaSymbolizer((JAXBElement<AreaSymbolizerType>) st);
        } else if (st.getDeclaredType() == LineSymbolizerType.class) {
            return new LineSymbolizer((JAXBElement<LineSymbolizerType>) st);
        } else if (st.getDeclaredType() == PointSymbolizerType.class) {
            return new PointSymbolizer((JAXBElement<PointSymbolizerType>) st);
        } else if (st.getDeclaredType() == TextSymbolizerType.class) {
            return new TextSymbolizer((JAXBElement<TextSymbolizerType>) st);
        } else if (st.getDeclaredType() == RasterSymbolizerType.class) {
            return new RasterSymbolizer((JAXBElement<RasterSymbolizerType>) st);
        } else {
            System.out.println("NULL => " + st.getDeclaredType());
            return null;
        }
    }

    @Override
    public int compareTo(Object o) {
        Symbolizer s = (Symbolizer) o;

        if (s.level < this.level) {
            return 1;
        } else if (s.level == this.level) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Go through parents and return the rule
     */
    public Rule getRule() {
        SymbolizerNode pIt = this.parent;
        while (!(pIt instanceof Rule)) {
            pIt = pIt.getParent();
        }

        return (Rule) pIt;
    }

    public abstract void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, 
            boolean selected, MapTransform mt, Geometry the_geom, RenderContext perm)
            throws ParameterException, IOException, DriverException;

    public abstract JAXBElement<? extends SymbolizerType> getJAXBElement();
}
