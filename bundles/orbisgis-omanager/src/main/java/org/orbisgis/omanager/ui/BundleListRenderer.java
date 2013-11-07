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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;

/**
 * @author Nicolas Fortin
 */
public class BundleListRenderer implements ListCellRenderer<BundleItem> {
    private static final Logger LOGGER = Logger.getLogger(BundleListRenderer.class);
    private ListCellRenderer<? super BundleItem> lookAndFeelRenderer;
    private static Dimension bundleIconDimension = new Dimension(32,32);
    private static final ImageIcon defaultIcon = new ImageIcon(BundleListRenderer.class.getResource("defaultIcon.png"));
    private static final ImageIcon activeLayer = new ImageIcon(BundleListRenderer.class.getResource("active_layer.png"));
    private static final ImageIcon obrIcon = mergeIcons(defaultIcon.getImage(),new ImageIcon(BundleListRenderer.class.getResource("obr.png")).getImage());
    private static final String ICON_SEPARATOR = ",";
    private static final String ICON_SIZE_PROP = "size=";
    private static final String ICON_HEADER = "Bundle-Icon";

    public BundleListRenderer(JList list) {
        initialize(list);
    }
    private static ImageIcon mergeIcons(Image bottom,Image top) {
        BufferedImage image = new BufferedImage(bundleIconDimension.width, bundleIconDimension.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.drawImage(bottom,0,0,null);
        g.drawImage(top, 0, 0, null);
        return new ImageIcon(image);
    }

    /**
     *
     * @param bundle
     * @param path Uri or Jar relative path.
     * @return
     */
    private ImageIcon pathToImage(Bundle bundle, String path) {
        if(path.isEmpty()) {
            return null;
        }
        URI iconUri = URI.create(path);
        if(!iconUri.isAbsolute()) {
            // In the JAR
            URL entry = bundle.getEntry(path);
            if(entry != null) {
                return new ImageIcon(entry);
            }
        } else {
            //TODO Download ?
        }
        return null;
    }

    /**
     * Find the most appropriate Icon path using the Icon header
     * @param icons
     * @return
     */
    public static String getBundleIconPath(String icons) {
        if(icons!=null) {
            List<String> iconList = new ArrayList<>();
            if(icons.contains(ICON_SEPARATOR)) {
                for(String icon_descr : icons.split(ICON_SEPARATOR)) {
                    iconList.add(icon_descr);
                }
            } else {
                iconList.add(icons);
            }
            String best_path = "";
            int best_width = 0;
            for(String icon_descr : iconList) {
                // Take the best icon size
                int size_pos = icon_descr.indexOf(ICON_SIZE_PROP);
                int size_val = 0;
                String iconPath = new String(icon_descr);
                if(size_pos>1) {
                    size_val = Integer.valueOf(icon_descr.substring(size_pos+ICON_SIZE_PROP.length()
                            ,icon_descr.length()).trim());
                    iconPath = icon_descr.substring(0,size_pos-1).trim();
                }
                if(best_width==0 || size_val == bundleIconDimension.width ||
                        (size_val > best_width && best_width != bundleIconDimension.width)) {
                    best_path = iconPath;
                    best_width = size_val;
                }
            }
            if(!best_path.isEmpty()) {
                return best_path;
            }
        }
        return "";
    }
    private ImageIcon getBundleIcon(Bundle bundle) {
        return pathToImage(bundle,getBundleIconPath(bundle.getHeaders().get(ICON_HEADER)));
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends BundleItem> jList, BundleItem bi, int i, boolean b, boolean b2) {
        Component lafComp = lookAndFeelRenderer.getListCellRendererComponent(jList,bi,i,b,b2);
        if(lafComp instanceof JLabel && bi!=null) {
                try {
                    JLabel label = (JLabel)lafComp;
                    ImageIcon bundleImage = defaultIcon;
                    // Open the bundle icon if defined
                    if(bi.getBundle()!=null) {
                        Bundle bundle = bi.getBundle();
                            try {
                                ImageIcon customBundleImage = getBundleIcon(bundle);
                                if(customBundleImage!=null) {
                                    bundleImage = customBundleImage;
                                }
                            } catch (Exception ex) {
                                // If bundle state is not ready or an error occur when loading
                                // an icon then show the default icon.
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                            }
                    }
                    if(bi.getBundle()!=null && bi.getBundle().getState()== Bundle.ACTIVE) {
                        label.setIcon(mergeIcons(bundleImage.getImage(),activeLayer.getImage()));
                    } else if(bi.getBundle()==null && bi.getObrResource()!=null) {
                        label.setIcon(obrIcon);
                    } else {
                        label.setIcon(bundleImage);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><h4>");
                    sb.append(bi.getPresentationName());
                    sb.append(" (");
                    sb.append(bi.getVersion());
                    sb.append(")</h4>");
                    sb.append(bi.getShortDescription());
                    sb.append("</html>");
                    label.setText(sb.toString());
                } catch(Exception ex) {
                        LOGGER.error(ex.getLocalizedMessage(),ex);
                }
        }
        return lafComp;
    }

    /**
     * Update the native renderer.
     * Warning, Used only by PropertyChangeListener on UI property
     */
    public void updateLFRenderer() {
        lookAndFeelRenderer = new JList<BundleItem>().getCellRenderer();
    }

    private void initialize(JList list) {
        updateLFRenderer();
        list.addPropertyChangeListener("UI",
                EventHandler.create(PropertyChangeListener.class, this, "updateLFRenderer"));
    }
}
