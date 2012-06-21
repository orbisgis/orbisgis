/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
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
package org.orbisgis.core.ui.plugins.views.sqlConsole.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import org.orbisgis.core.Services;
import org.orbisgis.core.background.BackgroundManager;
import org.orbisgis.core.ui.plugins.views.sqlConsole.blockComment.QuoteSQL;
import org.orbisgis.core.ui.plugins.views.sqlConsole.codereformat.CodeReformator;
import org.orbisgis.core.ui.plugins.views.sqlConsole.ui.SQLConsolePanel;

public class SQLConsoleKeyListener extends KeyAdapter {

        private SQLConsolePanel panel;
        private CodeReformator codeReformator;
        private ActionsListener actionsListener;

        public SQLConsoleKeyListener(SQLConsolePanel panel,
                CodeReformator codeReformator, ActionsListener listener) {
                this.panel = panel;
                this.codeReformator = codeReformator;
                this.actionsListener = listener;
        }

        @Override
        public void keyPressed(KeyEvent e) {
                String originalText = panel.getText();
                if ((e.getKeyCode() == KeyEvent.VK_ENTER) && e.isControlDown()) {
                        BackgroundManager bm = Services.getService(BackgroundManager.class);
                        bm.backgroundOperation(new ExecuteScriptProcess(originalText, panel));

                } else if ((e.getKeyCode() == KeyEvent.VK_S) && e.isControlDown()) {
                        ActionEvent ev = new ActionEvent(this, ConsoleAction.SAVE, String.valueOf(ConsoleAction.SAVE));
                        actionsListener.actionPerformed(ev);
                } // Format SQL code
                else if ((e.getKeyCode() == KeyEvent.VK_F) && e.isShiftDown() && e.isControlDown()) {
                        panel.replaceCurrentSQLStatement(
                                codeReformator.reformat(panel.getCurrentSQLStatement()));

                } // Quote SQL
                else if ((e.getKeyCode() == KeyEvent.VK_SLASH) && e.isShiftDown()) {
                        QuoteSQL.quoteSQL(panel, false);

                } // Unquote SQL
                else if ((e.getKeyCode() == KeyEvent.VK_BACK_SLASH)
                        && e.isShiftDown()) {
                        QuoteSQL.unquoteSQL(panel);

                } else if ((e.getKeyCode() == KeyEvent.VK_O) && e.isControlDown()) {
                        ActionEvent ev = new ActionEvent(this, ConsoleAction.OPEN, String.valueOf(ConsoleAction.OPEN));
                        actionsListener.actionPerformed(ev);

                } else if ((e.getKeyCode() == KeyEvent.VK_F) && e.isControlDown()) {
                        if (originalText.trim().length() > 0) {
                                panel.openFindReplaceDialog();
                        }
                }
                else if ((e.getKeyCode() == KeyEvent.VK_H) && e.isControlDown()) {
                        if (originalText.trim().length() > 0) {
                                panel.openFindReplaceDialog();
                        }
                }
        }
}
