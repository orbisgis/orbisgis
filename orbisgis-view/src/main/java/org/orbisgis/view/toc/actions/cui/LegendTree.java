/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.view.toc.actions.cui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.StringType;
import org.orbisgis.view.toc.actions.cui.components.LegendPicker;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.LegendTreeModel;
import org.orbisgis.view.toc.wrapper.RuleWrapper;
import org.orbisgis.view.toc.wrapper.StyleWrapper;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.legend.Legend;
import org.orbisgis.sif.SIFMessage;
import org.orbisgis.sif.components.RadioButtonPanel;
import org.orbisgis.sif.multiInputPanel.MIPValidation;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This class is a panel that embeds a JTree (representing the legend structure)
 * and some buttons that can be used to manage it.
 * @author alexis
 */
public class LegendTree extends JPanel {
        private static final I18n I18N = I18nFactory.getI18n(LegendTree.class);
        private JTree tree;
        private LegendsPanel legendsPanel;

        private JToolBar toolBar;
        private JButton jButtonMenuDel;
        private JButton jButtonMenuDown;
        private JButton jButtonMenuRename;
        private JButton jButtonMenuUp;

        public LegendTree(final LegendsPanel legendsPan){
                legendsPanel = legendsPan;

                StyleWrapper style = legendsPanel.getStyleWrapper();
                //We create our tree
                tree = new JTree();
                //We don't want to display the root.
                tree.setRootVisible(true);
                //We have a custom model to provide... Listeners on the TreeModel
                //are added by the tree when calling setModel.
                LegendTreeModel ltm = new LegendTreeModel(tree, style);
                tree.setModel(ltm);
                //...and a custom TreeCellRenderer.
                LegendCellRenderer lcr = new LegendCellRenderer();
                tree.setCellRenderer(lcr);
                //We want to select only one element at a time.
                tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                //We refresh icons when the selection changes.
                TreeSelectionListener tsl = EventHandler.create(TreeSelectionListener.class, this, "refreshIcons");
                tree.addTreeSelectionListener(tsl);
                //We refresh the CardLayout of the associated LegendsPanel
                TreeSelectionListener tslb = EventHandler.create(TreeSelectionListener.class,legendsPanel,"legendSelected");
                tree.addTreeSelectionListener(tslb);
                initButtons();
                this.setLayout(new BorderLayout());
                this.add(toolBar, BorderLayout.PAGE_START);
                this.add(new JScrollPane(tree), BorderLayout.CENTER);
        }

        /**
         * Removes the currently selected element from the tree. If it is an
         * inner node, all its children will be lost.
         */
        public void removeSelectedElement(){
                TreePath tp = tree.getSelectionPath();
                Object select = tp.getLastPathComponent();
                LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                if(select instanceof ILegendPanel){
                        RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length -2];
                        tm.removeElement(rw, select);
                        //We refresh the icons
                        refreshIcons();
                        //We refresh the legend container
                        legendsPanel.refreshLegendContainer();
                } else if(select instanceof RuleWrapper){
                        tm.removeElement(tm.getRoot(), select);
                        refreshIcons();
                        legendsPanel.refreshLegendContainer();
                }
        }

        /**
         * Adds an element to the tree. It will open a window to let the user
         * choose which type of element (legend or rule) it must be.
         */
        public void addElement(){
                List<String> ls = new LinkedList<String>();
                String rule = "Rule";
                ls.add(rule);
                String leg = "Legend";
                ls.add(leg);
                RadioButtonPanel rbp = new RadioButtonPanel(ls, "Choose the type of the object you want to add");
                if(UIFactory.showDialog(rbp)){
                        String ret = rbp.getSelectedText();
                        if(rule.equals(ret)){
                                addRule();
                        } else if (leg.equals(ret)){
                                addLegend();
                        }
                }
                refreshIcons();
        }

        /**
         * Move the currently selected element (if any) up in the model.
         */
        public void moveSelectedElementUp(){
                TreePath tp = tree.getSelectionPath();
                Object select = tp.getLastPathComponent();
                if(select instanceof RuleWrapper){
                        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                        tm.moveElementUp(tm.getRoot(), select);
                        refreshIcons();
                } else if(select instanceof ILegendPanel){
                        RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length -2];
                        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                        tm.moveElementUp(rw, select);
                        refreshIcons();
                }
        }

        /**
         * Move the currently selected element (if any) down in its structure.
         */
        public void moveSelectedElementDown(){
                TreePath tp = tree.getSelectionPath();
                Object select = tp.getLastPathComponent();
                if(select instanceof RuleWrapper){
                        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                        tm.moveElementDown(tm.getRoot(), select);
                        refreshIcons();
                } else if(select instanceof ILegendPanel){
                        RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length -2];
                        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                        tm.moveElementDown(rw, select);
                        refreshIcons();
                }
        }

        public final void renameElement(ActionEvent evt){
                throw new UnsupportedOperationException();
        }

        /**
         * Get the currently selected Legend, if any. If we find it, we return it.
         * Otherwise, we return null.
         * @return
         */
        public ILegendPanel getSelectedLegend(){
                TreePath tp = tree.getSelectionPath();
                if(tp!=null){
                        Object last = tp.getLastPathComponent();
                        if(last instanceof ILegendPanel){
                                return (ILegendPanel) last;
                        }
                }
                return null;
        }

        /**
         * Gets the {@code ISELegendPanel} that is associated to the currently
         * selected element in the tree.
         * @return
         */
        public ISELegendPanel getSelectedPanel(){
                TreePath tp = tree.getSelectionPath();
                if(tp!=null){
                        Object last = tp.getLastPathComponent();
                        if(last instanceof ILegendPanel){
                                return (ILegendPanel) last;
                        } else if(last instanceof RuleWrapper){
                                return (ISELegendPanel)((RuleWrapper)last).getPanel();
                        } else if(last instanceof StyleWrapper){
                                return (ISELegendPanel)((StyleWrapper)last).getPanel();
                        }
                }
                return null;

        }

        /**
         * Refreshes the state of the icons contained in the toolbar of this
         * panel.
         */
        public void refreshIcons(){
                //We must retrieve the index of the currently selected item in its
                //parent to decide if we display the buttons or not.
                TreePath tp = tree.getSelectionPath();
                if(tp == null){
                        jButtonMenuDel.setEnabled(false);
                        jButtonMenuRename.setEnabled(false);
                        jButtonMenuDown.setEnabled(false);
                        jButtonMenuUp.setEnabled(false);
                } else {
                        jButtonMenuDel.setEnabled(true);
                        jButtonMenuRename.setEnabled(true);
                        Object last = tp.getLastPathComponent();
                        int index=-1;
                        int max=-1;
                        if(last instanceof RuleWrapper){
                                StyleWrapper sw = legendsPanel.getStyleWrapper();
                                index = sw.indexOf((RuleWrapper) last);
                                max = sw.getSize()-1;
                        } else if(last instanceof ILegendPanel){
                                RuleWrapper rw = getSelectedRule();
                                index = rw.indexOf((ILegendPanel) last);
                                max = rw.getSize()-1;
                        }
                        if(index ==0){
                                jButtonMenuUp.setEnabled(false);
                        } else {
                                jButtonMenuUp.setEnabled(true);
                        }
                        if(index < max){
                                jButtonMenuDown.setEnabled(true);
                        } else {
                                jButtonMenuDown.setEnabled(false);
                        }

                }
        }

        /**
         * Tests if we have a legend in our tree.
         * @return
         */
        public boolean hasLegend() {
                LegendTreeModel ltm = (LegendTreeModel)tree.getModel();
                return ltm.hasLegend();
        }

        void refresh() {
                refreshIcons();
                refreshModel();
        }

        /**
         * Initialize all the buttons that can be used to manage the tree
         * content.
         */
        private void initButtons(){
                toolBar = new JToolBar();
                toolBar.setFloatable(false);

		jButtonMenuUp = new JButton();
		jButtonMenuUp.setIcon(OrbisGISIcon.getIcon("go-up"));
		jButtonMenuUp.setToolTipText(I18N.tr("Up"));
                ActionListener alu = EventHandler.create(ActionListener.class, this, "moveSelectedElementUp");
                jButtonMenuUp.addActionListener(alu);
		toolBar.add(jButtonMenuUp);

		jButtonMenuDown = new JButton();
		jButtonMenuDown.setIcon(OrbisGISIcon.getIcon("go-down"));
		jButtonMenuDown.setToolTipText(I18N.tr("Down"));
                ActionListener ald = EventHandler.create(ActionListener.class, this, "moveSelectedElementDown");
                jButtonMenuDown.addActionListener(ald);
		toolBar.add(jButtonMenuDown);

		JButton jButtonMenuAdd = new JButton();
		jButtonMenuAdd.setIcon(OrbisGISIcon.getIcon("picture_add"));
		jButtonMenuAdd.setToolTipText(I18N.tr("Add"));
                ActionListener aladd = EventHandler.create(ActionListener.class, this, "addElement");
		jButtonMenuAdd.addActionListener(aladd);
		toolBar.add(jButtonMenuAdd);

		jButtonMenuDel = new JButton();
		jButtonMenuDel.setIcon(OrbisGISIcon.getIcon("picture_delete"));
		jButtonMenuDel.setToolTipText(I18N.tr("Delete"));
                ActionListener alrem = EventHandler.create(ActionListener.class, this, "removeSelectedElement");
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
         * the currently selected Legend (if any in both case). A RuleWrapper
         * will be added in the case there is none.
         */
        private void addLegend(){
                StyleWrapper sw = legendsPanel.getStyleWrapper();
		ArrayList<String> paneNames = new ArrayList<String>();
		ArrayList<ILegendPanel> ids = new ArrayList<ILegendPanel>();
		ILegendPanel[] legends = legendsPanel.getAvailableLegends();
		for (int i = 0; i < legends.length; i++) {
			ILegendPanel legendPanelUI = legends[i];
			if (legendPanelUI.acceptsGeometryType(legendsPanel
					.getGeometryType())) {
				paneNames.add(legendPanelUI.getLegend().getLegendTypeName());
				ids.add(legendPanelUI);
			}
		}
		LegendPicker legendPicker = new LegendPicker(
                        paneNames.toArray(new String[paneNames.size()]),
                        ids.toArray(new ILegendPanel[ids.size()]));

		if (UIFactory.showDialog(legendPicker)) {
                        //We retrieve the legend we want to add
                        ILegendPanel ilp = (ILegendPanel) legendPicker.getSelected();
                        Legend leg = ilp.copyLegend();
                        ILegendPanel copy = (ILegendPanel)ilp.newInstance();
                        copy.setLegend(leg);

                        //We retrieve the rw where we will add it.
                        RuleWrapper currentrw = getSelectedRule();
                        if(currentrw == null ){
                                if(sw.getSize() == 0){
                                        addRule();
                                }
                                currentrw = sw.getRuleWrapper(sw.getSize()-1);
                        }
                        //We retrieve the index where to put it.
                        ILegendPanel sl = getSelectedLegend();
                        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                        tm.addElement(currentrw, copy, sl);
                        legendsPanel.legendAdded(copy);
                }
        }

        private void addRule(){
                //We must add it just after the currently selected Rule.
                //Let's find which one it is. If there is none, we add it at the
                //end of the list.
                MultiInputPanel mip = new MultiInputPanel("LegendTreeMip", "Choose a name for your rule", false);
                mip.addInput("RuleName", "Name of the Rule : ", new StringType(10));
                
                
                mip.addValidation(new MIPValidation() {

                        @Override
                        public SIFMessage validate(MultiInputPanel mid) {
                                String ruleName = mid.getInput("RuleName");
                                if (ruleName==null && ruleName.isEmpty()){
                                        return new SIFMessage("Rule name cannot be null or empty.", SIFMessage.ERROR);
                                }
                                return new SIFMessage();
                                
                        }
                });
                
                if(UIFactory.showDialog(mip)){
                        String s = mip.getValues()[0];
                        RuleWrapper cur = getSelectedRule();
                        LegendTreeModel tm = (LegendTreeModel) tree.getModel();
                        RuleWrapper nrw = new RuleWrapper(s);
                        tm.addElement(tm.getRoot(), nrw, cur);
                        legendsPanel.legendAdded(nrw.getPanel());
                }
        }

        /**
         * Get the currently selected Rule, if any. If we find it, we return it.
         * Otherwise, we return null.
         */
        private RuleWrapper getSelectedRule(){
                TreePath tp = tree.getSelectionPath();
                if(tp != null){
                        Object[] path = tp.getPath();
                        for(int i=path.length-1; i>=0; i--){
                                if(path[i] instanceof RuleWrapper){
                                        return (RuleWrapper) path[i];
                                }
                        }
                }
                return null;
        }

        private void refreshModel(){
                ((LegendTreeModel)tree.getModel()).refresh();
        }

        /**
         * A TreeCellRenderer dedicated to our tree. Paints text in red if the
         * cell is selected, in black otherwise.
         */
        private static class LegendCellRenderer implements TreeCellRenderer{

                private static final Color DESELECTED = Color.black;

                private static final Color SELECTED = Color.red;
                
                @Override
                public Component getTreeCellRendererComponent(
                                JTree tree, Object value,  boolean selected,
                                boolean expanded, boolean leaf, int row, boolean hasFocus) {
                        if(value instanceof StyleWrapper){
                                return getComponent((StyleWrapper)value, selected);
                        } else if(value instanceof RuleWrapper){
                                return getComponent((RuleWrapper) value, selected);
                        } else if(value instanceof ILegendPanel){
                                return getComponent((ILegendPanel) value, selected);
                        }
                        return new JLabel("root");
                }

                private Component getComponent(ILegendPanel legend, boolean selected) {
                        JLabel lab = new JLabel(legend.getLegend().getName());
                        lab.setForeground(selected ? SELECTED : DESELECTED);
                        lab.setBackground(Color.blue);
                        return lab;
                }

                private Component getComponent(RuleWrapper rw, boolean selected) {
                        String s = rw.getRule().getName();
                        if(s == null || s.isEmpty()){
                                s = "Unknown";
                        }
                        JLabel lab = new JLabel(s);
                        lab.setForeground(selected ? SELECTED : DESELECTED);
                        lab.setBackground(Color.blue);
                        return lab;
                }

                private Component getComponent(StyleWrapper sw, boolean selected) {
                        String s = sw.getStyle().getName();
                        if(s == null || s.isEmpty()){
                                s = "Unknown";
                        }
                        JLabel lab = new JLabel(s);
                        lab.setForeground(selected ? SELECTED : DESELECTED);
                        lab.setBackground(Color.blue);
                        return lab;
                }

        }
}
