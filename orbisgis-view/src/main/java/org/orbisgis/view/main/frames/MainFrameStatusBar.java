 
package org.orbisgis.view.main.frames;

import org.orbisgis.view.components.statusbar.StatusBar;

/**
 * The status bar of the MainFrame
 * @author fortin
 */
public class MainFrameStatusBar extends StatusBar {
        //Layout parameters
        private final static int OUTER_BAR_BORDER = 1;
        private final static int HORIZONTAL_EMPTY_BORDER = 4;

        public MainFrameStatusBar() {
                super(OUTER_BAR_BORDER, HORIZONTAL_EMPTY_BORDER);
                //Add the JobList
        }
        
}
