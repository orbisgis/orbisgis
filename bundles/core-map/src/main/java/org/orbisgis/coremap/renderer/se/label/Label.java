/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.label;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.LabelType;
import net.opengis.se._2_0.core.LineLabelType;
import net.opengis.se._2_0.core.ParameterValueType;
import net.opengis.se._2_0.core.PointLabelType;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;

/**
 * Labels are used to provide text-label contents. A textSymbolizer must contain
 * a label - If not it won't be displayed.</p>
 * <p>A Label instance contains a text value (as a StyledText) and informations 
 * about its alignment, vertical or horizontal.
 * @author Maxence Laurent, Alexis Guéganno
 */
public abstract class Label extends AbstractSymbolizerNode implements UomNode {
    private Uom uom;
    private StyledText label;
    private HorizontalAlignment hAlign;
    private VerticalAlignment vAlign;
    
    /**
     * Possible values for the HorizontalAlignment of a Label. It can be left, centered or right aligned.
     */
    public enum HorizontalAlignment {

        LEFT, CENTER, RIGHT;

        /**
         * Creates a <code>HorizontalAlignment</code> from a <code>String</code> value.
         * @param token
         * @return 
         * <ul><li><code>LEFT</code> if token == "left"</li>
         * <li><code>CENTER</code> if token == "center</li>
         * <li><code>RIGHT</code> of token == "right"</li>
         * <li><code>CENTER</code> otherwise (fallback value)</li></ul>
         * Comparisons are made ignoring case.
         */
        public static HorizontalAlignment fromString(String token) {
            if (token.equalsIgnoreCase("left")) {
                return LEFT;
            }

            if (token.equalsIgnoreCase("center")) {
                return CENTER;
            }

            if (token.equalsIgnoreCase("right")) {
                return RIGHT;
            }

            return CENTER; // default value
        }

        /**
         * Retrieve the possible values for <code>HorizontalAlignment</code> in 
         * an array of <code>String</code>
         * @return 
         * An array containing the legal values.
         */
        public static String[] getList() {
            String[] list = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                list[i] = values()[i].name();
            }
            return list;
        }
    }
  
    /**
     * Possible values for the VerticalAlignment of a Label. It can be top, bottom, middle 
     * or baseline aligned.
     */
    public enum VerticalAlignment {

        TOP, MIDDLE, BASELINE, BOTTOM;

        /**
         * Creates a <code>VerticalAlignment</code> from a <code>String</code> value.
         * @param token
         * @return 
         * <ul><li><code>BOTTOM</code> if token == "bottom"</li>
         * <li><code>MIDDLE</code> if token == "middle</li>
         * <li><code>BASELINE</code> of token == "baseline"</li>
         * <li><code>TOP</code> of token == "top"</li>
         * <li><code>TOP</code> otherwise (fallback value)</li></ul>
         * Comparisons are made ignoring case.
         */
        public static VerticalAlignment fromString(String token) {
            if (token.equalsIgnoreCase("bottom")) {
                return BOTTOM;
            }
            if (token.equalsIgnoreCase("middle")) {
                return MIDDLE;
            }
            if (token.equalsIgnoreCase("baseline")) {
                return BASELINE;
            }

            return TOP;
        }

        /**
         * Retrieve the possible values for <code>VerticalAlignment</code> in 
         * an array of <code>String</code>
         * @return 
         * An array containing the legal values.
         */
        public static String[] getList() {
            String[] list = new String[values().length];
            for (int i = 0; i < values().length; i++) {
                list[i] = values()[i].name();
            }
            return list;
        }
    }
    
    /**
     * Creates a <code>Label</code> instance using the given <code>JAXBElement</code>.
     * @param l
     * @return
     * The created <code>Label</code> instance, or null if the declared type of <code>l</code>
     * can't be recognized as a <code>PointLabelType</code> or a  <code>LineLabelType</code>
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public static Label createLabelFromJAXBElement(JAXBElement<? extends LabelType> l) throws InvalidStyle {
        if (l.getDeclaredType() == PointLabelType.class) {
            return new PointLabel((JAXBElement<PointLabelType>) l);
        } else if (l.getDeclaredType() == LineLabelType.class) {
            return new LineLabel((JAXBElement<LineLabelType>) l);
        }

        return null;
    }

    /**
     * Create a new <code>Label</code> with default values as defined in the default
     * {@code StyledText} constructor (cf 
     * {@link org.orbisgis.core.renderer.se.label.Label#Label() Label()} ).
     */
    protected Label() {
        setLabel(new StyledText());
    }

    /**
     * Create a new {@code Label} built from a JAXB object.
     * @param t
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    protected Label(LabelType t) throws InvalidStyle {
        if (t.getUom() != null) {
            this.uom = Uom.fromOgcURN(t.getUom());
        }

        if (t.getStyledText() != null) {
            this.setLabel(new StyledText(t.getStyledText()));
        }


        if (t.getHorizontalAlignment() != null) {
            this.hAlign = HorizontalAlignment.fromString(SeParameterFactory.extractToken(t.getHorizontalAlignment()));
        } else {
            this.hAlign = HorizontalAlignment.CENTER;
        }

        if (t.getVerticalAlignment() != null) {
            this.vAlign = VerticalAlignment.fromString(SeParameterFactory.extractToken(t.getVerticalAlignment()));
        } else {
            this.vAlign = VerticalAlignment.MIDDLE;
        }

    }

    /**
     * Create a new {@code Label} built from a generic JAXB object.
     * @param l
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    protected Label(JAXBElement<? extends LabelType> l) throws InvalidStyle {
        this(l.getValue());
    }

    @Override
    public Uom getOwnUom() {
        return uom;
    }
    
    @Override
    public Uom getUom() {
        if (uom != null) {
            return uom;
        } else if(getParent() instanceof UomNode){
            return ((UomNode)getParent()).getUom();
        } else {
                return Uom.PX;
        }
    }

    @Override
    public void setUom(Uom uom) {
        this.uom = uom;
    }

    /**
     * Get the text that need to be represented by this <code>Label</code>
     * @return 
     * The <code>StyledText</code> instance that contains all the informations needed
     * to represent the text.
     */
    public StyledText getLabel() {
        return label;
    }

    /**
     * Set the text that need to be represented by this <code>Label</code>
     * @param label 
     */
    public final void setLabel(StyledText label) {
        this.label = label;
        label.setParent(this);
    }

    /**
     * Get the current <code>HorizontalAlignment</code>
     * @return 
     * The current <code>HorizontalAlignment</code>
     */
    public HorizontalAlignment getHorizontalAlign() {
        return hAlign;
    }

    /**
     * Set the current <code>HorizontalAlignment</code>
     * @param hAlign 
     */
    public void setHorizontalAlign(HorizontalAlignment hAlign) {
        if (hAlign != null) {
            this.hAlign = hAlign;
        }
    }

    /**
     * Get the current <code>VerticalAlignment</code>
     * @return 
     * The current <code>VerticalAlignment</code>
     */
    public VerticalAlignment getVerticalAlign() {
        return vAlign;
    }

    /**
     * Set the current <code>VerticalAlignment</code>
     * @param vAlign 
     */
    public void setVerticalAlign(VerticalAlignment vAlign) {
        if (vAlign != null) {
            this.vAlign = vAlign;
        }
    }

    /**
     * Fill the {@code LabelType} given in argument with this {@code Label}'s
     * properties.
     * @param lt
     */
    protected void setJAXBProperties(LabelType lt) {
        if (uom != null) {
            lt.setUom(uom.toString());
        }

        if (label != null){
        lt.setStyledText(label.getJAXBType());
        }

        if (hAlign != null) {
            ParameterValueType h = new ParameterValueType();
            h.getContent().add(hAlign.toString());
            lt.setHorizontalAlignment(h);
        }

        if (vAlign != null) {
            ParameterValueType v = new ParameterValueType();
            v.getContent().add(vAlign.toString());
            lt.setVerticalAlignment(v);
        }
    }

    /**
     * Draw this {@code Label} in {@code g2}.
     * @param g2
     * @param feat
     * @param shp
     * @param selected
     * @param mt
     * @throws ParameterException
     * @throws IOException
     */
    public abstract void draw(Graphics2D g2, Map<String, Object> feat,
            Shape shp, boolean selected, MapTransform mt)
            throws ParameterException, IOException;

    /**
     * Get a JAXB representation of this {@code Label}
     * @return
     * A {@code JAXBElement} that contains a {@code LabelType} specialization.
     */
    public abstract JAXBElement<? extends LabelType> getJAXBElement();

}
