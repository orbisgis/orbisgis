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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

/**
 * List content of Bundles, items are only instance of {@link BundleItem}.
 * @author Nicolas Fortin
 */
public class BundleListModel extends AbstractListModel {
    // Bundles read from local repository and remote repositories
    private List<BundleItem> storedBundles = new ArrayList<BundleItem>();
    private BundleContext bundleContext;
    private BundleListener bundleListener = new BundleModelListener();

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
        update();
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

        Map<String,BundleItem> curBundles = new HashMap<String,BundleItem>(storedBundles.size());
        for(BundleItem bundle : storedBundles) {
            curBundles.put(bundle.getSymbolicName(),bundle);
        }
        // Start with local bundles
        Bundle[] bundles = bundleContext.getBundles();
        Set<String> currentBundles = new HashSet<String>(bundles.length);
        // Search new or updated bundles
        for(Bundle bundle : bundles) {
            currentBundles.add(bundle.getSymbolicName());
            BundleItem storedBundle = curBundles.get(getIdentifier(bundle));
            if(storedBundle!=null) {
                // Same bundle found in the shown list
                if(!bundle.equals(storedBundle.getBundle())) {
                    //TODO check same symbolic name but != version
                    storedBundle.setBundle(bundle);
                    int index = storedBundles.indexOf(storedBundle);
                    fireContentsChanged(this,index,index);
                }
            } else {
                BundleItem newBundle = new BundleItem();
                newBundle.setBundle(bundle);
                curBundles.put(getIdentifier(bundle),newBundle);
                int index = storedBundles.size();
                storedBundles.add(newBundle);
                fireIntervalAdded(this, index, index);
            }
        }
        // Search deleted bundles
        for(BundleItem item : new ArrayList<BundleItem>(storedBundles)) {
            if(!currentBundles.contains(getIdentifier(item))) {
                item.setBundle(null);
                if(item.getObrResource()==null) {
                    // Deleted item
                    int index = storedBundles.indexOf(item);
                    storedBundles.remove(index);
                    // find valid index
                    if(index>getSize()) {
                        index = getSize() - 1;
                    }
                    fireIntervalRemoved(this, index,index);
                }
            }
        }


    }

    private String getIdentifier(Bundle bundle) {
        return bundle.getSymbolicName();
    }
    private String getIdentifier(BundleItem bundle) {
        return bundle.getSymbolicName();
    }
    public int getSize() {
        return storedBundles.size();
    }

    public Object getElementAt(int i) {
        if(i >= 0 && i < getSize()) {
            return storedBundles.get(i);
        } else {
            return null;
        }
    }

    /**
     * @param i Index
     * @return List element
     */
    public BundleItem getBundle(int i) {
        return storedBundles.get(i);
    }

    private class BundleModelListener implements BundleListener {

        public void bundleChanged(final BundleEvent event) {
            SwingUtilities.invokeLater(new ProcessBundleEvent(event));
        }
    }

    private class ProcessBundleEvent implements Runnable {
        final BundleEvent evt;

        private ProcessBundleEvent(BundleEvent evt) {
            this.evt = evt;
        }

        public void run() {
            // Find minor modification (like state)
            if(evt.getType()!=BundleEvent.INSTALLED &&
                    evt.getType()!=BundleEvent.UNINSTALLED) {
                Bundle evtSource = evt.getBundle();
                for(int i=0;i< storedBundles.size();i++) {
                    if(evtSource.equals(storedBundles.get(i).getBundle())) {
                        fireContentsChanged(this,i,i);
                        break;
                    }
                }
            }
            // For major changes
            update();
        }
    }
}
