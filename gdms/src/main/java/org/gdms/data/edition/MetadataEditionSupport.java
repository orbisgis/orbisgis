package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.metadata.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;

public class MetadataEditionSupport {

	protected DataSource ds;

	private Metadata originalMetadata;

	protected List<Field> fields;

	private MetadataEditionListenerSupport mdels;

	public MetadataEditionSupport(DataSource ids) {
		this.ds = ids;
		mdels = new MetadataEditionListenerSupport(ids);
	}

	public Metadata getDataSourceMetadata() {
		return new ModifiedMetadata();
	}

	protected Metadata getOriginalMetadata() throws DriverException {
		if (null == originalMetadata) {
			originalMetadata = ds.getOriginalMetadata();
		}
		return originalMetadata;
	}

	protected List<Field> getFields() throws DriverException {
		if (null == fields) {
			fields = new ArrayList<Field>();
			final int fc = getOriginalMetadata().getFieldCount();

			for (int i = 0; i < fc; i++) {
				fields.add(new Field(i, getOriginalMetadata().getFieldName(i),
						originalMetadata.getFieldType(i)));
			}
		}
		return fields;
	}

	public void addField(String name, Type type) throws DriverException {
		getFields().add(new Field(-1, name, type));
		mdels.callAddField(getFields().size() - 1);
	}

	public void removeField(int index) throws DriverException {
		getFields().remove(index);
		mdels.callRemoveField(index);
	}

	public void setFieldName(int index, String name) throws DriverException {
		getFields().get(index).setName(name);
		mdels.callModifyField(index);
	}

	public int getFieldCount() throws DriverException {
		return getFields().size();
	}

	public int getOriginalFieldCount() throws DriverException {
		return getOriginalMetadata().getFieldCount();
	}

	public int getFieldIndexByName(final String name) throws DriverException {
		final int fc = getFieldCount();
		for (int i = 0; i < fc; i++) {
			if (getFields().get(i).getName().equalsIgnoreCase(name)) {
				return i;
			}
		}
		return -1;
	}

	public class ModifiedMetadata implements Metadata {

		public int getFieldCount() throws DriverException {
			return getFields().size();
		}

		public Type getFieldType(int fieldId) throws DriverException {
			return getFields().get(fieldId).getType();
		}

		public String getFieldName(int fieldId) throws DriverException {
			return getFields().get(fieldId).getName();
		}

		public String[] getPrimaryKey() throws DriverException {
			return MetadataUtilities.getPKNames(getOriginalMetadata());
		}

		public Boolean isReadOnly(int fieldId) throws DriverException {
			Field f = getFields().get(fieldId);
			int oi = f.getOriginalIndex();
			if (oi != -1) {
				return MetadataUtilities.isReadOnly(getOriginalMetadata(), oi);
			} else {
				return false;
			}
		}
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		mdels.addEditionListener(listener);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		mdels.removeEditionListener(listener);
	}

	/**
	 * Gets where in the edited DataSource each original field is
	 * 
	 * @return
	 * @throws DriverException
	 */
	Integer[] getOriginalFieldIndices() throws DriverException {
		final List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < getFields().size(); i++) {
			int oi = getFields().get(i).getOriginalIndex();
			if (oi != -1) {
				result.add(new Integer(oi));
			}
		}
		return result.toArray(new Integer[0]);
	}

	int getOriginalFieldIndex(final int fieldId) throws DriverException {
		return getFields().get(fieldId).getOriginalIndex();
	}

	public void start() {
		fields = null;
		originalMetadata = null;
	}

	public static String[] getStrings(Iterator<String> i, int tam) {
		final String[] result = new String[tam];
		int index = 0;
		while (i.hasNext()) {
			result[index] = i.next();
			index++;
		}
		return result;
	}

	public Field getField(final int fieldId) throws DriverException {
		return getFields().get(fieldId);
	}

	public int getSpatialFieldIndex() throws DriverException {
		for (int i = 0; i < getFields().size(); i++) {
			if (getFields().get(i).getType().getTypeCode() == Type.GEOMETRY) {
				return i;
			}
		}
		throw new RuntimeException(
				"This method only can be invoked on a SpatialDataSource");
	}
}