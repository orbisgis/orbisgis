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
package org.orbisgis.core.renderer.se.stroke;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.io.IOException;
import java.util.ArrayList;
import javax.media.jai.RenderableGraphics;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.TextStrokeType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.LineLabel;
import org.orbisgis.core.renderer.se.label.StyledLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

/**
 *
 * @author maxence
 */
public final class TextStroke extends Stroke {

	private LineLabel lineLabel;

	TextStroke(TextStrokeType tst) throws InvalidStyle {
        super();
		if (tst.getLineLabel() != null) {
			setLineLabel(new LineLabel(tst.getLineLabel()));
		}

		if (tst.getUnitOfMeasure() != null) {
			this.setUom(Uom.fromOgcURN(tst.getUnitOfMeasure()));
		} else {
            this.setUom(null);
		}
	}

	TextStroke(JAXBElement<TextStrokeType> s) throws InvalidStyle {
		this(s.getValue());
	}

	public LineLabel getLineLabel() {
		return lineLabel;
	}

	public void setLineLabel(LineLabel lineLabel) {
		this.lineLabel = lineLabel;

		if (lineLabel != null) {
			lineLabel.setParent(this);
		}
	}

	@Override
	public double getMaxWidth(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
		if (lineLabel != null){
			StyledLabel label = lineLabel.getLabel();
		    if (label != null){
				//RealParameter size = label.getFontSize();
				//if (size != null){
				//	double value = size.getValue(sds, fid);
				//	return Uom.toPixel(value, label.getUom(), mt.getDpi(), mt.getScaleDenominator(), null);
				//} else {
                //    return
                //}
                return label.getEmInPixel(sds, fid, mt)*2.0;
			}
		}
		return 0;
	}

    /*@Override
    public double getMinLength(SpatialDataSourceDecorator sds, long fid, MapTransform mt) throws ParameterException, IOException {
        return lineLabel.getLabel().getImage(sds, fid, true, mt).getWidth();
    }*/

	@Override
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt, double offset) throws ParameterException, IOException {
        if (this.lineLabel != null){
            lineLabel.draw(g2, sds, fid, shp, selected, mt);
        }
	}

	@Override
	public JAXBElement<TextStrokeType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		return of.createTextStroke(this.getJAXBType());
	}

	public TextStrokeType getJAXBType() {
		TextStrokeType s = new TextStrokeType();

		this.setJAXBProperties(s);

		if (lineLabel != null) {
			s.setLineLabel(lineLabel.getJAXBType());
		}

		return s;
	}

	@Override
	public String dependsOnFeature() {
		return lineLabel.dependsOnFeature();
	}

    @Override
    public double getNaturalLength(SpatialDataSourceDecorator sds, long fid, Shape shp, MapTransform mt) throws ParameterException, IOException {
        ArrayList<RenderableGraphics> glyphs = lineLabel.getLabel().getGlyphs(sds, fid, false, mt);

        double em = lineLabel.getLabel().getEmInPixel(sds, fid, mt);

        double length = 0.0;
        for (RenderableGraphics glyph : glyphs ){
            if (glyph != null){
                length += glyph.getWidth();
            }else{
                length += em;
            }
        }

        return length;

        //RenderableGraphics image = lineLabel.getLabel().getImage(sds, fid, false, mt);
        //return image.getWidth();
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
