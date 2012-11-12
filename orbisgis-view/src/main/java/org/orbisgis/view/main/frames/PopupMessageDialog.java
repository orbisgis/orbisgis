/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.main.frames;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.util.LinkedList;
import java.util.Queue;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.varia.DenyAllFilter;
import org.apache.log4j.varia.LevelMatchFilter;
import org.apache.log4j.varia.LevelRangeFilter;
import org.orbisgis.view.output.PanelAppender;

/**
 * This component is connected with the Logging system.
 * When a message is sent, this component appear,
 * and show the message for a while, show other messages,
 * then when the logging stack is empty it diseappear.
 * @author Nicolas Fortin
 */
public class PopupMessageDialog extends JDialog {
        private static final Logger ROOT_LOGGER = Logger.getRootLogger();
        private static final Logger GUI_LOGGER = Logger.getLogger("gui");
        private enum STATE {HIDDEN, APPEAR, VISIBLE, DISAPPEAR};
        // State of the dialog
        private STATE currentState = STATE.HIDDEN;
        private long startingAnimation = 0;
        // Parameters
        private static final int DIALOG_WIDTH = 400;
        // concatenate message while the number of rows is not superior than this constent
        private static final int MAX_DIALOG_ROWS = 8; 
        private static final int MAX_MESSAGE_LENGTH = 300; //Max message length in chars
        private static final int CHECK_LOGGING_INTERVAL = 500; // ms , check when hidden or visible
        private static final int DIALOG_ANIMATION_DURATION = 1000; //ms time to appear and disappear
        //Components
        private JComponent refComponent; //Where to appear
        private Queue<LoggingEvent> eventToDisplay = new LinkedList<LoggingEvent>();
        private JPanel content;
        private JLabel message = new JLabel();
        private Timer checkMessagesTimer = new Timer(CHECK_LOGGING_INTERVAL,EventHandler.create(ActionListener.class, this,"onCheckQueue"));
        private LoggerTarget rootLoggerTarget = new LoggerTarget();
        private LoggerTarget guiLoggerTarget = new LoggerTarget();
        // Do not reset the font if already set
        private Level lastLevel = Level.INFO;
        
        /**
         * 
         * @param refComponent Appear from the TOP of this component
         * @param owner 
         */
        PopupMessageDialog(JComponent refComponent, JFrame owner) {
                super(owner);
                content = new JPanel(new BorderLayout());
                content.setBorder(BorderFactory.createEtchedBorder());
                setContentPane(content);
                content.add(message,BorderLayout.CENTER);
                this.refComponent = refComponent;
                setUndecorated(true);
                setAlwaysOnTop(true);
        }
        public void init() {
                // Configure appenders
                // Root logger, from fatal to warning
                rootLoggerTarget.setLayout(new PatternLayout("%t: %m"));
                LevelRangeFilter filter = new LevelRangeFilter();
                filter.setLevelMax(Level.FATAL);
                filter.setLevelMin(Level.WARN);
                filter.setAcceptOnMatch(true);
                rootLoggerTarget.addFilter(filter);
                rootLoggerTarget.addFilter(new DenyAllFilter());
                ROOT_LOGGER.addAppender(rootLoggerTarget);
                // GUI logger, info only
                guiLoggerTarget.setLayout(new PatternLayout("%t: %m"));
                LevelMatchFilter guiFilter = new LevelMatchFilter();
                guiFilter.setLevelToMatch(Level.INFO.toString());
                guiLoggerTarget.addFilter(guiFilter);
                guiLoggerTarget.addFilter(new DenyAllFilter());
                GUI_LOGGER.addAppender(guiLoggerTarget);      
                // Add mouse listener
                addMouseListener(EventHandler.create(MouseListener.class,this,"onMouseEvt",null,"mouseClicked"));
        }
        /**
         * User click on this dialog, process to the next message
         */
        public void onMouseEvt() {
                startingAnimation = 0;
                if(checkMessagesTimer.isRunning()) {
                        checkMessagesTimer.stop();
                }
                if(eventToDisplay.isEmpty()) {                        
                        setVisible(false);
                        currentState = STATE.HIDDEN;
                } else {
                        SwingUtilities.invokeLater(new Refresh());
                }
        }
        @Override
        public void dispose() {
                super.dispose();
                currentState = STATE.HIDDEN;
                if(checkMessagesTimer.isRunning()) {
                        checkMessagesTimer.stop();
                }
                ROOT_LOGGER.removeAppender(rootLoggerTarget);
                GUI_LOGGER.removeAppender(guiLoggerTarget);
        }
        private void pollAndShowMessage(boolean doResize) {
                if(eventToDisplay.isEmpty()) {
                        return;
                }
                int rowCount = 0;
                Level lastPollLevel = null;
                StringBuilder strMessage = new StringBuilder("<html>");
 
                // Show multiple logg in the same time
                while(!eventToDisplay.isEmpty()
                        && rowCount < MAX_DIALOG_ROWS
                        && ( lastPollLevel==null || lastPollLevel.equals(eventToDisplay.peek().getLevel()))
                        ) {
                        LoggingEvent evt = eventToDisplay.poll();
                        lastPollLevel = evt.getLevel();
                        if(!lastPollLevel.equals(lastLevel)) {
                                lastLevel = lastPollLevel;
                                message.setForeground(PanelAppender.getLevelColor(evt.getLevel()));
                        }
                        if(rowCount!=0) {
                                strMessage.append("<br/>");
                        }
                        String evtMessage = evt.getMessage().toString();
                        if(evtMessage.length() > MAX_MESSAGE_LENGTH) {
                                evtMessage = evtMessage.substring(0, MAX_MESSAGE_LENGTH - 2) + "..";
                        }
                        // Add the number of rows to the counter
                        rowCount += evtMessage.split("\n", -1).length;
                        strMessage.append(evtMessage.replace("\n", "<br/>"));
                        if(evt.getThrowableInformation()!=null) {
                                // Add error information, without stack
                                Throwable tr = evt.getThrowableInformation().getThrowable();
                                strMessage.append("<br/>");
                                strMessage.append(tr.getLocalizedMessage());
                                rowCount ++;
                        }
                }
                
                
                strMessage.append("</html>");
                message.setText(strMessage.toString());                
                // Resize of the dialog
                if(doResize) {                        
                        SwingUtilities.invokeLater(new DoResize());
                }
        }
        
        private Dimension getMessagePreferedSize() {
                Dimension fullSize = message.getPreferredSize();
                fullSize.width = Math.min(refComponent.getWidth(),Math.max(fullSize.width,DIALOG_WIDTH));
                return fullSize;
        }
        /**
         * Set the position and size of the dialog during animation
         * @return True if the animation is done
         */
        private boolean updateAnimation() {
                if(!refComponent.isShowing()) {
                        return false;
                }
                Dimension fullSize =getMessagePreferedSize();
                if(fullSize.height <= 0) {
                        return true;
                }
                long now = System.currentTimeMillis();
                double state = (now-startingAnimation) / (double)DIALOG_ANIMATION_DURATION;
                state = Math.max(Math.min(state,1.),0.);
                if(currentState == STATE.DISAPPEAR) {
                        state = 1 - state;
                }
                int newHeight = (int)(fullSize.height * state);
                Point refLocation = refComponent.getLocationOnScreen();
                int leftPosition = (refLocation.x + (refComponent.getWidth() / 2))
                        -(fullSize.width/2);
                Point newLocation = new Point(leftPosition,refLocation.y - newHeight);
                if(!newLocation.equals(getLocation())) {
                        setLocation(newLocation);
                }
                Dimension newSize = new Dimension(fullSize.width, newHeight);
                if (!getSize().equals(newSize)) {
                        setSize(newSize);
                }
                return now >= (startingAnimation + DIALOG_ANIMATION_DURATION);
        }
        
        public void onCheckQueue() {
                boolean isQueueEmpty = eventToDisplay.isEmpty();
                switch(currentState) {
                        case HIDDEN:
                                if(!isQueueEmpty) {
                                        pollAndShowMessage(false);
                                        startingAnimation = System.currentTimeMillis();
                                        setSize(getMessagePreferedSize().width,1);
                                        setVisible(true);
                                        currentState = STATE.APPEAR;
                                        SwingUtilities.invokeLater(new Refresh());
                                }
                                break;
                        case APPEAR:
                                if(updateAnimation()) {
                                        // Animation is done, now the dialog is fully expanded
                                        currentState = STATE.VISIBLE;
                                        startingAnimation = System.currentTimeMillis();
                                        checkMessagesTimer.setDelay(CHECK_LOGGING_INTERVAL); 
                                        checkMessagesTimer.start();                                       
                                }
                                SwingUtilities.invokeLater(new Refresh());
                                break;
                        case VISIBLE:
                                // Keep showing this message for a while
                                int shownTextLength = (int)Math.min(5,Math.log(message.getText().length()+1)) * 1000;
                                if(startingAnimation + shownTextLength < System.currentTimeMillis()) {
                                        // The message has been sufficiently displayed
                                        // show next message or hide the dialog
                                        startingAnimation = System.currentTimeMillis();
                                        if(!isQueueEmpty) {
                                                pollAndShowMessage(true);
                                                checkMessagesTimer.start();
                                        } else {
                                                currentState = STATE.DISAPPEAR;
                                                SwingUtilities.invokeLater(new Refresh());
                                        }
                                } else {
                                        checkMessagesTimer.start();
                                }
                                break;
                        case DISAPPEAR:
                                if(!isQueueEmpty) {
                                        // A message queued while hiding
                                        pollAndShowMessage(true);
                                        currentState = STATE.APPEAR;
                                        checkMessagesTimer.setDelay(CHECK_LOGGING_INTERVAL); 
                                        checkMessagesTimer.start();
                                } else {
                                        if(updateAnimation()) {
                                                setVisible(false);
                                                // Animation is done, now the dialog is fully hidden
                                                currentState = STATE.HIDDEN;
                                                checkMessagesTimer.setDelay(CHECK_LOGGING_INTERVAL);  
                                                //Stop timer
                                        } else {
                                                //Animation is still running
                                                SwingUtilities.invokeLater(new Refresh());
                                        }
                                }
                                break;
                                
                }
        }
        
        private class LoggerTarget extends AppenderSkeleton {
                private int lastMessageHash = 0;
                private Long lastMessageTime = 0L;

                @Override
                protected void append(LoggingEvent le) {
                        if(le.getMessage()!=null) {
                                int messageHash = le.getMessage().hashCode();
                                if(messageHash!=lastMessageHash ||
                                    le.getTimeStamp()-lastMessageTime>PanelAppender.SAME_MESSAGE_IGNORE_INTERVAL) {
                                    lastMessageHash = messageHash;
                                    lastMessageTime = le.getTimeStamp();
                                    eventToDisplay.add(le);
                                    if(!checkMessagesTimer.isRunning() &&
                                            currentState == STATE.HIDDEN) {
                                            checkMessagesTimer.setRepeats(false);
                                            checkMessagesTimer.start();
                                    }
                                }
                        }
                }

                @Override
                public void close() {
                }

                @Override
                public boolean requiresLayout() {
                        return true;
                }
                
        }
        private class Refresh implements Runnable {
                @Override
                public void run() {
                        onCheckQueue();
                }
                
        }
        // Resize the component
        private class DoResize implements Runnable {
                @Override
                public void run() {
                        if (refComponent.isShowing()) {
                                Dimension fullSize = getMessagePreferedSize();
                                Dimension newSize = new Dimension(fullSize.width, fullSize.height);
                                if (!getSize().equals(newSize)) {
                                        setSize(newSize);
                                }
                                Point refLocation = refComponent.getLocationOnScreen();
                                int leftPosition = (refLocation.x + (refComponent.getWidth() / 2))
                                        - (fullSize.width / 2);
                                Point newLocation = new Point(leftPosition, refLocation.y - fullSize.height);
                                if (!newLocation.equals(getLocation())) {
                                        setLocation(newLocation);
                                }
                        }
                }
        }
}