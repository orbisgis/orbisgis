/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
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
package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.ui.components.file.FileDrop;
import org.orbisgis.core.ui.components.jlist.OGList;
import org.orbisgis.core.ui.components.sif.AskValue;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.pluginSystem.message.ErrorMessages;
import org.orbisgis.core.ui.pluginSystem.workbench.OrbisConfiguration;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.AllExcludeSytemTableFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.AlphanumericFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.DBsFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.FilesFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.GeoFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.GeocatalogFilterDecorator;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.IFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.RasterFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.TableSystemFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.VectorialFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.filters.WMSFilter;
import org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.SourceRenderer;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.CollectionUtils;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.utils.I18N;

public class Catalog extends JPanel implements DragGestureListener,
        DragSourceListener, WorkbenchFrame {

        private static final String AC_BTN_DEL_TAG = "del";
        private static final String AC_BTN_ADD_TAG = "add";
        private static final int FILTER_VISIBLE_ROW_COUNT = 6;
        private HashMap<String, HashSet<String>> tagSources = new HashMap<String, HashSet<String>>();
        private SourceListModel listModel;
        private OGList lstSources;
        private DragSource dragSource;
        private JButtonTextField txtFilter;
        private JToggleButton btnToggleFilters;
        private JPanel pnlFilters;
        private JList lstFilters;
        private JList lstTags;
        private JButton btnDelTag;
        private DefaultListModel tagListModel;
        private org.orbisgis.core.ui.pluginSystem.menu.MenuTree menuTree;
        private HashMap<String, EditableSource> editingSources;

        @Override
        public org.orbisgis.core.ui.pluginSystem.menu.MenuTree getMenuTreePopup() {
                return menuTree;
        }

        public Catalog() {
                menuTree = new org.orbisgis.core.ui.pluginSystem.menu.MenuTree();
                lstSources = new OGList();
                lstSources.addMouseListener(new MouseAdapter() {

                        @Override
                        public void mousePressed(MouseEvent e) {
                                showPopup(e);
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                                showPopup(e);
                        }

                        private void showPopup(MouseEvent e) {
                                if (e.getButton() == MouseEvent.BUTTON3) {
                                        int path = -1;
                                        for (int i = 0; i < listModel.getSize(); i++) {
                                                if (lstSources.getCellBounds(i, i).contains(
                                                        e.getPoint())) {
                                                        path = i;
                                                        break;
                                                }
                                        }
                                        int[] selectionPaths = lstSources.getSelectedIndices();
                                        if ((selectionPaths != null) && (path != -1)) {
                                                if (!CollectionUtils.contains(selectionPaths, path)) {
                                                        if (e.isControlDown()) {
                                                                lstSources.addSelectionInterval(path, path);
                                                        } else {
                                                                lstSources.setSelectionInterval(path, path);
                                                        }
                                                }
                                        } else if (path == -1) {
                                                lstSources.clearSelection();
                                        } else {
                                        }
                                }
                                if (e.isPopupTrigger()) {
                                        JPopupMenu popup = getPopup();
                                        if (popup != null) {
                                                popup.show(e.getComponent(), e.getX(), e.getY());
                                        }
                                }
                        }
                });
                listModel = new SourceListModel();
                lstSources.setModel(listModel);

                this.setLayout(new BorderLayout());
                this.add(new JScrollPane(lstSources), BorderLayout.CENTER);
                this.add(getNorthPanel(), BorderLayout.NORTH);
                SourceListRenderer cellRenderer = new SourceListRenderer(this);
                cellRenderer.setRenderers(new SourceRenderer[0]);
                lstSources.setCellRenderer(cellRenderer);

                dragSource = DragSource.getDefaultDragSource();
                dragSource.createDefaultDragGestureRecognizer(lstSources,
                        DnDConstants.ACTION_COPY_OR_MOVE, this);
                editingSources = new HashMap<String, EditableSource>();

                //Init the file drop system
                FileDrop fileDrop = new FileDrop(this, new FileDrop.Listener() {

                        @Override
                        public void filesDropped(java.io.File[] files) {
                                DataManager dm = (DataManager) Services.getService(DataManager.class);
                                SourceManager sourceManager = dm.getSourceManager();
                                for (File file : files) {
                                        // For each file, we ensure that we have a driver
                                        // that can be used to read it. If we don't, we don't
                                        // open the file.
                                        if (OrbisConfiguration.isFileEligible(file)) {
                                                try {
                                                        String name = sourceManager.getUniqueName(FileUtils.getFileNameWithoutExtensionU(file));
                                                        sourceManager.register(name, file);
                                                } catch (SourceAlreadyExistsException e) {
                                                        ErrorMessages.error(ErrorMessages.SourceAlreadyRegistered
                                                                + ": ", e);
                                                }
                                        }
                                }


                        }
                });

        }

        private JPanel getNorthPanel() {
                JPanel ret = new JPanel();
                ret.setLayout(new BorderLayout());
                JPanel pnlTextFilter = new JPanel();
                CRFlowLayout layout = new CRFlowLayout();
                layout.setAlignment(CRFlowLayout.LEFT);
                pnlTextFilter.setLayout(layout);
                txtFilter = new JButtonTextField();
                txtFilter.getDocument().addDocumentListener(new DocumentListener() {

                        @Override
                        public void removeUpdate(DocumentEvent e) {
                                doFilter();
                        }

                        @Override
                        public void insertUpdate(DocumentEvent e) {
                                doFilter();
                        }

                        @Override
                        public void changedUpdate(DocumentEvent e) {
                                doFilter();
                        }
                });
                pnlTextFilter.add(txtFilter);
                btnToggleFilters = new JToggleButton(OrbisGISIcon.FILTER);
                btnToggleFilters.setMargin(new Insets(0, 0, 0, 0));
                btnToggleFilters.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                pnlFilters.setVisible(!pnlFilters.isVisible());
                        }
                });
                btnToggleFilters.setBorderPainted(false);
                btnToggleFilters.setContentAreaFilled(false);
                pnlTextFilter.add(btnToggleFilters);
                ret.add(pnlTextFilter, BorderLayout.NORTH);
                JPanel pnlFilters = getFilterAndTagPanel();
                ret.add(pnlFilters, BorderLayout.CENTER);
                return ret;
        }

        private void doFilter() {
                Object[] filterObjects = lstFilters.getSelectedValues();
                ArrayList<IFilter> filters = new ArrayList<IFilter>();
                for (int i = 0; i < filterObjects.length; i++) {
                        filters.add((GeocatalogFilterDecorator) filterObjects[i]);
                }

                Object[] tags = lstTags.getSelectedValues();
                for (Object object : tags) {
                        String tag = object.toString();
                        filters.add(new TagFilter(tag));
                }

                listModel.filter(txtFilter.getText(), filters);
        }

        private JPanel getFilterAndTagPanel() {
                pnlFilters = new JPanel();
                pnlFilters.setVisible(false);
                pnlFilters.setLayout(new GridBagLayout());
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 1;
                c.weighty = 0;
                c.gridwidth = 2;
                pnlFilters.add(
                        new JLabel(
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.openFilter")),
                        c);
                c.fill = GridBagConstraints.BOTH;
                c.weightx = 0.5;
                c.weighty = 1;
                c.gridy = 1;
                c.gridwidth = 1;
                pnlFilters.add(getFilterPanel(), c);
                c.gridx = 1;
                pnlFilters.add(getTagPanel(), c);

                return pnlFilters;
        }

        private JPanel getFilterPanel() {
                JPanel ret = new JPanel();
                ret.setBorder(BorderFactory.createTitledBorder(I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.filters")));
                ret.setLayout(new BorderLayout());

                GeocatalogFilterDecorator filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.AllExcludeSystemTable",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.allExceptFilter"),
                        new AllExcludeSytemTableFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.GeoFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.geoFilter"),
                        new GeoFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.FilesFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.fileFilter"),
                        new FilesFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.DBsFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.dbFilter"),
                        new DBsFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.AlphanumericFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.alphanumericFilter"),
                        new AlphanumericFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.WMSFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.wmsFilter"),
                        new WMSFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.RasterFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.rasterFilter"),
                        new RasterFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.VectorialFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.vectorialFilter"),
                        new VectorialFilter());
                listModel.getFilters().add(filter);

                filter = new GeocatalogFilterDecorator(
                        "geocatalog.filters.SystemTableFilter",
                        I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.systemTableFilter"),
                        new TableSystemFilter());
                listModel.getFilters().add(filter);

                // TODO maybe improve the default filter set.
                // new GeocatalogFilterDecorator[1]
                lstFilters = new JList(listModel.getFilters().toArray(
                        new GeocatalogFilterDecorator[1])/* getAvailableFilters() */);
                lstFilters.setCellRenderer(new DefaultListCellRenderer() {

                        @Override
                        public Component getListCellRendererComponent(JList list,
                                Object value, int index, boolean isSelected,
                                boolean cellHasFocus) {
                                return super.getListCellRendererComponent(list,
                                        ((GeocatalogFilterDecorator) value).getName(), index,
                                        isSelected, cellHasFocus);
                        }
                });
                lstFilters.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {

                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                        doFilter();
                                }
                        });
                lstFilters.setVisibleRowCount(FILTER_VISIBLE_ROW_COUNT);
                JScrollPane scroll = new JScrollPane(lstFilters);
                ret.add(scroll, BorderLayout.CENTER);
                return ret;
        }

        private JPanel getTagPanel() {
                JPanel ret = new JPanel();
                ret.setBorder(BorderFactory.createTitledBorder(I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.tags")));
                ret.setLayout(new BorderLayout());
                lstTags = new JList();
                tagListModel = new DefaultListModel();
                refreshTagModel();
                lstTags.setModel(tagListModel);
                lstTags.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {

                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                        doFilter();
                                        btnDelTag.setEnabled(lstTags.getSelectedIndex() != -1);
                                }
                        });
                lstTags.setVisibleRowCount(FILTER_VISIBLE_ROW_COUNT - 1);
                JScrollPane scroll = new JScrollPane(lstTags);
                ret.add(scroll, BorderLayout.CENTER);
                JPanel pnlButtons = new JPanel();
                JButton btnAdd = getTagManagementButton(OrbisGISIcon.ADD,
                        AC_BTN_ADD_TAG);
                btnAdd.setBorderPainted(false);
                btnAdd.setContentAreaFilled(false);
                btnDelTag = getTagManagementButton(OrbisGISIcon.DEL, AC_BTN_DEL_TAG);
                btnDelTag.setEnabled(false);
                btnDelTag.setBorderPainted(false);
                btnDelTag.setContentAreaFilled(false);
                pnlButtons.add(btnAdd);
                pnlButtons.add(btnDelTag);
                ret.add(pnlButtons, BorderLayout.SOUTH);
                return ret;
        }

        private void refreshTagModel() {
                String[] tags = getTags();
                tagListModel.clear();
                for (String tag : tags) {
                        tagListModel.addElement(tag);
                }
        }

        private JButton getTagManagementButton(ImageIcon icon, String actionCommand) {
                JButton ret = new JButton(icon);
                ret.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                if (AC_BTN_ADD_TAG.equals(e.getActionCommand())) {
                                        AskValue av = new AskValue(
                                                I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.newTag"),
                                                I18N.getString("orbisgis.org.orbisgis.core.ui.message.askValue.sql"),
                                                I18N.getString("orbisgis.org.orbisgis.core.ui.message.askValue.error"));
                                        if (UIFactory.showDialog(av)) {
                                                String tagText = av.getValue();
                                                addTag(tagText);
                                        }
                                } else if (AC_BTN_DEL_TAG.equals(e.getActionCommand())) {
                                        tagSources.remove(lstTags.getSelectedValue());
                                        refreshTagModel();
                                }
                        }
                });
                ret.setActionCommand(actionCommand);
                ret.setMargin(new Insets(0, 0, 0, 0));
                return ret;
        }

        public void addTag(String tagText) {
                tagSources.put(tagText, new HashSet<String>());
                refreshTagModel();
        }

        public String[] getTags() {
                return tagSources.keySet().toArray(new String[0]);
        }

        public JPopupMenu getPopup() {
                JPopupMenu popup = new JPopupMenu();
                JComponent[] menus = menuTree.getJMenus();
                for (JComponent menu : menus) {
                        popup.add(menu);
                }

                if (!tagSources.isEmpty() && lstSources.getSelectedIndices().length > 0) {
                        JMenu menu = new JMenu(
                                I18N.getString("orbisgis.org.orbisgis.core.ui.plugins.views.geocatalog.tag"));
                        Iterator<String> tagsIterator = tagSources.keySet().iterator();
                        while (tagsIterator.hasNext()) {
                                String tag = tagsIterator.next();
                                JCheckBoxMenuItem item;
                                if (isSelectionTagged(tag)) {
                                        item = new JCheckBoxMenuItem(tag, true);
                                        RemoveTagActionListener removeTagAL = new RemoveTagActionListener(
                                                tag);
                                        item.addActionListener(removeTagAL);
                                } else {
                                        item = new JCheckBoxMenuItem(tag, false);
                                        AddTagActionListener addTagAL = new AddTagActionListener(
                                                tag);
                                        item.addActionListener(addTagAL);
                                }
                                menu.add(item);
                        }
                        popup.addSeparator();
                        popup.add(menu);
                }

                return popup;
        }

        private boolean isSelectionTagged(String tag) {
                HashSet<String> tagSourceSet = tagSources.get(tag);
                Object[] selectedValues = lstSources.getSelectedValues();
                for (Object source : selectedValues) {
                        if (!tagSourceSet.contains(source)) {
                                return false;
                        }
                }

                return selectedValues.length > 0;
        }

        public String[] getSelectedSources() {
                Object[] selectedValues = lstSources.getSelectedValues();
                String[] sources = new String[selectedValues.length];
                for (int i = 0; i < sources.length; i++) {
                        sources[i] = selectedValues[i].toString();
                }
                return sources;
        }

        public Transferable getDragData(DragGestureEvent dge) {
                String[] sources = getSelectedSources();
                if (sources.length > 0) {
                        return new TransferableSource(sources);
                } else {
                        return null;
                }
        }

        void delete() {
                listModel.freeResources();
        }

        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
                Transferable dragData = getDragData(dge);
                if (dragData != null) {
                        dragSource.startDrag(dge, DragSource.DefaultMoveDrop, dragData,
                                this);
                }
        }

        @Override
        public void dragDropEnd(DragSourceDropEvent dsde) {
        }

        @Override
        public void dragEnter(DragSourceDragEvent dsde) {
        }

        @Override
        public void dragExit(DragSourceEvent dse) {
        }

        @Override
        public void dragOver(DragSourceDragEvent dsde) {
        }

        @Override
        public void dropActionChanged(DragSourceDragEvent dsde) {
        }

        public String[] getActiveFiltersId() {
                Object[] filters = lstFilters.getSelectedValues();
                String[] ids = new String[filters.length];
                for (int i = 0; i < ids.length; i++) {
                        ids[i] = ((GeocatalogFilterDecorator) filters[i]).getId();
                }

                return ids;
        }

        public void setActiveFiltersId(String[] filterIds) {
                ListSelectionModel selectionModel = lstFilters.getSelectionModel();
                ListModel filterModel = lstFilters.getModel();
                selectionModel.clearSelection();
                selectionModel.setValueIsAdjusting(true);
                for (String filterId : filterIds) {
                        GeocatalogFilterDecorator filter = new GeocatalogFilterDecorator(
                                filterId, null, null);
                        int index = -1;
                        for (int i = 0; i < filterModel.getSize(); i++) {
                                if (filterModel.getElementAt(i).equals(filter)) {
                                        index = i;
                                }
                        }
                        if (index != -1) {
                                selectionModel.addSelectionInterval(index, index);
                        }
                }
                selectionModel.setValueIsAdjusting(false);
        }

        public boolean isEditingSource(String source) {
                EditableSource s = editingSources.get(source);
                return s != null && s.isEditing();
        }

        public EditableSource getEditingSource(String name) {
                return editingSources.get(name);
        }

        /**
         * @param currentEditedSource the currentEditedSource to add
         */
        public void addEditingSource(String currentEditedSource, EditableSource source) {
                editingSources.put(currentEditedSource, source);
        }

        public void removeEditingSource(String currentEditedSource) {
                EditableSource s = editingSources.get(currentEditedSource);
                if (s != null) {
                        s.setEditing(false);
                }
        }

        public class TagFilter implements IFilter {

                private String tag;

                public TagFilter(String tag) {
                        this.tag = tag;
                }

                @Override
                public boolean accepts(SourceManager sm, String sourceName) {
                        HashSet<String> sources = tagSources.get(tag);
                        if (sources.contains(sourceName)) {
                                return true;
                        }
                        return false;
                }
        }

        private class AddTagActionListener implements ActionListener {

                private String tag;

                public AddTagActionListener(String tag) {
                        this.tag = tag;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                        Object[] sources = lstSources.getSelectedValues();
                        for (Object source : sources) {
                                String tagText = tag;
                                tagSource(tagText, source.toString());
                        }
                }
        }

        private class RemoveTagActionListener implements ActionListener {

                private String tag;

                public RemoveTagActionListener(String tag) {
                        this.tag = tag;
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                        Object[] sources = lstSources.getSelectedValues();
                        for (Object source : sources) {
                                tagSources.get(tag).remove(source.toString());
                        }
                }
        }

        public HashSet<String> getTaggedSources(String tag) {
                return tagSources.get(tag);
        }

        public void tagSource(String tagText, String source) {
                tagSources.get(tagText).add(source);
        }

        public boolean isTagSelected(String tag) {
                Object[] tags = lstTags.getSelectedValues();
                for (Object selectedTag : tags) {
                        if (selectedTag.equals(tag)) {
                                return true;
                        }
                }

                return false;
        }

        public void setActiveLabels(String[] labels) {
                ListSelectionModel selectionModel = lstTags.getSelectionModel();
                selectionModel.clearSelection();
                for (String label : labels) {
                        selectionModel.setValueIsAdjusting(true);
                        int index = -1;
                        for (int i = 0; i < tagListModel.getSize(); i++) {
                                if (tagListModel.getElementAt(i).equals(label)) {
                                        index = i;
                                }
                        }
                        if (index != -1) {
                                selectionModel.addSelectionInterval(index, index);
                        }
                }
                selectionModel.setValueIsAdjusting(false);
        }

        /**
         * Get the filters stored in a JList
         * @return {@link JList}
         */
        public JList getListFilters() {
                return lstFilters;
        }
}
