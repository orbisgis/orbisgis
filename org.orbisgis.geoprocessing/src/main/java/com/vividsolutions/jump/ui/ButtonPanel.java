/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

package com.vividsolutions.jump.ui;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JPanel;


public class ButtonPanel extends JPanel {

    FlowLayout flowLayout1 = new FlowLayout();

    GridLayout gridLayout1 = new GridLayout();

    JPanel innerButtonPanel = new JPanel();

    public JButton getButton(String text) {
        return (JButton)textToButtonMap.get(text);
    }

    private ArrayList actionListeners = new ArrayList();

    // null if none selected
    protected JButton selectedButton;

    private HashMap textToButtonMap = new HashMap();

    /**
     * @param buttonText
     *                   use ampersands to denote mnemonics
     */
    public ButtonPanel(String[] buttonText) {
        innerButtonPanel.setLayout(gridLayout1);
        this.setLayout(flowLayout1);
        gridLayout1.setVgap(5);
        gridLayout1.setHgap(5);
        this.add(innerButtonPanel, null);
        for (int i = 0; i < buttonText.length; i++) {
            innerButtonPanel.add(createButton(buttonText[i]), null);
        }
    }

    private JButton createButton(String buttonText) {
        final JButton button = new JButton(StringUtil.replaceAll(buttonText,
                "&", ""));
        button.setMnemonic(buttonText.indexOf("&") > -1 ? buttonText
                .charAt(buttonText.indexOf("&") + 1) : buttonText.charAt(0));
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                selectedButton = button;
                fireActionPerformed();
            }
        });
        textToButtonMap.put(button.getText(), button);
        return button;
    }

    public void addActionListener(ActionListener l) {
        this.actionListeners.add(l);
    }

    public void removeActionListener(ActionListener l) {
        this.actionListeners.remove(l);
    }

    private void fireActionPerformed() {
        for (Iterator i = actionListeners.iterator(); i.hasNext();) {
            ActionListener l = (ActionListener) i.next();
            l.actionPerformed(new ActionEvent(this, 0, null));
        }
    }

    /**
     *
     * @return null if no button selected
     */
    public JButton getSelectedButton() {
        return selectedButton;
    }
    public void setSelectedButton(JButton selectedButton) {
        this.selectedButton = selectedButton;
    }
}
