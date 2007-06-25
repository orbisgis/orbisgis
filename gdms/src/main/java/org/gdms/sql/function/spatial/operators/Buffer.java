package org.gdms.sql.function.spatial.operators;

import org.gdms.data.values.NumericValue;
import org.gdms.data.values.StringValue;
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
