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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.gdms.data.SpatialDataSourceDecorator;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.persistance.se.FillType;
import org.orbisgis.core.renderer.persistance.se.HatchedFillType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.stroke.Stroke;

/**
 *
 * @author maxence
 */
public class HatchedFill extends Fill {

    private RealParameter angle;
    private RealParameter distance;
    private RealParameter offset;
    private Stroke stroke;

    @Override
    public boolean dependsOnFeature() {
        return (angle != null && angle.dependsOnFeature())
                || (offset != null && offset.dependsOnFeature())
                || (distance != null && distance.dependsOnFeature())
                || (stroke != null && stroke.dependsOnFeature());
    }

    @Override
    public void draw(Graphics2D g2, SpatialDataSourceDecorator sds, long fid, Shape shp, boolean selected, MapTransform mt) throws ParameterException, IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Paint getPaint(long fid, SpatialDataSourceDecorator sds, boolean selected, MapTransform mt) throws ParameterException {
    
        Paint painter = null;

        double theta = -45.0;

        if (this.angle != null){
            theta = angle.getValue(sds, fid);
        }

        // convert to rad
        theta *= Math.PI/180.0;

        double pDist = 10;

        if (distance != null){
            pDist = distance.getValue(sds, fid);
        }

        double minL;
        try {
            minL = stroke.getMinLength(sds, fid, mt);
        } catch (IOException ex) {
            Logger.getLogger(HatchedFill.class.getName()).log(Level.SEVERE, null, ex);
            minL = pDist;
        }

        if (minL < pDist){
            minL = pDist;
        }

        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);

        if (Math.abs(sinTheta) < 0.0001){
            // == vertical

        }
        else if(Math.abs(sinTheta) < 0.0001) {
            // == horizontal

        } else{
            // == oblique
            double dy = sinTheta*minL;
            double dist = dy*cosTheta;

            int ratio = (int) Math.ceil(dist / pDist);

            double finalDist = ratio*dist;

            double dx = finalDist/sinTheta;
            dy = Math.tan(theta)*dx;
        }

        return painter;
    }

    @Override
    public JAXBElement<? extends FillType> getJAXBElement() {
        ObjectFactory of = new ObjectFactory();
        return of.createHatchedFill(this.getJAXBType());
    }

    @Override
    public HatchedFillType getJAXBType() {
        ObjectFactory of = new ObjectFactory();
        HatchedFillType hf = of.createHatchedFillType();

        if (angle != null) {
            hf.setAngle(angle.getJAXBParameterValueType());
        }

        if (distance != null) {
            hf.setDistance(distance.getJAXBParameterValueType());
        }

        if (offset != null) {
            hf.setOffset(offset.getJAXBParameterValueType());
        }

        if (stroke != null) {
            hf.setStroke(stroke.getJAXBElement());
        }

        return hf;
    }
}
