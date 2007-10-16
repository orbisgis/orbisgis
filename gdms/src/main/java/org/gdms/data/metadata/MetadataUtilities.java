package org.gdms.data.metadata;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.ReadOnlyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

public class MetadataUtilities {
	public static String[] getPKNames(final Metadata metadata)
			throws DriverException {
		final int[] pKIndices = getPKIndices(metadata);
		final String[] pKNames = new String[pKIndices.length];

		for (int i = 0; i < pKNames.length; i++) {
			pKNames[i] = metadata.getFieldName(pKIndices[i]);
		}

		return pKNames;
	}

	public static int[] getPKIndices(final Metadata metadata)
			throws DriverException {
		final int fc = metadata.getFieldCount();
		final List<Integer> tmpPKIndices = new ArrayList<Integer>();

		for (int i = 0; i < fc; i++) {
			final Type type = metadata.getFieldType(i);
			final Constraint[] constraints = type.getConstraints();
			for (Constraint c : constraints) {
				if (ConstraintNames.PK == c.getConstraintName()) {
					tmpPKIndices.add(i);
					break;
				}
			}
		}
		final int[] pkIndices = new int[tmpPKIndices.size()];
		int i = 0;
		for (Integer idx : tmpPKIndices) {
			pkIndices[i++] = idx.intValue();
		}

		return pkIndices;
	}

	public static boolean isReadOnly(final Metadata metadata, final int fieldId)
			throws DriverException {
		final Constraint[] constraints = metadata.getFieldType(fieldId)
				.getConstraints();
		for (Constraint c : constraints) {
			if (c instanceof ReadOnlyConstraint) {
				return true;
			}
		}
		return false;
	}

	public static boolean isPrimaryKey(final Metadata metadata,
			final int fieldId) throws DriverException {
		final Constraint[] constraints = metadata.getFieldType(fieldId)
				.getConstraints();
		for (Constraint c : constraints) {
			if (c instanceof PrimaryKeyConstraint) {
				return true;
			}
		}
		return false;
	}

	public static String check(final Metadata metadata, final int fieldId,
			Value value) throws DriverException {
		final Constraint[] constraints = metadata.getFieldType(fieldId)
				.getConstraints();
		for (Constraint c : constraints) {
			if (null != c.check(value)) {
				return c.check(value);
			}
		}
		return null;
	}

	public static Type[] getFieldTypes(Metadata metadata)
			throws DriverException {
		Type[] fieldTypes = new Type[metadata.getFieldCount()];
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			fieldTypes[i] = metadata.getFieldType(i);
		}
		return fieldTypes;
	}

	public static boolean isWritable(Type fieldType) {
		return (fieldType.getConstraint(ConstraintNames.READONLY) == null)
				&& (fieldType.getConstraint(ConstraintNames.AUTO_INCREMENT) == null);
	}
}