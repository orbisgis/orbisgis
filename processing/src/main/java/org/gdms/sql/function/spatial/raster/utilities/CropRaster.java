package org.gdms.sql.function.spatial.raster.utilities;

import java.awt.geom.Rectangle2D;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.grap.io.GeoreferencingException;
import org.grap.model.GeoRaster;
import org.grap.processing.OperationException;
import org.grap.processing.operation.Crop;

import com.vividsolutions.jts.geom.Envelope;

public class CropRaster implements Function {
	public Value evaluate(Value[] args) throws FunctionException {
		GeoRaster gr = args[0].getAsRaster();
		Envelope g = args[1].getAsGeometry().getEnvelopeInternal();
		
		Envelope intersection = gr.getMetadata().getEnvelope().intersection(g);
		Rectangle2D rect = new Rectangle2D.Double(intersection.getMinX(), intersection.getMinY(), intersection
				.getWidth(), intersection.getHeight());
		try {
			GeoRaster ret = gr.doOperation(new Crop(rect));
			return ValueFactory.createValue(ret);
		} catch (OperationException e) {
			throw new FunctionException("Cannot crop", e);
		} catch (GeoreferencingException e) {
			throw new FunctionException("Cannot crop", e);
		}
	}

	public String getDescription() {
		return "Crops the raster in the first argument with the "
				+ "geometry in the second one. The result is the croped raster";
	}

	public String getName() {
		return "CropRaster";
	}

	public String getSqlOrder() {
		return "select CropRaster(r.raster, f.the_geom) as raster from mytif r, fence f;";
	}

	public Type getType(Type[] argsTypes) throws InvalidTypeException {
		return TypeFactory.createType(Type.RASTER);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		FunctionValidator.failIfBadNumberOfArguments(this, argumentsTypes, 2);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0], Type.RASTER);
		FunctionValidator.failIfNotOfType(this, argumentsTypes[1],
				Type.GEOMETRY);
	}
}