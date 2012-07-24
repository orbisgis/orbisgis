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
package org.orbisgis.sif.components;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class CategorizedChoosePanel extends JPanel implements UIPanel {

        private String id;
        private String title;
        private Map<Option, List<Option>> categories = new HashMap<Option, List<Option>>();
        private JTree tree;
        private CategoriesTreeModel categoriesTreeModel;
        private static final I18n i18n = I18nFactory.getI18n(CategorizedChoosePanel.class);

        public CategorizedChoosePanel(String title, String id) {
                this.title = title;
                this.id = id;

                initComponents();
        }

        public void addOption(String categoryId, String categoryName, String name,
                String id, String icon) {
                Option category = new Option(categoryId, categoryName, true, null);
                List<Option> options = categories.get(category);
                if (options == null) {
                        options = new ArrayList<Option>();
                }
                options.add(new Option(id, name, false, icon));

                categories.put(category, options);

                categoriesTreeModel.refresh();
        }

        private void initComponents() {
                tree = new JTree();
                categoriesTreeModel = new CategoriesTreeModel();
                tree.setModel(categoriesTreeModel);
                tree.setRootVisible(false);
                tree.setCellRenderer(new IconRenderer());
                tree.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.SINGLE_TREE_SELECTION);
                this.setLayout(new BorderLayout());
                this.add(new JScrollPane(tree), BorderLayout.CENTER);
        }

        public String[] getErrorMessages() {
                return null;
        }
        

        public String getId() {
                return id;
        }

        public String[] getValues() {
                return new String[]{getSelectedElement()};
        }

        public void setValue(String fieldName, String fieldValue) {
                Iterator<Option> it = categories.keySet().iterator();
                while (it.hasNext()) {
                        Option category = it.next();
                        List<Option> options = categories.get(category);
                        for (Option option : options) {
                                if (option.getId().equals(fieldValue)) {
                                        tree.setSelectionPath(new TreePath(new Object[]{
                                                        categoriesTreeModel.getRoot(), category, option}));
                                        return;
                                }
                        }
                }
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public String getTitle() {
                return title;
        }

        @Override
        public String validateInput() {
                TreePath selectionPath = tree.getSelectionPath();
                if ((selectionPath == null)
                        || (((Option) selectionPath.getLastPathComponent()).isCategory())) {
                        return i18n.tr("An item must be selected.");
                }

                return null;
        }

        @Override
        public URL getIconURL() {
                return UIFactory.getDefaultIcon();
        }

              

        /**
         * Returns the id of the currently selected option if it's valid. If
         * there is no selection or the selection is not valid it returns null
         *
         * @return
         */
        public String getSelectedElement() {
                if (validateInput() == null) {
                        Object selection = tree.getSelectionPath().getLastPathComponent();
                        return ((Option) selection).getId();
                } else {
                        return null;
                }
        }

        private class Option {

                private String id;
                private String name;
                private boolean category;
                private String icon;

                public Option(String id, String name, boolean category, String icon) {
                        super();
                        this.id = id;
                        this.name = name;
                        this.category = category;
                        this.icon = icon;
                }

                public String getIcon() {
                        return icon;
                }

                public String getId() {
                        return id;
                }

                public String getName() {
                        return name;
                }

                @Override
                public String toString() {
                        return name;
                }

                @Override
                public boolean equals(Object obj) {
                        if (obj instanceof Option) {
                                Option opt = (Option) obj;
                                return id.equals(opt.id);
                        } else {
                                return false;
                        }
                }

                @Override
                public int hashCode() {
                        return id.hashCode();
                }

                public boolean isCategory() {
                        return category;
                }
        }

        private class CategoriesTreeModel implements TreeModel {

                private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

                @Override
                public void addTreeModelListener(TreeModelListener l) {
                        listeners.add(l);
                }

                public void refresh() {
                        for (TreeModelListener listener : listeners) {
                                listener.treeStructureChanged(new TreeModelEvent(this,
                                        new Object[]{getRoot()}));
                        }
                }

                @Override
                public Object getChild(Object parent, int index) {
                        List<Object> names = getArray(parent);
                        return names.get(index);
                }

                private List<Object> getArray(Object parent) {
                        List<Object> names = new ArrayList<Object>();
                        if (parent.toString().equals("ROOT")) {
                                // Categories
                                names.addAll(categories.keySet());
                        } else {
                                // Category content
                                List<Option> options = categories.get((Option)parent);
                                if (options != null) {
                                        names.addAll(options);
                                }
                        }
                        return names;
                }

                @Override
                public int getChildCount(Object parent) {
                        return getArray(parent).size();
                }

                @Override
                public int getIndexOfChild(Object parent, Object child) {
                        return getArray(parent).indexOf(child);
                }

                @Override
                public Object getRoot() {
                        return new Option("ROOT", "ROOT", true, null);
                }

                @Override
                public boolean isLeaf(Object node) {
                        return !((Option) node).isCategory();
                }

                @Override
                public void removeTreeModelListener(TreeModelListener l) {
                        listeners.remove(l);
                }

                @Override
                public void valueForPathChanged(TreePath path, Object newValue) {
                }
        }

        private class IconRenderer extends DefaultTreeCellRenderer implements
                TreeCellRenderer {

                private Icon defaultClosedFolderIcon;
                private Icon defaultOpenFolderIcon;
                private Icon defaultLeafIcon;

                public IconRenderer() {
                        this.defaultClosedFolderIcon = this.getDefaultClosedIcon();
                        this.defaultOpenFolderIcon = this.getDefaultOpenIcon();
                        this.defaultLeafIcon = this.getLeafIcon();
                }

                @Override
                public Component getTreeCellRendererComponent(JTree tree, Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf, int row,
                        boolean hasFocus) {
                        Option option = (Option) value;
                        if (option.getIcon() != null) {
                                ImageIcon icon = new ImageIcon(this.getClass().getResource(
                                        option.getIcon()));
                                this.setLeafIcon(icon);
                        } else {
                                if (option.isCategory()) {
                                        this.setOpenIcon(defaultOpenFolderIcon);
                                        this.setClosedIcon(defaultClosedFolderIcon);
                                } else {
                                        this.setLeafIcon(defaultLeafIcon);
                                }
                        }
                        return super.getTreeCellRendererComponent(tree, value, sel,
                                expanded, leaf, row, hasFocus);
                }
        }
}
