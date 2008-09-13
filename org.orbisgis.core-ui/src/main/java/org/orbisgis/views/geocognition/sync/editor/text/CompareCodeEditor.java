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
		return getContent(leftElement);
	}

	@Override
	protected String getRightContent() throws GeocognitionException {
		return getContent(rightElement);
	}

	@Override
	protected void setLeftContent(String content) throws GeocognitionException {
		setContent(leftElement, content);
	}

	@Override
	protected void setRightContent(String content) throws GeocognitionException {
		setContent(rightElement, content);
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

	/**
	 * Gets the content of the element. The element decorated by the given
	 * decorator must be an instance of LeafElement
	 * 
	 * @param dec
	 *            the element to obtain content
	 * @return the content of the element
	 */
	private String getContent(GeocognitionElementDecorator dec) {
		LeafElement element = (LeafElement) dec.getDecoratedElement();
		Code code = (Code) element.getObject();
		return code.getCode();
	}

	/**
	 * Sets the content of the element. The element decorated by the given
	 * decorator must be an instance of LeafElement
	 * 
	 * @param dec
	 *            the element to set content
	 * @param content
	 *            the content to set
	 */
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
