/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.contrib.algorithm.jgrapht;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * This class is a wrapper around a Coordinate object which acts as a Node of 
 * the JGraphT library
 * 0.1 (2006-06-01) : initial release<br>
 * 0.2 (2007-04-20) : use INode static DEFAULT_GEOMETRY_FACTORY<br>
 * 0.3 (2007-05-28) : add a getGeometry method taking a GeometryFactory parameter
 * @author Michael Michaud
 * @version 0.3 (2007-05-28)
 */
public class Node2D implements INode {
    
    Coordinate c;
    
    public Node2D(Coordinate c) {this.c = c;}
    
   /**
    * Return the coordinate of this Node.
    */
    public Coordinate getCoordinate() {return c;}
    
   /**
    * Return a Geometry representing this Node.
    */
    public Geometry getGeometry() {return DEFAULT_GEOMETRY_FACTORY.createPoint(c);}
    
   /**
    * Return a Geometry representing this Node.
    */
    public Geometry getGeometry(GeometryFactory factory) {return factory.createPoint(c);}
    
   /**
    * Return true if obj is a Node2D with the same 2D coordinate as this node.
    */
    public boolean equals(Object obj){
        if (obj instanceof Node2D) {return c.equals(((Node2D)obj).c);}
        else return false;
    }
    
    public int hashCode(){return c.hashCode();}
    
    public String toString() {return "Node2D " + c.toString();}
}

