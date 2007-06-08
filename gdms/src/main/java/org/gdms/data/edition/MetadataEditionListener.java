package org.gdms.data.edition;

public interface MetadataEditionListener {

	public void fieldAdded(FieldEditionEvent event);

	public void fieldRemoved(FieldEditionEvent event);

	public void fieldModified(FieldEditionEvent event);
}
