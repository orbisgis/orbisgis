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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.factory.LegendFactory;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;
import org.orbisgis.view.components.renderers.TreeLaFRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanelFactory;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.LegendTreeModel;
import org.orbisgis.view.toc.wrapper.RuleWrapper;
import org.orbisgis.view.toc.wrapper.StyleWrapper;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * A panel embedding a JTree representing the legend structure (as well as some
 * buttons to manage it).
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 */
public class LegendTree extends JPanel {

    private static final I18n I18N = I18nFactory.getI18n(LegendTree.class);
    private JTree tree;
    private SimpleStyleEditor simpleStyleEditor;
    private JToolBar toolBar;
    private JButton jButtonMenuDel;
    private JButton jButtonMenuDown;
    private JButton jButtonMenuRename;
    private JButton jButtonMenuUp;

    public LegendTree(final SimpleStyleEditor simpleEditor) {
        simpleStyleEditor = simpleEditor;

        StyleWrapper style = simpleStyleEditor.getStyleWrapper();
        //We create our tree
        tree = new JTree();
        //We don't want to display the root.
        tree.setRootVisible(true);
        //We have a custom model to provide... Listeners on the TreeModel
        //are added by the tree when calling setModel.
        LegendTreeModel ltm = new LegendTreeModel(tree, style);
        tree.setModel(ltm);
        //..A custom cell editor...
        LegendTreeCellEditor editor = new LegendTreeCellEditor();
        editor.setClickCountToStart(2);
        tree.setCellEditor(editor);
        //...and a custom TreeCellRenderer.
        LegendCellRenderer lcr = new LegendCellRenderer(tree);
        tree.setCellRenderer(lcr);
        //We want to select only one element at a time.
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        selectAndShowFirstLegend(style);
        //We refresh icons when the selection changes.
        TreeSelectionListener tsl = EventHandler.create(
                TreeSelectionListener.class, this, "refreshIcons");
        tree.addTreeSelectionListener(tsl);
        //We refresh the CardLayout of the associated SimpleStyleEditor
        TreeSelectionListener select = EventHandler.create(
                TreeSelectionListener.class, simpleStyleEditor, "legendSelected");
        tree.addTreeSelectionListener(select);
        expandAll(tree);
        //We want an editable tree
        tree.setEditable(true);
        initButtons();
        this.setLayout(new BorderLayout());
        this.add(toolBar, BorderLayout.PAGE_START);
        JScrollPane scrollPane = new JScrollPane(tree);
        this.add(scrollPane, BorderLayout.CENTER);
        refreshIcons();
    }

    /**
     * Removes the currently selected element from the tree. If it is an inner
     * node, all its children will be lost.
     */
    public void removeSelectedElement() {
        TreePath tp = tree.getSelectionPath();
        Object select = tp.getLastPathComponent();
        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
        if (select instanceof ILegendPanel) {
            RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length - 2];
            tree.setSelectionPath(null);
            tm.removeElement(rw, select);
            //We refresh the legend container
            simpleStyleEditor.showDialogForCurrentlySelectedLegend();
            //We refresh the icons
        } else if (select instanceof RuleWrapper) {
            tree.setSelectionPath(null);
            tm.removeElement(tm.getRoot(), select);
            simpleStyleEditor.showDialogForCurrentlySelectedLegend();
        }
        refreshIcons();
    }

    /**
     * Adds an element to the tree. It will open a window to let the user choose
     * which type of element (legend or rule) it must be.
     */
    public void addElement() {
        TreePath tp = tree.getSelectionPath();
        if (tp == null) {
            addRule();
        } else {
            Object select = tp.getLastPathComponent();
            if (select instanceof StyleWrapper) {
                addRule();
            } else {
                addLegend();
            }
        }
        refreshIcons();
    }

    /**
     * Move the currently selected element (if any) up in the model.
     */
    public void moveSelectedElementUp() {
        TreePath tp = tree.getSelectionPath();
        Object select = tp.getLastPathComponent();
        if (select instanceof RuleWrapper) {
            LegendTreeModel tm = (LegendTreeModel) tree.getModel();
            tm.moveElementUp(tm.getRoot(), select);
            refreshIcons();
        } else if (select instanceof ILegendPanel) {
            RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length - 2];
            LegendTreeModel tm = (LegendTreeModel) tree.getModel();
            tm.moveElementUp(rw, select);
            refreshIcons();
        }
    }

    /**
     * Move the currently selected element (if any) down in its structure.
     */
    public void moveSelectedElementDown() {
        TreePath tp = tree.getSelectionPath();
        Object select = tp.getLastPathComponent();
        if (select instanceof RuleWrapper) {
            LegendTreeModel tm = (LegendTreeModel) tree.getModel();
            tm.moveElementDown(tm.getRoot(), select);
            refreshIcons();
        } else if (select instanceof ILegendPanel) {
            RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length - 2];
            LegendTreeModel tm = (LegendTreeModel) tree.getModel();
            tm.moveElementDown(rw, select);
            refreshIcons();
        }
    }

    /**
     * Start editing the name of the currently selected element.
     *
     * @param evt The initial event.
     */
    public final void renameElement(ActionEvent evt) {
        TreePath tp = tree.getSelectionPath();
        tree.startEditingAtPath(tp);
    }

    /**
     * Get the currently selected Legend, if any. If we find it, we return it.
     * Otherwise, we return null.
     *
     * @return The currently selected panel, or null if none is selected.
     */
    public ILegendPanel getSelectedLegend() {
        TreePath tp = tree.getSelectionPath();
        if (tp != null) {
            Object last = tp.getLastPathComponent();
            if (last instanceof ILegendPanel) {
                return (ILegendPanel) last;
            }
        }
        return null;
    }

    /**
     * Gets the {@code ISELegendPanel} that is associated to the currently
     * selected element in the tree.
     *
     * @return The currently selected panel, or null if none is selected.
     */
    public ISELegendPanel getSelectedPanel() {
        TreePath tp = tree.getSelectionPath();
        if (tp != null) {
            Object last = tp.getLastPathComponent();
            if (last instanceof ILegendPanel) {
                return (ILegendPanel) last;
            } else if (last instanceof RuleWrapper) {
                return ((RuleWrapper) last).getPanel();
            } else if (last instanceof StyleWrapper) {
                return ((StyleWrapper) last).getPanel();
            }
        }
        return null;
    }

    /**
     * Refreshes the state of the icons contained in the toolbar of this panel.
     */
    public final void refreshIcons() {
        //We must retrieve the index of the currently selected item in its
        //parent to decide if we display the buttons or not.
        TreePath tp = tree.getSelectionPath();
        if (tp == null) {
            jButtonMenuDel.setEnabled(false);
            jButtonMenuRename.setEnabled(false);
            jButtonMenuDown.setEnabled(false);
            jButtonMenuUp.setEnabled(false);
        } else {
            jButtonMenuRename.setEnabled(true);
            Object last = tp.getLastPathComponent();
            int index = -1;
            int max = -1;
            if (last instanceof StyleWrapper) {
                max = 0;
                index = 0;
            } else if (last instanceof RuleWrapper) {
                StyleWrapper sw = simpleStyleEditor.getStyleWrapper();
                index = sw.indexOf((RuleWrapper) last);
                max = sw.getSize() - 1;
            } else if (last instanceof ILegendPanel) {
                RuleWrapper rw = getSelectedRule();
                index = rw.indexOf((ILegendPanel) last);
                max = rw.getSize() - 1;
            }
            if (index == 0) {
                jButtonMenuUp.setEnabled(false);
            } else {
                jButtonMenuUp.setEnabled(true);
            }
            if (index < max) {
                jButtonMenuDown.setEnabled(true);
            } else {
                jButtonMenuDown.setEnabled(false);
            }
            if (max < 1) {
                jButtonMenuDel.setEnabled(false);
            } else {
                jButtonMenuDel.setEnabled(true);
            }
        }
    }

    /**
     * Tests if we have a legend in our tree.
     *
     * @return
     */
    public boolean hasLegend() {
        LegendTreeModel ltm = (LegendTreeModel) tree.getModel();
        return ltm.hasLegend();
    }

    void refresh() {
        refreshIcons();
        refreshModel();
    }

    /**
     * Initialize all the buttons that can be used to manage the tree content.
     */
    private void initButtons() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        jButtonMenuUp = new JButton();
        jButtonMenuUp.setIcon(OrbisGISIcon.getIcon("go-up"));
        jButtonMenuUp.setToolTipText(I18N.tr("Up"));
        ActionListener alu = EventHandler.create(
                ActionListener.class, this, "moveSelectedElementUp");
        jButtonMenuUp.addActionListener(alu);
        toolBar.add(jButtonMenuUp);

        jButtonMenuDown = new JButton();
        jButtonMenuDown.setIcon(OrbisGISIcon.getIcon("go-down"));
        jButtonMenuDown.setToolTipText(I18N.tr("Down"));
        ActionListener ald = EventHandler.create(
                ActionListener.class, this, "moveSelectedElementDown");
        jButtonMenuDown.addActionListener(ald);
        toolBar.add(jButtonMenuDown);

        JButton jButtonMenuAdd = new JButton();
        jButtonMenuAdd.setIcon(OrbisGISIcon.getIcon("picture_add"));
        jButtonMenuAdd.setToolTipText(I18N.tr("Add"));
        ActionListener aladd = EventHandler.create(
                ActionListener.class, this, "addElement");
        jButtonMenuAdd.addActionListener(aladd);
        jButtonMenuAdd.setFocusPainted(false);
        toolBar.add(jButtonMenuAdd);

        jButtonMenuDel = new JButton();
        jButtonMenuDel.setIcon(OrbisGISIcon.getIcon("picture_delete"));
        jButtonMenuDel.setToolTipText(I18N.tr("Delete"));
        ActionListener alrem = EventHandler.create(
                ActionListener.class, this, "removeSelectedElement");
        jButtonMenuDel.addActionListener(alrem);
        toolBar.add(jButtonMenuDel);

        jButtonMenuRename = new JButton();
        jButtonMenuRename.setIcon(OrbisGISIcon.getIcon("picture_edit"));
        jButtonMenuRename.setToolTipText(I18N.tr("Rename"));
        jButtonMenuRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                renameElement(evt);
            }
        });
        toolBar.add(jButtonMenuRename);
    }

    /**
     * Add a legend to the tree, in the currently selected RuleWrapper, after
     * the currently selected Legend (if any in both case). A RuleWrapper will
     * be added in the case there is none.
     */
    private void addLegend() {
        LegendUIChooser legendPicker = new LegendUIChooser(simpleStyleEditor);

        if (UIFactory.showDialog(legendPicker)) {
            // Recover the panel that was selected when the user clicked OK.
            ILegendPanel ilp = legendPicker.getSelectedPanel();

            // Get the currently selected RuleWrapper, or the last one in this
            // style if none is currently selected.
            RuleWrapper currentrw = getSelectedRule();
            StyleWrapper sw = simpleStyleEditor.getStyleWrapper();
            if (currentrw == null) {
                if (sw.getSize() == 0) {
                    addRule();
                }
                currentrw = sw.getRuleWrapper(sw.getSize() - 1);
            }

            // Set the Legend's name.
            Legend legend = ilp.getLegend();
            legend.getSymbolizer().setName(
                getUniqueName(legend.getLegendTypeName(),
                              currentrw.getRule(), 0));

            // Add the panel to the LegendTree.
            ((LegendTreeModel) tree.getModel())
                    .addElement(currentrw, ilp, getSelectedLegend());

            // Automatically select the newly added legend in the tree.
            TreePath selectionPath = tree.getSelectionPath();
            TreePath parent;
            if(selectionPath.getLastPathComponent() instanceof RuleWrapper){
                parent = selectionPath;
            } else {
                parent = selectionPath.getParentPath();
            }
            tree.setSelectionPath(parent.pathByAddingChild(ilp));

            // Notify the SimpleStyleEditor that a Legend has been added.
            simpleStyleEditor.legendAdded(ilp);
        }
    }

    /**
     * Get a name for a Symbolizer that is not already contained in the rule.
     *
     * @param n The base name
     * @param r the rule where we search
     * @param i An int we'll try to had to reach unicity
     *
     * @return The unique name.
     */
    private String getUniqueName(String n, Rule r, int i) {
        int a = i < 0 ? 0 : i;
        String ret = n;
        if (a > 0) {
            ret = n + " " + a;
        }
        boolean contained = false;
        List<Symbolizer> symbolizerList = r.getCompositeSymbolizer().
                getSymbolizerList();
        for (int p = 0; p < symbolizerList.size() && !contained; p++) {
            Symbolizer s = symbolizerList.get(p);
            if (s.getName().equals(ret)) {
                contained = true;
            }
        }
        if (!contained) {
            return ret;
        } else {
            return getUniqueName(n, r, a + 1);
        }
    }

    private void addRule() {
        //We must add it just after the currently selected Rule.
        //Let's find which one it is. If there is none, we add it at the
        //end of the list.
        MultiInputPanel mip = new MultiInputPanel(I18N.tr(
                "Choose a name for your rule"));
        mip.addInput("RuleName", I18N.tr("Name of the Rule : "),
                     new TextBoxType(10));
        mip.addValidation(new MIPValidation() {
            @Override
            public String validate(MultiInputPanel mid) {
                String ruleName = mid.getInput("RuleName");
                return ruleName.isEmpty() ? I18N.tr(
                        "Rule name cannot be null or empty.") : null;
            }
        });
        if (UIFactory.showDialog(mip)) {
            String s = mip.getInput("RuleName");
            LegendTreeModel tm = (LegendTreeModel) tree.getModel();
            //We need to link our new RuleWrapper with the layer we are editing.
            Rule temp = new Rule(simpleStyleEditor.getStyleWrapper().getStyle().getLayer());
            temp.setName(s);
            Legend leg = LegendFactory.getLegend(
                    temp.getCompositeSymbolizer().getSymbolizerList().get(0));
            // Initialize a panel for this legend.
            ILegendPanel ilp = ILegendPanelFactory.getILegendPanel(
                    simpleStyleEditor, leg);
            List<ILegendPanel> list = new ArrayList<ILegendPanel>();
            list.add(ilp);
            RuleWrapper nrw = new RuleWrapper(simpleStyleEditor, temp, list);
            tm.addElement(tm.getRoot(), nrw, getSelectedRule());
            simpleStyleEditor.legendAdded(nrw.getPanel());
        }
    }

    /**
     * Get the currently selected Rule, if any. If we find it, we return it.
     * Otherwise, we return null.
     */
    private RuleWrapper getSelectedRule() {
        TreePath tp = tree.getSelectionPath();
        if (tp != null) {
            Object[] path = tp.getPath();
            for (int i = path.length - 1; i >= 0; i--) {
                if (path[i] instanceof RuleWrapper) {
                    return (RuleWrapper) path[i];
                }
            }
        }
        return null;
    }

    private void refreshModel() {
        ((LegendTreeModel) tree.getModel()).refresh();
    }

    /**
     * Selects the first legend attached to the given style in this
     * {@link LegendTree} and displays it in the Simple Style Editor's card
     * layout.
     *
     * @param style Style
     */
    private void selectAndShowFirstLegend(StyleWrapper style) {
        RuleWrapper firstRW = style.getRuleWrapper(0);
        ILegendPanel firstPanel = firstRW.getLegend(0);
        TreePath tp = new TreePath(style)
                .pathByAddingChild(firstRW)
                .pathByAddingChild(firstPanel);
        tree.setSelectionPath(tp);
        simpleStyleEditor.showDialogForLegend(firstPanel);
    }

    /**
     * A TreeCellRenderer dedicated to our tree. Paints text in red if the cell
     * is selected, in black otherwise.
     */
    private static class LegendCellRenderer extends TreeLaFRenderer {

        public LegendCellRenderer(JTree tree) {
            super(tree);
        }

        @Override
        public Component getTreeCellRendererComponent(
                JTree tree, Object value, boolean selected,
                boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component comp = lookAndFeelRenderer.getTreeCellRendererComponent(
                    tree, value, selected, expanded, leaf, row, hasFocus);
            if (comp instanceof JLabel) {
                JLabel lab = (JLabel) comp;
                if (value instanceof StyleWrapper) {
                    return getComponent((StyleWrapper) value, lab);
                } else if (value instanceof RuleWrapper) {
                    return getComponent((RuleWrapper) value, lab);
                } else if (value instanceof ILegendPanel) {
                    return getComponent((ILegendPanel) value, lab);
                } else {
                    lab.setText("root");
                }
            }
            return comp;
        }

        private Component getComponent(ILegendPanel legend, JLabel lab) {
            lab.setText(legend.getLegend().getName());
            return lab;
        }

        private Component getComponent(RuleWrapper rw, JLabel lab) {
            String s = rw.getRule().getName();
            if (s == null || s.isEmpty()) {
                s = "Unknown";
            }
            lab.setText(s);
            return lab;
        }

        private Component getComponent(StyleWrapper sw, JLabel lab) {
            String s = sw.getStyle().getName();
            if (s == null || s.isEmpty()) {
                s = "Unknown";
            }
            lab.setText(s);
            return lab;
        }
    }

    /**
     * The name of the selected element changed !
     */
    public void selectedNameChanged() {
        LegendTreeModel model = (LegendTreeModel) tree.getModel();
        model.refresh();
    }

    /**
     * Expand configuration tree
     *
     * @param tree The tree we want to process.
     */
    private void expandAll(JTree tree) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);
        }
    }
}
