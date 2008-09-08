package org.orbisgis.geocognition.symbology;

import org.orbisgis.geocognition.AbstractExtensionElement;
import org.orbisgis.geocognition.GeocognitionElementContentListener;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.renderer.legend.Legend;

public class GeocognitionLegend extends AbstractExtensionElement implements
		GeocognitionExtensionElement {

	private Legend legend;
	private Object revertStatus;

	public GeocognitionLegend(Legend legend, GeocognitionElementFactory factory) {
		super(factory);
		this.legend = legend;
	}

	@Override
	public Object getJAXBObject() {
		return legend.getJAXBObject();
	}

	@Override
	public Object getObject() throws UnsupportedOperationException {
		return legend;
	}

	@Override
	public String getTypeId() {
		return legend.getLegendTypeId();
	}

	@Override
	public void close(IProgressMonitor progressMonitor) {
		legend.setJAXBObject(revertStatus);
	}

	@Override
	public void open(IProgressMonitor progressMonitor)
			throws UnsupportedOperationException, GeocognitionException {
		revertStatus = getJAXBObject();
	}

	@Override
	public void save() {
		revertStatus = getJAXBObject();
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public Object getRevertJAXBObject() {
		return revertStatus;
	}

	@Override
	public void setElementListener(GeocognitionElementContentListener listener) {
	}

	@Override
	public void setJAXBObject(Object jaxbObject)
			throws IllegalArgumentException, GeocognitionException {
		legend.setJAXBObject(jaxbObject);
	}

}
