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


package org.orbisgis.core.renderer.se.fill;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;

import java.io.IOException;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;

import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.DotMapFillType;

import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.GraphicNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.graphic.GraphicCollection;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

public final class DotMapFill extends Fill implements GraphicNode {

	DotMapFill(JAXBElement<DotMapFillType> f) throws InvalidStyle {
		DotMapFillType dmf = f.getValue();

		if (dmf.getGraphic() != null) {
			this.setGraphicCollection(new GraphicCollection(dmf.getGraphic(), this));
		}

		if (dmf.getValuePerMark() != null) {
			this.setQuantityPerMark(SeParameterFactory.createRealParameter(dmf.getValuePerMark()));
		}

		if (dmf.getValueToRepresent() != null) {
			this.setTotalQuantity(SeParameterFactory.createRealParameter(dmf.getValueToRepresent()));
		}
	}

	@Override
	public void setGraphicCollection(GraphicCollection mark) {
		this.mark = mark;
		mark.setParent(this);
	}

	@Override
	public GraphicCollection getGraphicCollection() {
		return mark;
	}

	public void setQuantityPerMark(RealParameter quantityPerMark) {
		this.quantityPerMark = quantityPerMark;
		if (this.quantityPerMark != null) {
			this.quantityPerMark.setContext(RealParameterContext.realContext);
		}
	}

	public RealParameter getQantityPerMark() {
		return quantityPerMark;
	}

	public void setTotalQuantity(RealParameter totalQuantity) {
		this.totalQuantity = totalQuantity;
		if (this.totalQuantity != null) {
			this.totalQuantity.setContext(RealParameterContext.realContext);
		}
	}

	public RealParameter getTotalQantity() {
		return totalQuantity;
	}

	@Override
	public Paint getPaint(long fid, SpatialDataSourceDecorator sds,
            boolean selected, MapTransform mt) throws ParameterException {
		return null;
	}

	@Override
	public void draw(Graphics2D g2, SpatialDataSourceDecorator sds,
            long fid, Shape shp, boolean selected, MapTransform mt)
        throws ParameterException, IOException {
        throw new ParameterException("DotMap not implemented yet!");

	}

	@Override
	public String dependsOnFeature() {
        String m = "";
        String q = "";
        String t = "";

		if (mark != null){
            m = mark.dependsOnFeature();
		}
		if (this.quantityPerMark != null){
            q = quantityPerMark.dependsOnFeature();
		}
		if (this.totalQuantity != null) {
            t = totalQuantity.dependsOnFeature();
		}

        return (m + " " + q + " " + t).trim();
	}

	@Override
	public DotMapFillType getJAXBType() {
		DotMapFillType f = new DotMapFillType();

		if (mark != null) {
			f.setGraphic(mark.getJAXBElement());
		}

		if (quantityPerMark != null) {
			f.setValuePerMark(quantityPerMark.getJAXBParameterValueType());
		}

		if (totalQuantity != null) {
			f.setValuePerMark(totalQuantity.getJAXBParameterValueType());
		}

		return f;
	}

	@Override
	public JAXBElement<DotMapFillType> getJAXBElement() {
		ObjectFactory of = new ObjectFactory();
		return of.createDotMapFill(this.getJAXBType());
	}
	private GraphicCollection mark;
	private RealParameter quantityPerMark;
	private RealParameter totalQuantity;
}
