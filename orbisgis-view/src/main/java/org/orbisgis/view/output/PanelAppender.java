 /*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.output;

import java.awt.Color;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A LOG4J Appender connected with the LogPanel
 */
public class PanelAppender extends AppenderSkeleton {
    private static final Color COLOR_ERROR = Color.RED;
    private static final Color COLOR_WARNING = Color.ORANGE;
    private static final Color COLOR_DEBUG = Color.BLUE;
    private static final Color COLOR_INFO = Color.BLACK;
    private Level lastLevel = Level.INFO;
    private Color lastLevelColor = getLevelColor(lastLevel);
    private OutputPanel guiPanel;
    
    /**
     * Find the corresponding color depending on error level
     * @param level
     * @return The color associated
     */
    private static Color getLevelColor(Level level) {
        switch(level.toInt()) {
            case Level.INFO_INT:
                return COLOR_INFO;
            case Level.WARN_INT:
                return COLOR_WARNING;
            case Level.DEBUG_INT:
                return COLOR_DEBUG;
            case Level.ERROR_INT:
                return COLOR_ERROR;
            default:
                return COLOR_INFO;
        }
    }
    public PanelAppender(OutputPanel guiPanel) {
        this.guiPanel = guiPanel;
    }

    /**
     * The logging event has been filtered and formated
     * @param le 
     */
    @Override
    protected void append(LoggingEvent le) {
        //TODO use swing thread
        //Update the color if the level change
        if(!le.getLevel().equals(lastLevel)) {
            lastLevel = le.getLevel();
            lastLevelColor = getLevelColor(lastLevel);
            guiPanel.setDefaultColor(lastLevelColor);
        }
        guiPanel.println(le.getMessage().toString());
    }

    public void close() {
        //Nothing to close
    }

    public boolean requiresLayout() {
        return true;
    }    
}
