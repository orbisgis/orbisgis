/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function.spatial.operators;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.operation.buffer.BufferOp;

public class Buffer implements Function {

	
	private static final String  CAP_STYLE_SQUARE = "square";
	
	private static final String  CAP_STYLE_BUTT = "butt";
			
		
	    
	    
	public Function cloneFunction() {
		return new Buffer();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		GeometryValue gv = (GeometryValue) args[0];
		double bufferSize = ((NumericValue) args[1]).doubleValue();
		Geometry buffer = null;
		if (args.length==3){
			String bufferStyle = args[2].toString();
			 buffer = runBuffer(gv.getGeom(), bufferSize, bufferStyle);
			
		}
		
		else {
			 buffer = gv.getGeom().buffer(bufferSize);
			
		}
			
		
		return ValueFactory.createValue(buffer);
	}

	public String getName() {
		return "Buffer";
	}

	public int getType(int[] types) {

		return types[0];
	}

	public boolean isAggregate() {
		return false;
	}

	
	 private Geometry runBuffer(Geometry a, Double distance, String endCapStyle)
	  {
	    Geometry result = null;
	  
	    BufferOp bufOp = new BufferOp(a);
	    
	    if (endCapStyle.equalsIgnoreCase(CAP_STYLE_SQUARE)){
	    	 bufOp.setEndCapStyle(BufferOp.CAP_SQUARE);
	    }	
	    
	    else if (endCapStyle.equalsIgnoreCase(CAP_STYLE_BUTT)){
	    	 bufOp.setEndCapStyle(BufferOp.CAP_BUTT);
	    }	
	    
	    else {
	    	 bufOp.setEndCapStyle(BufferOp.CAP_ROUND);
	    }
	    
	     result = bufOp.getResultGeometry(distance);	     
	   
	    return result;
	  }
	 
	 private static int endCapStyleCode(String capStyle)
	  {
	    if (capStyle == CAP_STYLE_BUTT) return BufferOp.CAP_BUTT;
	    if (capStyle == CAP_STYLE_SQUARE) return BufferOp.CAP_SQUARE;
	    return BufferOp.CAP_ROUND;
	  }
	 
	 
}
