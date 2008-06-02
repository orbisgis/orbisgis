package org.orbisgis.editors.map.actions;

import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.action.IEditorSelectableAction;
import org.orbisgis.editors.map.MapEditor;

public class ShowXY implements IEditorSelectableAction{

	public void actionPerformed(IEditor editor) {
		MapEditor me = (MapEditor) editor;
		me.setShowInfo(!me.getShowInfo());
	}

	public boolean isEnabled(IEditor editor) {
		return true;
	}

	public boolean isVisible(IEditor editor) {
		return true;
	}

	public boolean isSelected(IEditor editor) {
		MapEditor me = (MapEditor) editor;
		return me.getShowInfo();
	}

}
