package org.orbisgis.view.toc;

import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.SingleEditorFactory;

/**
 * This factory creates only one instance of Toc
 */
public class TocEditorFactory implements SingleEditorFactory {
        public static final String FACTORY_ID = "TocFactory";
        Toc tocPanel = null;

        public void dispose() {
        }

        public EditorDockable[] getSinglePanels() {
                if(tocPanel==null) {
                        tocPanel = new Toc();
                }
                return new EditorDockable[] {tocPanel};
        }

        public String getId() {
                return FACTORY_ID;
        }
}
