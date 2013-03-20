/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.Toc;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Display a UI with the name of all supported legends and their coresponding
 * representation.
 *
 * @author ebocher
 */
public class LegendUIChooser implements UIPanel {

    private static final I18n I18N = I18nFactory.getI18n(Toc.class);
    private static final Logger LOGGER = Logger.getLogger(LegendUIChooser.class);
    private final String[] names;
    private final ILegendPanel[] ids;
    private JPanel mainPanel;
    private JList lst;
    private JLabel imageLabel;

    /**
     * Set the names for supported legends and their UIPanels
     *
     * @param names
     * @param ids
     */
    public LegendUIChooser(String[] names, ILegendPanel[] ids) {
        this.names = names;
        this.ids = ids;
        init();
    }

    /**
     * Create the UI
     */
    private void init() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        lst = new JList();
        DefaultListModel model = new DefaultListModel();
        for (int i = 0; i < names.length; i++) {
            model.addElement(names[i]);
        }
        lst.setModel(model);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst.addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent lse) {
                            onThumbnailChange(lse);
                    }
                });

        lst.setSelectedIndex(0);
        mainPanel.add(new JScrollPane(lst), BorderLayout.WEST);
        mainPanel.add(imageLabel, BorderLayout.EAST);
    }
    
    /**
     * Change the thumbnail according the type of legend.
     */
    public void onThumbnailChange(ListSelectionEvent lse) {
        if (imageLabel == null) {
            imageLabel = new JLabel();
        }
        imageLabel.setIcon(getThumbnail());
    }

    @Override
    public String getTitle() {
        return I18N.tr("Legend selection");
    }

    @Override
    public String validateInput() {
        if (lst.getSelectedIndex() == -1) {
            return UIFactory.getI18n().tr("A legend must be selected.");
        }
        return null;
    }

    @Override
    public Component getComponent() {
        return mainPanel;
    }
    
    /**
     * Create the thumbnail image
     *
     * @return
     */
    public ImageIcon getThumbnail() {
        ILegendPanel legendPanel = (ILegendPanel) getSelected();
        String legendId = legendPanel.getLegend().getLegendTypeId();
        if (legendId.equals("org.orbisgis.legend.thematic.proportional.ProportionalPoint")) {
            return createImageIcon("thumbnail_proportionnal_point.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.proportional.ProportionalLine")) {
            return createImageIcon("thumbnail_proportionnal_line.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.constant.UniqueSymbolPoint")) {
            return createImageIcon("thumbnail_unique_point.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.constant.UniqueSymbolLine")) {
            return createImageIcon("thumbnail_unique_line.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.constant.UniqueSymbolArea")) {
            return createImageIcon("thumbnail_unique_area.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.recode.RecodedLine")) {
            return createImageIcon("thumbnail_recoded_line.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.recode.RecodedArea")) {
            return createImageIcon("thumbnail_recoded_area.png");
        } else if (legendId.equals("org.orbisgis.legend.thematic.recode.RecodedPoint")) {
            return createImageIcon("thumbnail_recoded_point.png");
        } else {
            return createImageIcon("thumbnail_not_found.png");
        }
    }
    
    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = LegendUIChooser.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            LOGGER.error(I18N.tr("Couldn't find file") + ": " + path);
            return null;
        }
    }

    public Object getSelected() {
        return ids[lst.getSelectedIndex()];
    }

    public int getSelectedIndex() {
        return lst.getSelectedIndex();
    }

    public Object[] getSelectedElements() {
        ArrayList<Object> ret = new ArrayList<Object>();
        int[] indexes = lst.getSelectedIndices();
        for (int index : indexes) {
            ret.add(ids[index]);
        }

        return ret.toArray();
    }

    @Override
    public URL getIconURL() {
        return UIFactory.getDefaultIcon();
    }
}
