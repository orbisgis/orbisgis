/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

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
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.orbisgis.core.sif.UIFactory;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.sif.multiInputPanel.StringType;
import org.orbisgis.core.ui.components.sif.RadioButtonPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.LegendPicker;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.legend.LegendTreeModel;
import org.orbisgis.core.ui.editorViews.toc.wrapper.RuleWrapper;
import org.orbisgis.core.ui.editorViews.toc.wrapper.StyleWrapper;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.legend.Legend;
import org.orbisgis.utils.I18N;

/**
 * This class is a panel that embeds a JTree (representing the legend structure)
 * and some buttons that can be used to manage it.
 * @author alexis
 */
public class LegendTree extends JPanel {

        private JTree tree;
        private LegendsPanel legendsPanel;

	private JToolBar toolBar;
	private JButton jButtonMenuAdd;
	private JButton jButtonMenuDel;
	private JButton jButtonMenuDown;
	private JButton jButtonMenuRename;
	private JButton jButtonMenuUp;

        public LegendTree(final LegendsPanel legendsPan){
                legendsPanel = legendsPan;

                StyleWrapper style = legendsPanel.getStyleWrapper();
                //We create our tree
                tree = new JTree();
                tree.setRootVisible(false);
                LegendTreeModel ltm = new LegendTreeModel(tree, style);
                tree.setModel(ltm);
                LegendCellRenderer lcr = new LegendCellRenderer();
                tree.setCellRenderer(lcr);
                TreeSelectionListener tsl = EventHandler.create(TreeSelectionListener.class, this, "refreshIcons");
                tree.addTreeSelectionListener(tsl);
                initToolBar();
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
                if(select instanceof Legend){
                        Legend leg = (Legend) select;
                        RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length -2];
                        rw.remove(leg);
                        refresh();
                } else if(select instanceof RuleWrapper){
                        RuleWrapper rw = (RuleWrapper) select;
                        StyleWrapper sw = (StyleWrapper) tp.getPath()[tp.getPath().length -2];
                        sw.remove(rw);
                        refresh();
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
                refresh();
        }

        /**
         * Move the currently selected element (if any) up in its structure. If
         * it is a legend, it is moved in the underlying RuleWrapper. If it is
         * a RuleWrapper, it is moved in the underlying StyleWrapper.
         */
        public void moveSelectedElementUp(){
                TreePath tp = tree.getSelectionPath();
                Object select = tp.getLastPathComponent();
                if(select instanceof RuleWrapper){
                        RuleWrapper rw = (RuleWrapper) select;
                        StyleWrapper sw = legendsPanel.getStyleWrapper();
                        int i = sw.indexOf(rw);
                        sw.moveRuleWrapperUp(i);
                        refresh();
                } else if(select instanceof Legend){
                        RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length -2];
                        Legend leg = (Legend) select;
                        int i = rw.indexOf(leg);
                        rw.moveLegendUp(i);
                        refresh();
                }
        }

        /**
         * Move the currently selected element (if any) down in its structure. If
         * it is a legend, it is moved in the underlying RuleWrapper. If it is
         * a RuleWrapper, it is moved in the underlying StyleWrapper.
         */
        public void moveSelectedElementDown(){
                TreePath tp = tree.getSelectionPath();
                Object select = tp.getLastPathComponent();
                if(select instanceof RuleWrapper){
                        RuleWrapper rw = (RuleWrapper) select;
                        TreeModel tm = tree.getModel();
                        StyleWrapper sw = legendsPanel.getStyleWrapper();
                        int i = tm.getIndexOfChild(sw, rw);
                        sw.moveRuleWrapperDown(i);
                        refresh();
                } else if(select instanceof Legend){
                        RuleWrapper rw = (RuleWrapper) tp.getPath()[tp.getPath().length -2];
                        Legend leg = (Legend) select;
                        int i = rw.indexOf(leg);
                        rw.moveLegendDown(i);
                        refresh();
                }
        }

        public void renameElement(ActionEvent evt){
                throw new UnsupportedOperationException();
        }

        /**
         * Get the currently selected Legend, if any. If we find it, we return it.
         * Otherwise, we return null.
         * @return
         */
        public Legend getSelectedLegend(){
                TreePath tp = tree.getSelectionPath();
                if(tp!=null){
                        Object last = tp.getLastPathComponent();
                        if(last instanceof Legend){
                                return (Legend) last;
                        }
                }
                return null;
        }

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
                        } else if(last instanceof Legend){
                                RuleWrapper rw = getSelectedRule();
                                index = rw.indexOf((Legend) last);
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

        void refresh() {
                refreshIcons();
                refreshModel();
        }

        private void initToolBar(){
                toolBar = new JToolBar();
                toolBar.setFloatable(false);
        }

        /**
         * Initialize all the buttons that can be used to manage the tree
         * content.
         */
        private void initButtons(){

		jButtonMenuUp = new JButton();
		jButtonMenuUp.setIcon(OrbisGISIcon.GO_UP);
		jButtonMenuUp.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.up"));
                ActionListener alu = EventHandler.create(ActionListener.class, this, "moveSelectedElementUp");
                jButtonMenuUp.addActionListener(alu);
		toolBar.add(jButtonMenuUp);

		jButtonMenuDown = new JButton();
		jButtonMenuDown.setIcon(OrbisGISIcon.GO_DOWN);
		jButtonMenuDown.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.down"));
                ActionListener ald = EventHandler.create(ActionListener.class, this, "moveSelectedElementDown");
                jButtonMenuDown.addActionListener(ald);
		toolBar.add(jButtonMenuDown);

		jButtonMenuAdd = new JButton();
		jButtonMenuAdd.setIcon(OrbisGISIcon.PICTURE_ADD);
		jButtonMenuAdd.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.add"));
                ActionListener aladd = EventHandler.create(ActionListener.class, this, "addElement");
		jButtonMenuAdd.addActionListener(aladd);
		toolBar.add(jButtonMenuAdd);

		jButtonMenuDel = new JButton();
		jButtonMenuDel.setIcon(OrbisGISIcon.PICTURE_DEL);
		jButtonMenuDel.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.delete"));
                ActionListener alrem = EventHandler.create(ActionListener.class, this, "removeSelectedElement");
		jButtonMenuDel.addActionListener(alrem);
		toolBar.add(jButtonMenuDel);

		jButtonMenuRename = new JButton();
		jButtonMenuRename.setIcon(OrbisGISIcon.PICTURE_EDI);
		jButtonMenuRename.setToolTipText(I18N.getString("orbisgis.org.orbisgis.ui.toc.legendList.rename"));
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
                        Legend leg = ((ILegendPanel) legendPicker.getSelected()).getLegend();
                        //We retrieve the rw where we will add it.
                        RuleWrapper currentrw = getSelectedRule();
                        if(currentrw == null ){
                                if(sw.getSize() == 0){
                                        addRule();
                                }
                                currentrw = sw.getRuleWrapper(sw.getSize()-1);
                        }
                        //We retrieve the index where to put it.
                        Legend sl = getSelectedLegend();
                        int pos;
                        if(sl != null){
                                //We want to insert it after the currently selected Legend.
                                pos = currentrw.indexOf(sl)+1;
                        } else {
                                pos = currentrw.getSize();
                        }
                        currentrw.addLegend(pos, leg);
                }
        }

        private void addRule(){
                //We must add it just after the currently selected Rule.
                //Let's find which one it is. If there is none, we add it at the
                //end of the list.
                MultiInputPanel mip = new MultiInputPanel("LegendTreeMip", "Choose a name for your rule", false);
                mip.addInput("RuleName", "Name of the Rule : ", new StringType(10));
                mip.addValidationExpression("RuleName IS NOT NULL AND RuleName != ''", "Enter a name.");
                if(UIFactory.showDialog(mip)){
                        String s = mip.getValues()[0];
                        StyleWrapper sw = legendsPanel.getStyleWrapper();
                        RuleWrapper rw = getSelectedRule();
                        int pos;
                        if(rw != null){
                                pos=sw.indexOf(rw)+1;
                        } else {
                                pos=sw.getSize();
                        }
                        sw.addRuleWrapper(pos, new RuleWrapper(s));
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
                        if(value instanceof RuleWrapper){
                                return getComponent((RuleWrapper) value, selected);
                        } else if(value instanceof Legend){
                                return getComponent((Legend) value, selected);
                        }
                        return new JLabel("root");
                }

                private Component getComponent(Legend legend, boolean selected) {
                        JLabel lab = new JLabel(legend.getName());
                        lab.setForeground(selected ? SELECTED : DESELECTED);
                        return lab;
                }

                private Component getComponent(RuleWrapper rw, boolean selected) {
                        String s = rw.getRule().getName();
                        if(s == null || s.isEmpty()){
                                s = "Unknown";
                        }
                        JLabel lab = new JLabel(s);
                        lab.setForeground(selected ? SELECTED : DESELECTED);
                        return lab;
                }

        }
}
