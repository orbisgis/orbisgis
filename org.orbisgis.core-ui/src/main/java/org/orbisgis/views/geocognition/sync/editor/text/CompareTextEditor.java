package org.orbisgis.views.geocognition.sync.editor.text;

import org.orbisgis.geocognition.mapContext.GeocognitionException;

public class CompareTextEditor extends AbstractCompareTextEditor {

	@Override
	public boolean accepts(String contentTypeId) {
		return true;
	}

	@Override
	protected String getLeftContent() throws GeocognitionException {
		return originalLeft.getXMLContent();
	}

	@Override
	protected String getRightContent() throws GeocognitionException {
		return originalRight.getXMLContent();
	}

	@Override
	protected void setLeftContent(String content) throws GeocognitionException {
		originalLeft.setXMLContent(content);
	}

	@Override
	protected void setRightContent(String content) throws GeocognitionException {
		originalRight.setXMLContent(content);
	}
}
