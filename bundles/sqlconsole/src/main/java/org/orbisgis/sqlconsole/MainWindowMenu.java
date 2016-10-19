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
package org.orbisgis.sqlconsole;


import org.orbisgis.mainframe.api.MainFrameAction;
import org.orbisgis.mainframe.api.MainWindow;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.edition.EditorManager;
import org.orbisgis.sqlconsole.api.SQLElement;
import org.orbisgis.sqlconsole.icons.SQLConsoleIcon;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nicolas Fortin
 */
@Component
public class MainWindowMenu implements MainFrameAction {
    public static String M_NEW_SQL_WINDOW = "M_NEW_SQL_WINDOW";
    public static final I18n I18N = I18nFactory.getI18n(MainWindowMenu.class);
    private EditorManager editorManager;

    @Override
    public List<Action> createActions(MainWindow target) {
        return Arrays.asList((Action)new DefaultAction(M_NEW_SQL_WINDOW, I18N.tr("New SQL Console"), I18N.tr
                ("Open a new SQL editor panel"), SQLConsoleIcon.getIcon("sql_code"), EventHandler.create
                (ActionListener.class, this, "onMenuOpenSQLConsole"),
                KeyStroke.getKeyStroke("ctrl e")).setParent(MainFrameAction.MENU_TOOLS)
                .setBefore(MainFrameAction.MENU_CONFIGURE));
    }

    @Override
    public void disposeActions(MainWindow target, List<Action> actions) {

    }

    @Reference
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }

    public void unsetEditorManager(EditorManager editorManager) {
        this.editorManager = null;
    }

    public void onMenuOpenSQLConsole() {
        editorManager.openEditable(new SQLElement());
    }
}
