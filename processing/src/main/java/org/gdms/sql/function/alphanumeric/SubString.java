package org.gdms.sql.function.alphanumeric;

import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionValidator;
import org.gdms.sql.strategies.IncompatibleTypesException;

public class SubString implements Function {

	public Value evaluate(Value[] arg0) throws FunctionException {
		// Return a null value is the two arguments are null
		if ((arg0[0].isNull()) || (arg0[1].isNull())) {
			return ValueFactory.createNullValue();
		} else {
			// Get the argument
			final String text = arg0[0].getAsString();
			final int firstRightString = arg0[1].getAsInt();
			String newText = null;
			if (arg0.length == 3) {
				final int secondRightString = arg0[2].getAsInt();
				// The substring with two arguments
				newText = text.substring(firstRightString, secondRightString);

			} else {
				// The substring with one argument
				if (text.length()< firstRightString){
					newText = text;
				}
				else {
				newText = text.substring(firstRightString);
				}
			}
			
			if (newText!=null){
				return ValueFactory.createValue(newText);
			}
			else  {
				return ValueFactory.createNullValue();
			}
			
		}

	}

	public String getDescription() {

		return "Extract a substring. Arguments = right digits ";
	}

	public String getName() {

		return "SubString";
	}

	public String getSqlOrder() {

		return "select substring(text, integer[, integer]) from myTable";
	}

	public Type getType(Type[] arg0) throws InvalidTypeException {
		
		return TypeFactory.createType(Type.STRING);
	}

	public boolean isAggregate() {
		return false;
	}

	public void validateTypes(Type[] argumentsTypes)
			throws IncompatibleTypesException {
		// At leat two arguments must be used and the third is an option.
		FunctionValidator
				.failIfBadNumberOfArguments(this, argumentsTypes, 2, 3);
		// The first argument must be a text
		FunctionValidator.failIfNotOfType(this, argumentsTypes[0], Type.STRING);
		// The second argument must be a numeric
		FunctionValidator.failIfNotNumeric(this, argumentsTypes[1]);
		if (argumentsTypes.length == 3) {
			// The third argument must be a numeric
			FunctionValidator.failIfNotNumeric(this, argumentsTypes[2]);
		}
	}

}
