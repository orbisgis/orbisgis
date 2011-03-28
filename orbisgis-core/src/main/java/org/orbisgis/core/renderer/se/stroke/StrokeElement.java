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

import net.opengis.se._2_0.core.StrokeElementType;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.StrokeNode;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealParameterContext;

/**
 *
 * @author maxence
 */
public final class StrokeElement extends CompoundStrokeElement implements StrokeNode {

	private RealParameter length;
	private RealParameter preGap;
	private RealParameter postGap;
	private Stroke stroke;


	public StrokeElement(StrokeElementType set) throws InvalidStyle {
		if (set.getPreGap() != null) {
			setPreGap(SeParameterFactory.createRealParameter(set.getPreGap()));
		}

		if (set.getPreGap() != null) {
			setPostGap(SeParameterFactory.createRealParameter(set.getPostGap()));
		}

		if (set.getLength() != null) {
			setLength(SeParameterFactory.createRealParameter(set.getLength()));
		}

		if (set.getStroke() != null) {
			Stroke s = Stroke.createFromJAXBElement(set.getStroke());
			if (!(s instanceof CompoundStroke)){
				setStroke(Stroke.createFromJAXBElement(set.getStroke()));
			} else {
				throw new InvalidStyle("Not allowed to nest compound stroke within compound stroke");
			}

		}
	}

	public void setPreGap(RealParameter preGap) {
		this.preGap = preGap;

		if (preGap != null) {
			this.preGap.setContext(RealParameterContext.nonNegativeContext);
		}
	}

	public void setPostGap(RealParameter postGap) {
		this.postGap = postGap;

		if (postGap != null) {
			this.postGap.setContext(RealParameterContext.nonNegativeContext);
		}
	}

	public RealParameter getPreGap() {
		return preGap;
	}

	public RealParameter getPostGap() {
		return postGap;
	}

	public RealParameter getLength() {
		return length;
	}

	public void setLength(RealParameter length) {
		this.length = length;
		if (length != null) {
			length.setContext(RealParameterContext.nonNegativeContext);
		}
	}

	@Override
	public void setStroke(Stroke stroke) {
		this.stroke = stroke;
		if (stroke != null) {
			stroke.setParent(this);
		}
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public Object getJaxbType() {
		StrokeElementType set = new StrokeElementType();

		if (this.getLength() != null){
			set.setLength(length.getJAXBParameterValueType());
		}

		if (this.getPreGap() != null){
			set.setPreGap(preGap.getJAXBParameterValueType());
		}

		if (this.getPostGap() != null){
			set.setPostGap(postGap.getJAXBParameterValueType());
		}

		if (this.getStroke() != null){
			set.setStroke(stroke.getJAXBElement());
		}

		return set;
	}

	@Override
	public String dependsOnFeature() {

        String result = "";

        if (length != null)
            result += " " + length.dependsOnFeature();
        if (preGap != null)
            result += " " + preGap.dependsOnFeature();
        if (postGap != null)
            result += " " + postGap.dependsOnFeature();
        if (stroke != null)
            result += " " + stroke.dependsOnFeature();

        return result.trim();
	}
}
