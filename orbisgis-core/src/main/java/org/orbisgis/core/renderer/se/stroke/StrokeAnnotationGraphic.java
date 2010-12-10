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

import org.orbisgis.core.renderer.persistance.se.StrokeAnnotationGraphicType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.RelativeOrientation;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.graphic.Graphic;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 */
final class StrokeAnnotationGraphic implements SymbolizerNode {

	protected SymbolizerNode parent;
	private Graphic graphic;
	private RealParameter relativePosition;
	private RelativeOrientation orientation;

	public StrokeAnnotationGraphic(StrokeAnnotationGraphicType sagt) throws InvalidStyle {
		if (sagt.getGraphic() != null) {
			setGraphic(Graphic.createFromJAXBElement(sagt.getGraphic()));
		}

		if (sagt.getRelativeOrientation() != null) {
			this.setRelativeOrientation(RelativeOrientation.readFromToken(sagt.getRelativeOrientation().value()));
		}

		if (sagt.getRelativePosition() != null) {
			this.setRelativePosition(SeParameterFactory.createRealParameter(sagt.getRelativePosition()));
		}
	}

	public Graphic getGraphic() {
		return graphic;
	}

	public void setGraphic(Graphic graphic) {
		this.graphic = graphic;
		if (graphic != null) {
			graphic.setParent(this);
		}
	}

	public RelativeOrientation getRelativeOrientation() {
		return orientation;
	}

	public void setRelativeOrientation(RelativeOrientation orientation) {
		this.orientation = orientation;
	}

	public RealParameter getRelativePosition() {
		return relativePosition;
	}

	public void setRelativePosition(RealParameter relativePosition) {
		this.relativePosition = relativePosition;

		if (relativePosition != null) {
			relativePosition.setContext(RealParameterContext.percentageContext);
		}
	}

	@Override
	public Uom getUom() {
		return parent.getUom();
	}

	@Override
	public SymbolizerNode getParent() {
		return parent;
	}

	@Override
	public void setParent(SymbolizerNode node) {
		parent = node;
	}

	StrokeAnnotationGraphicType getJaxbType() {
		StrokeAnnotationGraphicType sagt = new StrokeAnnotationGraphicType();

		if (getGraphic() != null) {
			sagt.setGraphic(graphic.getJAXBElement());
		}

		if (getRelativeOrientation() != null) {
			sagt.setRelativeOrientation(orientation.getJaxbType());
		}

		if (getRelativePosition() != null) {
			sagt.setRelativePosition(relativePosition.getJAXBParameterValueType());
		}


		return sagt;
	}

	boolean dependsOnFeature() {
		return (this.graphic != null && this.graphic.dependsOnFeature()) ||
			   (this.relativePosition != null && this.relativePosition.dependsOnFeature());
	}
}
