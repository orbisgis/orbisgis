package org.orbisgis.core.renderer.se.parameter.geometry;

import com.vividsolutions.jts.geom.Geometry;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.ogc.FunctionType;
import org.orbisgis.core.renderer.persistance.se.GeometryType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;

/**
 *
 * @author maxence
 * @todo link with SimpleFeature functions (buffer, etc)
 */
public class GeometryFunction implements GeometryParameter {

	private FunctionName name;

	@Override
	public GeometryType getJAXBGeometryType() {
		ObjectFactory of = new ObjectFactory();
		GeometryType gt = of.createGeometryType();

		return gt;
	}

	public enum FunctionName {
		Envelope,
		Boundary,
		LocateAlong,
		LocateBetween,
		Buffer,
		ConvexHull,
		Intersection,
		Union,
		Difference,
		SymDifference
	};


	public GeometryFunction(FunctionType ft){
		String n = ft.getName();

		List<JAXBElement<?>> exs = ft.getExpression();

		if (n.equalsIgnoreCase("envelope")){
			this.name = FunctionName.Envelope;
			// 1 param => GeometryParameter
		}
		else if (n.equalsIgnoreCase("boundary")){
			this.name = FunctionName.Envelope;
			// 1 param => GeometryParameter
		}
		else if (n.equalsIgnoreCase("buffer")){
			this.name = FunctionName.Buffer;
			// 2 param => GeometryParameter & RealParameter
		}
		else if (n.equalsIgnoreCase("convexhull")){
			this.name = FunctionName.ConvexHull;
			// 1 param => GeometryParameter
		}
		else if (n.equalsIgnoreCase("intersection")){
			this.name = FunctionName.Intersection;
			// 2 param => GeometryParameter
		}
		else if (n.equalsIgnoreCase("union")){
			this.name = FunctionName.Union;
			// 2 param => GeometryParameter
		}
		else if (n.equalsIgnoreCase("difference")){
			this.name = FunctionName.Difference;
			// 2 param => GeometryParameter
		}
		else if (n.equalsIgnoreCase("symdifference")){
			this.name = FunctionName.SymDifference;
			// 2 param => GeometryParameter
		}

		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public Geometry getTheGeom(Feature feat) {
		return null;
	}
}
