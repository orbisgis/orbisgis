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
package org.orbisgis.coremap.renderer.se.label;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.ExclusionRectangleType;
import net.opengis.se._2_0.core.ObjectFactory;
import org.orbisgis.coremap.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.parameter.SeParameterFactory;
import org.orbisgis.coremap.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameter;
import org.orbisgis.coremap.renderer.se.parameter.real.RealParameterContext;

/**
 * An {@code ExclusionZone} where the forbidden area is defined as a rectangle. It is 
 * defined thanks to a x and y values. Their meaning is of course dependant of the inner
 * UOM instance.
 * @author Alexis Guéganno, Maxence Laurent
 */
public final class ExclusionRectangle extends ExclusionZone {

    private RealParameter x;
    private RealParameter y;

    /**
     * Build a {@code ExclusionZone} with default width and length set to 3. 
     */
    public ExclusionRectangle(){
        this.setX(new RealLiteral(3));
        this.setY(new RealLiteral(3));
    }

    /**
     * Build a {@code ExclusionZone} from the JAXBElement given in argument. 
     */
    ExclusionRectangle(JAXBElement<ExclusionRectangleType> ert) throws InvalidStyle {
        ExclusionRectangleType e = ert.getValue();

        if (e.getX() != null){
            setX(SeParameterFactory.createRealParameter(e.getX()));
        }

        if (e.getY() != null){
            setY(SeParameterFactory.createRealParameter(e.getY()));
        }

        if (e.getUom() != null){
            setUom(Uom.fromOgcURN(e.getUom()));
        }
    }

    /**
     * Get the x-length of the rectangle.
     * @return 
     * the x-length as a {@code RealParameter} 
     */
    public RealParameter getX() {
        return x;
    }

    /**
     * Set the x-length of the rectangle.
     * @param x 
     */
    public void setX(RealParameter x) {
        this.x = x;
        if (x != null){
            x.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            x.setParent(this);
        }
    }

    /**
     * Get the y-length of the rectangle.
     * @return 
     * the y-length as a {@code RealParameter} 
     */
    public RealParameter getY() {
        return y;
    }

    /**
     * Set the y-length of the rectangle.
     * @param x 
     */
    public void setY(RealParameter y) {
        this.y = y;
        if (this.y != null){
            y.setContext(RealParameterContext.NON_NEGATIVE_CONTEXT);
            y.setParent(this);
        }
    }

    @Override
    public JAXBElement<ExclusionRectangleType> getJAXBElement() {
        ExclusionRectangleType r = new ExclusionRectangleType();

        if (getUom() != null) {
            r.setUom(getUom().toString());
        }

        if (x != null) {
            r.setX(x.getJAXBParameterValueType());
        }

        if (y != null) {
            r.setY(y.getJAXBParameterValueType());
        }

        ObjectFactory of = new ObjectFactory();

        return of.createExclusionRectangle(r);
    }

    @Override
    public List<SymbolizerNode> getChildren() {
        List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
        if (x != null) {
                ls.add(x);
        }
        if (y != null) {
                ls.add(y);
        }
        return ls;
    }

}
