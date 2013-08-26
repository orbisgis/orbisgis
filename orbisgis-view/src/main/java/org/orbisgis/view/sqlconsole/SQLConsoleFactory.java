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
package org.orbisgis.view.sqlconsole;

import org.fife.rsta.ac.LanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.SingleEditorFactory;
import org.orbisgis.view.sqlconsole.ui.ext.SQLAction;
import org.orbisgis.view.sqlconsole.ui.ext.SQLConsoleEditor;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * Create a single instance of SQLConsole and
 * manage the declaration of the SQLMetadataManager service.
 * @author Nicolas Fortin
 */
public class SQLConsoleFactory implements SingleEditorFactory {

        public static final String factoryId = "SQLConsoleFactory";
        protected final static I18n I18N = I18nFactory.getI18n(SQLConsoleFactory.class);
        private SQLConsole sqlConsole;
        private BundleContext hostBundle;
        private MenuItemServiceTracker<SQLConsoleEditor,SQLAction> actionTracker;
        private ServiceTracker<LanguageSupport,LanguageSupport> st;
        /**
         * Constructor
         * @param hostBundle The SQLConsole buttons can be extended.
         */
        public SQLConsoleFactory(BundleContext hostBundle) {
            this.hostBundle = hostBundle;
        }

        @Override
        public EditorDockable[] getSinglePanels() {
                if(sqlConsole==null) {
                        sqlConsole = new SQLConsole();
                        //Track Action plugin
                        actionTracker = new MenuItemServiceTracker<SQLConsoleEditor, SQLAction>(hostBundle,SQLAction.class,sqlConsole.getActions(),sqlConsole);
                        actionTracker.open(); //begin the track
                        //Track language service
                        if(sqlConsole.getTextArea() instanceof RSyntaxTextArea) {
                            LanguageSupportTracker tracker = new LanguageSupportTracker(hostBundle, (RSyntaxTextArea)sqlConsole.getTextArea());
                            st = new ServiceTracker<LanguageSupport, LanguageSupport>(hostBundle, LanguageSupport.class, tracker);
                            st.open();
                        }
                }
                return new EditorDockable[] {sqlConsole};
        }

        @Override
        public String getId() {
                return factoryId;
        }

        @Override
        public void dispose() {
                if(actionTracker!=null) {
                    actionTracker.close();
                }
                if(sqlConsole!=null) {
                    sqlConsole.dispose();
                }
                if(st!=null) {
                    st.close();
                }
        }
}
