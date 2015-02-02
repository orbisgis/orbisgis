/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.logpanel;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;
import org.orbisgis.commons.events.EventException;
import org.orbisgis.commons.events.Listener;
import org.orbisgis.commons.events.ListenerContainer;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogService;

/**
 * A LOG4J Appender connected with the LogPanel.
 */
public class PanelAppender implements LogListener {
    public interface ShowMessageListener extends Listener<ShowMessageEventData> {
            
    }
    //New duplicata message is ignored if the time interval is lower than this constant value.
    public static final int SAME_MESSAGE_IGNORE_INTERVAL = 500; //ms
    public static final Color COLOR_ERROR = Color.RED;
    public static final Color COLOR_WARNING = Color.ORANGE.darker();
    public static final Color COLOR_DEBUG = Color.BLUE;
    public static final Color COLOR_INFO = Color.BLACK;
    private int lastLevel = LogService.LOG_INFO;
    private Color lastLevelColor = getLevelColor(lastLevel);
    private OutputPanel guiPanel;
    private int lastMessageHash = 0;
    private Long lastMessageTime = 0L;
    private int levelMinFilter;
    private int levelMaxFilter;
    
    //Messages are stored here before being pushed in the gui
    private Queue<LogEntry> leQueue = new LinkedList<>();
    private AtomicBoolean processingQueue=new AtomicBoolean(false); /*!< If true a swing runnable */
    
    private ListenerContainer<ShowMessageEventData> messageEvent = new ListenerContainer<ShowMessageEventData>();

    public ListenerContainer<ShowMessageEventData> getMessageEvent() {
        return messageEvent;
    }
    
    /**
     * 
     * @return The linked GuiPanel
     */
    public OutputPanel getGuiPanel() {
        return guiPanel;
    }
    
    /**
     * Find the corresponding color depending on error level
     * @param level
     * @return The color associated
     */
    public static Color getLevelColor(int level) {
        switch(level) {
            case LogService.LOG_INFO:
                return COLOR_INFO;
            case LogService.LOG_WARNING:
                return COLOR_WARNING;
            case LogService.LOG_DEBUG:
                return COLOR_DEBUG;
            case LogService.LOG_ERROR:
                return COLOR_ERROR;
            default:
                return COLOR_INFO;
        }
    }

    /**
     * Constructor
     * @param guiPanel Gui panel to write log messages
     * @param levelMinFilter Included Minimum log level
     * @param levelMaxFilter Included Maxumum log level
     */
    public PanelAppender(OutputPanel guiPanel,int levelMinFilter, int levelMaxFilter) {
        this.guiPanel = guiPanel;
        this.levelMinFilter = levelMinFilter;
        this.levelMaxFilter = levelMaxFilter;
    }


    @Override
    public void logged(LogEntry entry) {
        if(entry.getLevel() >= levelMinFilter && entry.getLevel() <= levelMaxFilter) {
            leQueue.add(entry);
            // Show the application when Swing will be ready
            if (!processingQueue.getAndSet(true)) {
                SwingUtilities.invokeLater(new ShowMessage());
            }
        }
    }

    /**
     * Output the message on each listener
     * @param text Message text
     * @param textColor Message color
     */
    private void firePrintMessage(String text,Color textColor) {
        try {
            messageEvent.callListeners(new ShowMessageEventData(text, textColor, this));
        } catch (EventException ex) {
            //Do nothing on listener error
        }
    } 

   /**
    * Push awaiting messages to the gui
    */ 
   private class ShowMessage implements Runnable {
       /**
        * Push awaiting messages to the gui
        */
        @Override
        public void run(){
            try {
                while(!leQueue.isEmpty()) {
                    LogEntry le = leQueue.poll();
                    if(le.getMessage() != null) {
                        int messageHash = le.getMessage().hashCode();
                        if(messageHash!=lastMessageHash ||
                            le.getTime()-lastMessageTime > SAME_MESSAGE_IGNORE_INTERVAL) {
                            lastMessageHash = messageHash;
                            lastMessageTime = le.getTime();
                            //Update the color if the level change
                            if(le.getLevel() != lastLevel) {
                                lastLevel = le.getLevel();
                                lastLevelColor = getLevelColor(lastLevel);
                                guiPanel.setDefaultColor(lastLevelColor);
                            }
                            String message = "\n"+le.getMessage();
                            guiPanel.print(message);
                            firePrintMessage(message,lastLevelColor);
                        }
                    }
                }
            } finally {
                processingQueue.set(false);                
            }
        }
    }
}
