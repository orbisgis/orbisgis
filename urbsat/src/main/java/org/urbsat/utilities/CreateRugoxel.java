package org.urbsat.utilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SyntaxException;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.DefaultType;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class CreateRugoxel implements Function {
	static int gasp =0;
	public Function cloneFunction() {

		return new CreateRugoxel();
	}

	public Value evaluate(Value[] args) throws FunctionException {
		String ar1 = args[1].toString();
		
	 return ValueFactory.createValue("j");
	}

	public String getName() {

		return "CreateRugoxel";
	}

	public int getType(int[] types) {
		return Type.INT;
	}

	public boolean isAggregate() {

		return true;
	}

}