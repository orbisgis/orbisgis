/**
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
package org.orbisgis.view.output;

import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.*;
import org.apache.log4j.Logger;
import org.orbisgis.view.util.MenuCommonFunctions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class OutputPanel extends JPanel {
        //Root logger, for gui logger error
        private static final Logger LOGGER = Logger.getLogger(OutputPanel.class);
        private static final I18n I18N = I18nFactory.getI18n(OutputPanel.class);
        private static final long serialVersionUID = 1L;
        private static final int DEFAULT_MAX_CHARACTERS = 200000;
	    private int maxCharacters = DEFAULT_MAX_CHARACTERS;
        private JTextPane textPane;
        private Color defaultColor=Color.black;
        
        //Current text Attribute for insertion, change whith style update        
        private AttributeSet aset;
        private Color lastColor = defaultColor;
        
        /**
         * Constructor with maxChar parameters
         * @param viewName
         * @param viewLabel
         * @param maxCharacters 
         */
        public OutputPanel(int maxCharacters) {
            this();
            this.maxCharacters = maxCharacters;
        }

        public OutputPanel() {
                changeAttribute(lastColor); //Init attribute
                this.setLayout(new BorderLayout());
                textPane = new JTextPane();
                textPane.setEditable(false);
                textPane.setComponentPopupMenu(makePopupMenu());
                this.add(new JScrollPane(textPane), BorderLayout.CENTER);

                this.addPropertyChangeListener("UI",
                        EventHandler.create(PropertyChangeListener.class, this, "resetFont"));
        }


        public void resetFont() {
            final Font currFont = textPane.getFont();
            textPane.setFont(new Font(Font.MONOSPACED, currFont.getStyle(), currFont.getSize()));
        }

    /**
         * Get the internal text pane
         * @return internal text pane
         */
        public JTextPane getTextPane() {
                return textPane;
        }

        /**
         * Create a popup menu
         * @return A new popup menu
         */
        private JPopupMenu makePopupMenu() {
            //Create the root menu
            JPopupMenu rootMenu = new JPopupMenu();
            //Menu->Copy
            JMenuItem copyItem = new JMenuItem(I18N.tr("&Copy"));
            copyItem.addActionListener(EventHandler.create(ActionListener.class, this, "onMenuCopy"));
            MenuCommonFunctions.setMnemonic(copyItem);
            rootMenu.add(copyItem);
            //Menu->Clear
            JMenuItem clearItem = new JMenuItem(I18N.tr("Clea&r"));
            clearItem.addActionListener(EventHandler.create(ActionListener.class, this, "onMenuClear"));
            MenuCommonFunctions.setMnemonic(clearItem);
            rootMenu.add(clearItem);
            
            return rootMenu;
        }
        /**
         * The user click on copy menu item
         */
        public void onMenuCopy() {
            textPane.copy();
        }
        /**
         * Update the color used by print functions
         * @param defaultColor 
         */
        public void setDefaultColor(Color defaultColor) {
            this.defaultColor = defaultColor;
        }
        
        /**
         * 
         * @return The maximum characters shown in the document
         */
        public int getMaxCharacters() {
            return maxCharacters;
        }
        /**
         * 
         * @param maxCharacters The maximum characters shown in the document
         */
        public void setMaxCharacters(int maxCharacters) {
            this.maxCharacters = maxCharacters;
            try{
                removeAdditionnalCharacters();
            } catch (BadLocationException e) {
                LOGGER.error("orbisgis.view.output.CannotShowLogMessage", e);
            }
        }
        
        /**
         * The user click on clear text button
         */
        public void onMenuClear() {
            textPane.setText(null);
        }

        /**
         * Add the provided text with the default color to the GUI document
         * @param test The text that will be added with an additionnal carriage return
         */
	public void println(String test) {
		print(test + "\n");
	}

        /**
         * Add the provided text with the provided color to the GUI document
         * @param text The text that will be added with an additionnal carriage return
         * @param color The color used to show the text 
         */
	public void println(String text, Color color) {
		print(text + "\n", color);
	}
        /**
         * Add the provided text with the default color to the GUI document
         * @param text The text that will be added without adding a carriage return
         */
	public void print(String text) {
		print(text, defaultColor);
	}
        /**
         * Remove characters that exceed the limitation maxCharacter
         */
        private void removeAdditionnalCharacters() throws BadLocationException {
                if(maxCharacters > 0) {
                    int len = textPane.getDocument().getLength();
                    if (len > maxCharacters) 			{
                            textPane.getDocument().remove(0, len - maxCharacters);
                    }                    
                }
        }
        
        private void changeAttribute(Color color) {
            lastColor = color;
            StyleContext sc = StyleContext.getDefaultStyleContext();
            aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                            StyleConstants.Foreground, color);            
        }
        /**
         * Add the provided text with the provided color to the GUI document
         * @param text The text that will be added without adding a carriage return
         * @param color The color used to show the text 
         */
	public void print(String text, Color color) {
                if(!color.equals(lastColor)) {
                    changeAttribute(color);
                }
		int len = textPane.getDocument().getLength();
		try {
			textPane.setCaretPosition(len);
			textPane.getDocument().insertString(len, text, aset);
                        removeAdditionnalCharacters();
		} catch (BadLocationException e) {
			LOGGER.error(I18N.tr("Cannot show the log message"), e);
		}
		textPane.setCaretPosition(textPane.getDocument().getLength());
	}
}
