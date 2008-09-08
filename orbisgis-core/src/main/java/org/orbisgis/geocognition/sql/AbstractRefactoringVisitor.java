package org.orbisgis.geocognition.sql;

import org.orbisgis.javaManager.autocompletion.AbstractVisitor;

public class AbstractRefactoringVisitor extends AbstractVisitor {

	protected String text;
	protected int start;
	protected int end;
	private String newPart;

	public AbstractRefactoringVisitor(String text, String newPart) {
		this.text = text;
		this.newPart = newPart;
	}

	public String getModifiedText() {
		return text.substring(0, start) + newPart + text.substring(end + 1);
	}

}
