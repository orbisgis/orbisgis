package org.orbisgis.views.geocognition.sync.editor.text;

import org.orbisgis.geocognition.mapContext.GeocognitionException;

public class CompareTextEditor extends AbstractCompareTextEditor {

	@Override
	public boolean accepts(String contentTypeId) {
		return true;
	}

	@Override
	protected String getLeftContent() throws GeocognitionException {
		return leftElement.getXMLContent();
	}

	@Override
	protected String getRightContent() throws GeocognitionException {
		return rightElement.getXMLContent();
	}

	@Override
	protected void setLeftContent(String content) throws GeocognitionException {
		leftElement.setXMLContent(content);
	}

	@Override
	protected void setRightContent(String content) throws GeocognitionException {
		rightElement.setXMLContent(content);
	}
}
