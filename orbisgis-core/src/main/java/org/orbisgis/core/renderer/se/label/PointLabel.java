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
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.PointLabelType;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * A label located at a single point. In addition to all the {@code Label} characteristics,
 * it has two additional properties : 
 * <ul><li>A rotation angle, in degrees.</li>
 * <li>An exclusion zone, ie a zone around the label where no other text will be displayed.</li>
 * </ul>
 * @author Alexis Guéganno, Maxence Laurent
 */
public final class PointLabel extends Label {

    private RealParameter rotation;
    private ExclusionZone exclusionZone;


    /**
     * Creates a new {@code PointLabel} with default values as detailed in 
     * {@link org.orbisgis.core.renderer.se.label.Label#Label() Label} and
     * {@link org.orbisgis.core.renderer.se.label.StyledText#StyledText() StyledText}.
     * This {@code PointLabel} will be top and right aligned.
     */
    public PointLabel() {
        super();
        rotation = new RealLiteral(0.0);
        setVerticalAlign(VerticalAlignment.TOP);
        setHorizontalAlign(HorizontalAlignment.RIGHT);
    }


    /**
     * Creates a new {@code PointLabel} from a {@code PointLabelType} instance.
     * @param plt
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public PointLabel(PointLabelType plt) throws InvalidStyle {
        super(plt);
        if (plt.getExclusionZone() != null) {
            setExclusionZone(ExclusionZone.createFromJAXBElement(plt.getExclusionZone()));
        }
        if (plt.getRotation() != null) {
            setRotation(SeParameterFactory.createRealParameter(plt.getRotation()));
        }
    }


    /**
     * Creates a new {@code PointLabel} from a {@code JAXBElement} instance.
     * @param plt
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    PointLabel(JAXBElement<PointLabelType> pl) throws InvalidStyle {
        this(pl.getValue());
    }


    /**
     * Get the exclusion zone defined for this {@code PointLabel}. In this zone, 
     * we won't draw any other text.
     * @return 
     * An {@link ExclusionZone} instance.
     */
    public ExclusionZone getExclusionZone() {
        return exclusionZone;
    }


    /**
     * Set the exclusion zone defined for this {@code PointLabel}.
     * @param exclusionZone 
     */
    public void setExclusionZone(ExclusionZone exclusionZone) {
        this.exclusionZone = exclusionZone;
        if (exclusionZone != null) {
            exclusionZone.setParent(this);
        }
    }


    /**
     * Get the rotation that must be applied to this {@code PointLabel} before rendering.
     * @return 
     * The rotation, in degrees, as a {@link RealParameter}
     */
    public RealParameter getRotation() {
        return rotation;
    }


    /**
     * Set the rotation that must be applied to this {@code PointLabel} before rendering.
     * @param rotation 
     */
    public void setRotation(RealParameter rotation) {
        this.rotation = rotation;
        if (this.rotation != null) {
            this.rotation.setContext(RealParameterContext.REAL_CONTEXT);
            this.rotation.setParent(this);
        }
    }


    @Override
    public void draw(Graphics2D g2, Map<String, Value> map,
            Shape shp, boolean selected, MapTransform mt, RenderContext perm)
            throws ParameterException, IOException {
        double x;
        double y;

        // TODO RenderPermission !
        double deltaX = 0;
        double deltaY = 0;

        Rectangle2D bounds = getLabel().getBounds(g2, map, mt);
        x = shp.getBounds2D().getCenterX() + bounds.getWidth() / 2;
        y = shp.getBounds2D().getCenterY() - bounds.getHeight() / 2;

        if (this.exclusionZone != null) {
            if (this.exclusionZone instanceof ExclusionRadius) {
                double radius = ((ExclusionRadius) (this.exclusionZone)).getRadius().getValue(map);
                radius = Uom.toPixel(radius, getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                deltaX = radius;
                deltaY = radius;
            } else {
                deltaX = ((ExclusionRectangle) (this.exclusionZone)).getX().getValue(map);
                deltaY = ((ExclusionRectangle) (this.exclusionZone)).getY().getValue(map);

                deltaX = Uom.toPixel(deltaX, getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
                deltaY = Uom.toPixel(deltaY, getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
            }
        }

        AffineTransform at = AffineTransform.getTranslateInstance(x + deltaX, y + deltaY);

        getLabel().draw(g2, map, selected, mt, at, perm);
    }


    @Override
    public JAXBElement<PointLabelType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createPointLabel(getJAXBType());
    }


    /**
     * Get a JAXB representation of this element.
     * @return 
     */
    public PointLabelType getJAXBType() {
        PointLabelType pl = new PointLabelType();

        setJAXBProperties(pl);

        if (exclusionZone != null) {
            pl.setExclusionZone(exclusionZone.getJAXBElement());
        }

        if (rotation != null) {
            pl.setRotation(rotation.getJAXBParameterValueType());
        }

        return pl;
    }

    @Override
    public HashSet<String> dependsOnFeature() {
        HashSet<String> result = new HashSet<String>();
        if (getLabel() != null) {
            result.addAll(getLabel().dependsOnFeature());
        }
        if (exclusionZone != null) {
            result.addAll(exclusionZone.dependsOnFeature());
        }
        if (rotation != null) {
            result.addAll(rotation.dependsOnFeature());
        }
        return result;
    }

    @Override
    public UsedAnalysis getUsedAnalysis() {
        UsedAnalysis result = new UsedAnalysis();
        if (getLabel() != null) {
            result.merge(getLabel().getUsedAnalysis());
        }
        if (exclusionZone != null) {
            result.merge(exclusionZone.getUsedAnalysis());
        }
        if (rotation != null) {
            result.merge(rotation.getUsedAnalysis());
        }
        return result;
    }

}
