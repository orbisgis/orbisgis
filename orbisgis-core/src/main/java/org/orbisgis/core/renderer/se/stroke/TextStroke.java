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
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.TextStrokeType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.label.LineLabel;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * @author maxence
 */
public final class TextStroke extends Stroke {

	private LineLabel lineLabel;

    public TextStroke(){
        setLineLabel(new LineLabel());
    }

	TextStroke(TextStrokeType tst) throws InvalidStyle {
        super(tst);
		if (tst.getLineLabel() != null) {
			setLineLabel(new LineLabel(tst.getLineLabel()));
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
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt, double offset) throws ParameterException, IOException {
        if (this.lineLabel != null){
            lineLabel.draw(g2, sds, fid, shp, selected, mt, null);
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
    public Double getNaturalLength(SpatialDataSourceDecorator sds, long fid, Shape shp, MapTransform mt) throws ParameterException, IOException {
        Rectangle2D bounds = lineLabel.getLabel().getBounds(null, sds, fid, mt);
        return bounds.getWidth();
    }

    @Override
    public Uom getUom() {
        return parent.getUom();
    }
}
