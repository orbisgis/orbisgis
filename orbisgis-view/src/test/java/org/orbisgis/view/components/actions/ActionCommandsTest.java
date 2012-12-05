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
package org.orbisgis.view.components.actions;

import org.junit.Test;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import java.awt.Container;
import java.awt.event.ActionEvent;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Unit test of Actions to Controls tool.
 * @author Nicolas Fortin
 */
public class ActionCommandsTest {
        @Test
        public void testAfterActions() throws Exception {
                JPopupMenu menu = new JPopupMenu();
                ActionCommands ac = new ActionCommands();
                //Register actions
                ac.addAction(new UnitTestActionGroup("A"));
                ac.addAction(new UnitTestAction("AA").parent("A"));
                ac.addAction(new UnitTestAction("D"));
                ac.addAction(new UnitTestAction("B").after("A"));
                ac.addAction(new UnitTestAction("C").after("B"));
                ac.addAction(new UnitTestAction("AB").parent("A"));
                ac.addAction(new UnitTestActionGroup("AC").parent("A"));

                ac.registerContainer(menu);
                // Check action order
                assertEquals("A",getActionMenuId(menu,0));
                assertEquals("B",getActionMenuId(menu,1));
                assertEquals("C",getActionMenuId(menu,2));
                assertEquals("D",getActionMenuId(menu,3));
                // A should be a Menu
                assertTrue(menu.getComponents()[0] instanceof JMenu);
                // Menu A should contain 2 MenuItem
                assertEquals(3,((JMenu)menu.getComponents()[0]).getMenuComponentCount());
                assertEquals("AA",getActionMenuId((Container)menu.getComponents()[0],0));
                assertEquals("AB",getActionMenuId((Container)menu.getComponents()[0],1));
                assertEquals("AC",getActionMenuId((Container)menu.getComponents()[0],2));
        }

        @Test
        public void testBeforeActions() throws Exception {
                JPopupMenu menu = new JPopupMenu();
                ActionCommands ac = new ActionCommands();
                //Register actions
                ac.addAction(new UnitTestAction("B"));
                ac.addAction(new UnitTestAction("D"));
                ac.addAction(new UnitTestAction("A").before("B"));
                ac.addAction(new UnitTestAction("C").before("D"));

                ac.registerContainer(menu);
                // Check action order
                assertEquals("A",getActionMenuId(menu,0));
                assertEquals("B",getActionMenuId(menu,1));
                assertEquals("C",getActionMenuId(menu,2));
                assertEquals("D",getActionMenuId(menu,3));
        }

        @Test
        public void testRemoveActions() throws Exception {
                JPopupMenu menu = new JPopupMenu();
                ActionCommands ac = new ActionCommands();
                ac.registerContainer(menu);

                //Register actions
                UnitTestAction A = new UnitTestAction("A");
                ac.addAction(A);
                ac.addAction(new UnitTestAction("B"));

                //Check
                assertEquals("A",getActionMenuId(menu,0));
                assertEquals("B",getActionMenuId(menu,1));
                assertEquals(2,menu.getComponentCount());

                //Remove A
                ac.removeAction(A);

                //Check
                assertEquals("B",getActionMenuId(menu,0));
                assertEquals(1,menu.getComponentCount());
        }
        private String getActionMenuId(Container comp,int actionIndex) {
                return ActionTools.getMenuId(getAction(comp,actionIndex));
        }
        private Action getAction(Container comp,int actionIndex) {
                if(comp instanceof JMenu) {
                        return ((AbstractButton)((JMenu)comp).getMenuComponent(actionIndex)).getAction();
                } else {
                        return ((AbstractButton)comp.getComponent(actionIndex)).getAction();
                }
        }
        private class UnitTestAction extends AbstractAction {
                public UnitTestAction(String menuID) {
                        putValue(ActionTools.MENU_ID,menuID);
                }
                public UnitTestAction after(String otherMenuID) {
                        putValue(ActionTools.INSERT_AFTER_MENUID,otherMenuID);
                        return this;
                }
                public UnitTestAction before(String otherMenuID) {
                        putValue(ActionTools.INSERT_BEFORE_MENUID, otherMenuID);
                        return this;
                }
                public UnitTestAction parent(String parentMenuID) {
                        putValue(ActionTools.PARENT_ID, parentMenuID);
                        return this;
                }
                @Override
                public void actionPerformed(ActionEvent actionEvent) {

                }
        }
        private class UnitTestActionGroup extends UnitTestAction {
                public UnitTestActionGroup(String menuID) {
                        super(menuID);
                        putValue(ActionTools.MENU_GROUP,true);
                }
        }
}
