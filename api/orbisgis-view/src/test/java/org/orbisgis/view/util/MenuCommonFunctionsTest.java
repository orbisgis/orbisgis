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
package org.orbisgis.view.util;

import org.junit.Test;
import org.orbisgis.sif.common.MenuCommonFunctions;

import static junit.framework.Assert.assertEquals;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Unit test of MenuCommonFunctions
 */
public class MenuCommonFunctionsTest {
    @Test
    public void testSetMnemonicAction() throws Exception {
        UnitTestAction action = new UnitTestAction("H&ello");
        MenuCommonFunctions.setMnemonic(action);
        assertEquals(KeyEvent.VK_E,action.getMnemonic());
        assertEquals("Hello",action.getValue(Action.NAME));
    }

    @Test
    public void testSetMnemonicButton() throws Exception {
        UnitTestAction action = new UnitTestAction("H&ello");
        JMenuItem menu = new JMenuItem(action);
        assertEquals("H&ello",menu.getText());
        MenuCommonFunctions.setMnemonic(menu);
        assertEquals(KeyEvent.VK_E,menu.getMnemonic());
        assertEquals("Hello",menu.getText());
    }

    @Test
    public void testSetMnemonicButtonSpecial() throws Exception {
        UnitTestAction action = new UnitTestAction("Smith && &Wesson");
        JMenuItem menu = new JMenuItem(action);
        MenuCommonFunctions.setMnemonic(menu);
        assertEquals(KeyEvent.VK_W,menu.getMnemonic());
        assertEquals("Smith & Wesson",menu.getText());
    }
    private class UnitTestAction extends AbstractAction {
        public UnitTestAction(String label) {
            super(label);
        }
        public int getMnemonic() {
            if(getValue(MNEMONIC_KEY)==null) {
                return 0;
            } else {
                return (Integer)getValue(MNEMONIC_KEY);
            }
        }
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
        }
    }
}
