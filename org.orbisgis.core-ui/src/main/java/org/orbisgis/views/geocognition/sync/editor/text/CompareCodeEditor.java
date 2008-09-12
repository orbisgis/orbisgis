package org.orbisgis.views.geocognition.sync.editor.text;

import org.orbisgis.Services;
import org.orbisgis.geocognition.LeafElement;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.sql.Code;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.views.geocognition.sync.GeocognitionElementDecorator;

public class CompareCodeEditor extends AbstractCompareTextEditor {

	@Override
	protected String getLeftContent() throws GeocognitionException {
		return getContent(originalLeft);
	}

	@Override
	protected String getRightContent() throws GeocognitionException {
		return getContent(originalRight);
	}

	@Override
	protected void setLeftContent(String content) throws GeocognitionException {
		setContent(originalLeft, content);
	}

	@Override
	protected void setRightContent(String content) throws GeocognitionException {
		setContent(originalRight, content);
	}

	@Override
	public boolean accepts(String contentTypeId) {
		if (contentTypeId == null) {
			return false;
		} else {
			return contentTypeId
					.equals(GeocognitionFunctionFactory.JAVA_FUNCTION_ID)
					|| contentTypeId
							.equals(GeocognitionCustomQueryFactory.JAVA_QUERY_ID);
		}
	}

	private String getContent(GeocognitionElementDecorator dec) {
		LeafElement element = (LeafElement) dec.getDecoratedElement();
		Code code = (Code) element.getObject();
		return code.getCode();
	}

	private void setContent(GeocognitionElementDecorator dec, String content) {
		LeafElement element = (LeafElement) dec.getDecoratedElement();
		Code code = (Code) element.getObject();
		code.setCode(content);
		try {
			element.save();
		} catch (UnsupportedOperationException e) {
			Services.getErrorManager().error("bug!", e);
		} catch (GeocognitionException e) {
			Services.getErrorManager().warning(
					"Some extraordinary conditions occurred while saving", e);
		}
	}
}
