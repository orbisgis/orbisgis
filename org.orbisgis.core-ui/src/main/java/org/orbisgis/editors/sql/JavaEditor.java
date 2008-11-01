package org.orbisgis.editors.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.Transferable;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import org.orbisgis.PersistenceException;
import org.orbisgis.edition.EditableElement;
import org.orbisgis.edition.EditableElementListener;
import org.orbisgis.editor.IEditor;
import org.orbisgis.geocognition.sql.Code;
import org.orbisgis.ui.text.UndoableDocument;
import org.orbisgis.views.javaConsole.CompletionKeyListener;
import org.orbisgis.views.sqlConsole.actions.ConsoleListener;
import org.orbisgis.views.sqlConsole.ui.ConsolePanel;

public abstract class JavaEditor implements IEditor {

	private EditableElement element;
	private Code code;
	private ConsolePanel consolePanel;
	private SaveListener saveListener = new SaveListener();
	private MarkNavigator errorNavigator;

	@Override
	public EditableElement getElement() {
		return element;
	}

	@Override
	public String getTitle() {
		return element.getId();
	}

	@Override
	public void setElement(EditableElement element) {
		this.element = element;
		this.element.addElementListener(saveListener);
		this.code = (Code) element.getObject();
		markErrors();
	}

	@Override
	public void delete() {
		this.element.removeElementListener(saveListener);
	}

	@Override
	public Component getComponent() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		consolePanel = new ConsolePanel(false, new ConsoleListener() {

			public boolean showControlButtons() {
				return false;
			}

			public void save(String text) throws IOException {

			}

			public String open() throws IOException {
				return null;
			}

			public void execute(String text) {
			}

			public void change() {
				if (!consolePanel.getText().equals(
						JavaEditor.this.code.getCode())) {
					JavaEditor.this.code.setCode(consolePanel.getText());
				}
			}

			@Override
			public String doDrop(Transferable t) {
				return null;
			}

		});
		consolePanel.setText(code.getCode());
		JTextComponent textComponent = consolePanel.getTextComponent();
		((UndoableDocument) textComponent.getDocument()).resetUndoEdits();
		textComponent.addKeyListener(new CompletionKeyListener(false,
				textComponent));
		panel.add(consolePanel, BorderLayout.CENTER);
		errorNavigator = new MarkNavigator();
		errorNavigator.setPreferredSize(new Dimension(10, 100));
		markErrors();
		panel.add(errorNavigator, BorderLayout.EAST);

		return panel;
	}

	@Override
	public void initialize() {

	}

	@Override
	public void loadStatus() throws PersistenceException {

	}

	@Override
	public void saveStatus() throws PersistenceException {

	}

	private void markErrors() {
		if (errorNavigator != null) {
			errorNavigator.setTotalLines(code.getLineCount());
			errorNavigator.setErrorLines(code.getErrorLines());
		}
	}

	private class SaveListener implements EditableElementListener {

		@Override
		public void contentChanged(EditableElement element) {
			JTextComponent txt = consolePanel.getTextComponent();
			if (!txt.getText().equals(code.getCode())) {
				int caretPosition = txt.getCaretPosition();
				txt.setText(code.getCode());
				if (caretPosition < code.getCode().length()) {
					txt.setCaretPosition(caretPosition);
				}
			}
		}

		@Override
		public void idChanged(EditableElement element) {
		}

		@Override
		public void saved(EditableElement element) {
			markErrors();
		}

	}

}
