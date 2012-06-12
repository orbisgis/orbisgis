
package org.orbisgis.view.map;

import org.apache.log4j.Logger;
import org.orbisgis.view.edition.EditableElementException;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.SingleEditorFactory;

/**
 * MapEditor cannot be opened twice, the the factory is a SingleEditorFactory
 */
public class MapEditorFactory implements SingleEditorFactory {
        public static final String FACTORY_ID = "MapFactory";
        private static final Logger LOGGER = Logger.getLogger(MapEditorFactory.class);
        private MapEditor mapPanel = null;

        public void dispose() {
                //Save the loaded map
                if(mapPanel!=null && mapPanel.getEditableElement()!=null) {
                        if(mapPanel.getEditableElement().isModified()) {
                                try {
                                        mapPanel.getEditableElement().save();
                                } catch (UnsupportedOperationException ex) {
                                        LOGGER.error(ex);
                                } catch (EditableElementException ex) {
                                        LOGGER.error(ex);
                                }
                        }
                }                
        }

        public EditorDockable[] getSinglePanels() {
                if(mapPanel==null) {
                        mapPanel = new MapEditor();
                }
                return new EditorDockable[] {mapPanel};
        }

        public String getId() {
                return FACTORY_ID;
        }
}
