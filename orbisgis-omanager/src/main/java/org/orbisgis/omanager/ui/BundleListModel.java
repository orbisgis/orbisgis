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
package org.orbisgis.omanager.ui;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

/**
 * List content of Bundles
 * @author Nicolas Fortin
 */
public class BundleListModel implements ListModel {
    private List<ListDataListener> listeners = new ArrayList<ListDataListener>();
    // Bundles read from local repository and remote repositories
    private List<BundleItem> storedBundles = new ArrayList<BundleItem>();
    private List<Integer> shownBundles = null; // Filtered (visible) bundles
    private BundleContext bundleContext;
    private BundleListener bundleListener = EventHandler.create(BundleListener.class,this,"update");

    /**
     * @param bundleContext Bundle context to track.
     */
    public BundleListModel(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * Watch for local bundle updates.
     */
    public void install() {
        bundleContext.addBundleListener(bundleListener);
    }

    /**
     * Stop watching for bundles.
     */
    public void uninstall() {
        bundleContext.removeBundleListener(bundleListener);
    }
    /**
     * Update the content of the bundle context
     */
    public void update() {

    }
    public int getSize() {
        if(shownBundles==null) {
            return storedBundles.size();
        } else {
            return shownBundles.size();
        }
    }

    public Object getElementAt(int i) {
        return null;
    }

    public void addListDataListener(ListDataListener listDataListener) {
        listeners.add(listDataListener);
    }

    public void removeListDataListener(ListDataListener listDataListener) {
        listeners.remove(listDataListener);
    }

    /**
     * Listen to plug-in state modification
     */
    private class ListServiceListener implements ServiceListener {
        public void serviceChanged(ServiceEvent event) {
            synchronized ( BundleListModel.this ) {

            }
        }
    }
}
