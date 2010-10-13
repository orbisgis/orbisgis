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


package org.orbisgis.core.renderer.se.graphic;

import java.awt.Dimension;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.se.ViewBoxType;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;

public final class ViewBox implements SymbolizerNode {

	public ViewBox(){
		this.x = null;
		this.y = null;
	}

    public ViewBox(RealParameter width) {
        this.x = width;
    }

    public ViewBox(ViewBoxType viewBox) {
        if (viewBox.getHeight() != null){
            this.setHeight(SeParameterFactory.createRealParameter(viewBox.getHeight()));
        }

        if (viewBox.getWidth() != null){
            this.setWidth(SeParameterFactory.createRealParameter(viewBox.getWidth()));
        }
    }

	public boolean usable() {
		return this.x != null || this.y != null;
	}


    public void setWidth(RealParameter width) {
        x = width;
    }

    public RealParameter getWidth() {
        return x;
    }

    public void setHeight(RealParameter height) {
        y = height;
    }

    public RealParameter getHeight() {
        return y;
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

    public boolean dependsOnFeature() {
        return (x != null && x.dependsOnFeature()) || (y != null && y.dependsOnFeature());
    }

    /**
     * Return the final dimension described by this view box, in [px].
     * @param ds DataSource, i.e. the layer
     * @param fid feature id
     * @param ratio required final ratio (if either width or height isn't defined)
     * @return
     * @throws ParameterException
     */
    public Dimension getDimensionInPixel(Feature feat, double height, double width, Double scale, Double dpi) throws ParameterException {
        double dx, dy;

		double ratio = height / width;

		System.out.println(this);

        if (x != null && y != null) {
            dx = x.getValue(feat);
            dy = y.getValue(feat);
        } else if (x != null) {
            dx = x.getValue(feat);
            dy = dx * ratio;
        } else if (y != null) {
            dy = y.getValue(feat);
            dx = dy / ratio;
        } else { // nothing is defined
            dx = width;
            dy = height;
			return null;
        }

        dx = Uom.toPixel(dx, this.getUom(), dpi, scale, width);
        dy = Uom.toPixel(dy, this.getUom(), dpi, scale, height);

		if (dx <= 0.00021 || dy <= 0.00021){
			throw new ParameterException("View-box is too small");
		}

        return new Dimension((int) dx, (int) dy);
    }

    public ViewBoxType getJAXBType() {
        ViewBoxType v = new ViewBoxType();

        if (x != null) {
            v.setWidth(x.getJAXBParameterValueType());
        }

        if (y != null) {
            v.setHeight(y.getJAXBParameterValueType());
        }

        return v;
    }

	@Override
    public String toString(){
        String result = "ViewBox:";

        if (this.x != null){
            result += "  Width: " + x.toString();
        }

        if (this.y != null){
            result += "  Height: " + y.toString();
        }

        return result;
    }


    private SymbolizerNode parent;
    private RealParameter x;
    private RealParameter y;
}
