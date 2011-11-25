/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer.se;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;

import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.TextSymbolizerType;

import org.gdms.driver.DriverException;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.RenderContext;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

import org.orbisgis.core.renderer.se.common.ShapeHelper;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.Label;
import org.orbisgis.core.renderer.se.label.PointLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 * {@code TextSymbolizer} instances are used to style text labels. In addition to
 * the {@link VectorSymbolizer} parameters, it is computed given these arguments :
 * <ul><li>Perpendicular Offset : Transformation according a line parallel to the
 * original geometry</li>
 * <li>A {@link Label} that gathers all the informations needed to print the 
 * text. This element is compulsory.</li></ul>
 * @author alexis, maxence
 */
public final class TextSymbolizer extends VectorSymbolizer {

    private RealParameter perpendicularOffset;


    private Label label;


    /**
     * Build a new {@code TextSymbolizer} using the informations contained 
     * in the {@code JAXBElement} given in argument.
     * @param st
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle 
     */
    public TextSymbolizer(JAXBElement<TextSymbolizerType> st) throws InvalidStyle {
        super(st);
        TextSymbolizerType tst = st.getValue();

        if (tst.getGeometry() != null) {
            this.setGeometry(new GeometryAttribute(tst.getGeometry()));
        }

        if (tst.getUom() != null) {
            this.uom = Uom.fromOgcURN(tst.getUom());
        }

        if (tst.getPerpendicularOffset() != null) {
            this.setPerpendicularOffset(SeParameterFactory.createRealParameter(tst.getPerpendicularOffset()));
        }

        if (tst.getLabel() != null) {
            this.setLabel(Label.createLabelFromJAXBElement(tst.getLabel()));
        }
    }


    /**
     * Build a new {@code TextSymbolizer}, named {@code Label}. It is defined
     * using a default {@link PointLabel#PointLabel() PointLabel}, and is
     * measured in {@link Uom#MM}.
     */
    public TextSymbolizer() {
        super();
        this.name = "Label";
        setLabel(new PointLabel());
        uom = Uom.MM;
    }


    /**
     * Set the label contained in this {@code TextSymbolizer}.
     * @param label 
     * The new {@code Label} contained in this {@code TextSymbolizer}. Must 
     * be non-{@code null}.
     */
    public void setLabel(Label label) {
        label.setParent(this);
        this.label = label;
    }


    /**
     * Get the label contained in this {@code TextSymbolizer}.
     * @return 
     * The label currently contained in this {@code TextSymbolizer}.
     */
    public Label getLabel() {
        return label;
    }


    /**
     * Get the offset currently associated to this {@code TextSymbolizer}.
     * @return 
     * The current perpendicular offset as a {@code RealParameter}. If null, 
     * the offset is considered to be equal to {@code 0}.
     */
    public RealParameter getPerpendicularOffset() {
        return perpendicularOffset;
    }


    /**
     * Set the perpendicular offset associated to this {@code TextSymbolizer}.
     * @param perpendicularOffset 
     */
    public void setPerpendicularOffset(RealParameter perpendicularOffset) {
        this.perpendicularOffset = perpendicularOffset;
        if (this.perpendicularOffset != null) {
            this.perpendicularOffset.setContext(RealParameterContext.REAL_CONTEXT);
        }
    }


   	@Override
	public void draw(Graphics2D g2, DataSource sds, long fid,
            boolean selected, MapTransform mt, Geometry the_geom, RenderContext perm)
            throws ParameterException, IOException, DriverException {

        List<Shape> shapes = this.getShapes(sds, fid, mt, the_geom);

        if (shapes != null) {
            for (Shape shp : shapes) {
                if (shp != null && label != null) {
                    List<Shape> shps;
                    if (perpendicularOffset != null) {
                        Double pOffset = perpendicularOffset.getValue(sds, fid);
                        shps = ShapeHelper.perpendicularOffset(shp, pOffset);
                    } else {
                        shps = new ArrayList<Shape>();
                        shps.add(shp);
                    }
                    for (Shape s : shps) {
                        label.draw(g2, sds, fid, s, selected, mt, perm);
                    }
                }
            }
        }
    }


    @Override
    public JAXBElement<TextSymbolizerType> getJAXBElement() {

        ObjectFactory of = new ObjectFactory();
        TextSymbolizerType s = of.createTextSymbolizerType();

        this.setJAXBProperty(s);

        if (this.getGeometry() != null) {
            s.setGeometry(getGeometry().getJAXBGeometryType());
        }

        if (this.getUom() != null) {
            s.setUom(this.getUom().toURN());
        }

        if (perpendicularOffset != null) {
            s.setPerpendicularOffset(perpendicularOffset.getJAXBParameterValueType());
        }

        if (label != null) {
            s.setLabel(label.getJAXBElement());
        }

        return of.createTextSymbolizer(s);
    }


}
