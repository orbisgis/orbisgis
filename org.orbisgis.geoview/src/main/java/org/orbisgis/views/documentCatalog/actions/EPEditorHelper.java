package org.orbisgis.views.documentCatalog.actions;

import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.editor.EditorDecorator;
import org.orbisgis.editor.IEditor;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.orbisgis.pluginManager.ItemAttributes;
import org.orbisgis.views.documentCatalog.IDocument;

public class EPEditorHelper {

	public static EditorDecorator getFirstEditor(IDocument document) {
		ExtensionPointManager<IEditor> epm = new ExtensionPointManager<IEditor>(
				"org.orbisgis.Editor");
		ArrayList<ItemAttributes<IEditor>> itemAttributes = epm
				.getItemAttributes("/extension/editor");
		for (ItemAttributes<IEditor> attributes : itemAttributes) {
			IEditor editor = attributes.getInstance("class");
			if (editor.acceptDocument(document)) {
				String iconURL = attributes.getAttribute("icon");
				Icon icon = null;
				if (iconURL != null) {
					icon = new ImageIcon(EPEditorHelper.class
							.getResource(iconURL));
				}
				String id = attributes.getAttribute("id");
				return new EditorDecorator(editor, icon, id);
			}
		}

		return null;
	}

}
