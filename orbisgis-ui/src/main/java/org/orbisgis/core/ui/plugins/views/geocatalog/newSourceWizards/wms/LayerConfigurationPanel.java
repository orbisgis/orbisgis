package org.orbisgis.core.ui.plugins.views.geocatalog.newSourceWizards.wms;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.gvsig.remoteClient.wms.WMSClient;

import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.components.resourceTree.FilterTreeModelDecorator;
import org.orbisgis.core.ui.components.text.JButtonTextField;
import org.orbisgis.core.ui.components.text.JTextFilter;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.utils.I18N;

public class LayerConfigurationPanel extends JPanel implements UIPanel {

        private static final String ALL_LEFT = "ALL_LEFT";
        private static final String CURRENT_LEFT = "CURRENT_LEFT";
        private static final String CURRENT_RIGHT = "CURRENT_RIGHT";
        private TreeModel optionTreeModel;
        private JButton btnCurrentRight;
        private JButton btnCurrentLeft;
        private JButton btnAllLeft;
        private JTree treeOption;
        private JList lstSelection;
        private ActionListener actionListener;
        private DefaultListModel listModel;
        private WMSClient client;
        private JButtonTextField txtFilter;

        public LayerConfigurationPanel() {

                actionListener = new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                                int selectedListIndex = lstSelection.getSelectedIndex();
                                if (CURRENT_RIGHT.equals(e.getActionCommand())) {
                                        TreePath[] selectedPaths = treeOption.getSelectionPaths();
                                        for (TreePath treePath : selectedPaths) {
                                                if (!listModel.contains(treePath.getLastPathComponent())) {
                                                        listModel.addElement(treePath.getLastPathComponent());
                                                }
                                        }
                                } else if (CURRENT_LEFT.equals(e.getActionCommand())) {
                                        listModel.remove(selectedListIndex);
                                } else if (ALL_LEFT.equals(e.getActionCommand())) {
                                        listModel.clear();
                                }
                        }
                };
        }

        private JButton createButton(String iconName, String actionCommand) {
                JButton button = new JButton(IconLoader.getIcon(iconName));
                button.setActionCommand(actionCommand);
                button.addActionListener(actionListener);
                return button;
        }

        private Component getSelectionList() {
                lstSelection = new JList();
                lstSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                listModel = new DefaultListModel();
                lstSelection.setModel(listModel);
                lstSelection.getSelectionModel().addListSelectionListener(
                        new ListSelectionListener() {

                                @Override
                                public void valueChanged(ListSelectionEvent e) {
                                        enableDisableButtons();
                                }
                        });
                JScrollPane ret = new JScrollPane(lstSelection);
                ret.setPreferredSize(new Dimension(1, 1));
                return ret;
        }

        private Component getAddRemoveButtons() {
                JPanel ret = new JPanel();
                ret.setLayout(new CRFlowLayout());
                btnCurrentRight = createButton("current_right.png", CURRENT_RIGHT);
                btnCurrentLeft = createButton("current_left.png", CURRENT_LEFT);
                btnAllLeft = createButton("all_left.png", ALL_LEFT);
                ret.add(btnCurrentRight);
                ret.add(new CarriageReturn());
                ret.add(btnCurrentLeft);
                ret.add(new CarriageReturn());
                ret.add(btnAllLeft);
                return ret;
        }

        private Component getOptionTree() {
                treeOption = new JTree();
                FilterTreeModelDecorator model = new FilterTreeModelDecorator(optionTreeModel, treeOption);
                treeOption.setModel(model);
                treeOption.getSelectionModel().addTreeSelectionListener(
                        new TreeSelectionListener() {

                                @Override
                                public void valueChanged(TreeSelectionEvent e) {
                                        enableDisableButtons();
                                }
                        });
                JPanel panel = new JPanel(new BorderLayout());
                JScrollPane ret = new JScrollPane(treeOption);

                panel.add(ret, BorderLayout.CENTER);
                txtFilter = new JButtonTextField();
                txtFilter.getDocument().addDocumentListener(new DocumentListener() {

                        public void removeUpdate(DocumentEvent e) {
                                doFilter();
                        }

                        public void insertUpdate(DocumentEvent e) {
                                doFilter();
                        }

                        public void changedUpdate(DocumentEvent e) {
                                doFilter();
                        }
                });
                panel.add(txtFilter, BorderLayout.NORTH);
                panel.setPreferredSize(new Dimension(1, 1));
                return panel;
        }

        private void enableDisableButtons() {
                btnCurrentRight.setEnabled(treeOption.getSelectionCount() > 0);
                int selectedListIndex = lstSelection.getSelectedIndex();
                btnCurrentLeft.setEnabled(selectedListIndex != -1);
        }

        @Override
        public Component getComponent() {
                return this;
        }

        @Override
        public URL getIconURL() {
                return null;
        }

        @Override
        public String getInfoText() {
                return I18N.getString("orbisgis.org.orbisgis.wms.serverLayers");
        }

        @Override
        public String getTitle() {
                return I18N.getString("orbisgis.org.orbisgis.wms.layerConfiguration");
        }

        @Override
        public String initialize() {

                if (null == optionTreeModel) {
                        optionTreeModel = new WMSLayerTreeModel(client);
                }

                this.removeAll();

                GridBagLayout gl = new GridBagLayout();
                this.setLayout(gl);


                // Option tree
                GridBagConstraints c = new GridBagConstraints();
                c.fill = GridBagConstraints.BOTH;
                c.gridx = 0;
                c.weightx = 1;
                c.weighty = 1;
                this.add(getOptionTree(), c);

                // add/remove buttons
                c.fill = GridBagConstraints.NONE;
                c.gridx = 1;
                c.weightx = 0.1;
                this.add(getAddRemoveButtons(), c);

                // selection list
                c.fill = GridBagConstraints.BOTH;
                c.gridx = 2;
                c.weightx = 1;
                this.add(getSelectionList(), c);

                // sort buttons
                c.fill = GridBagConstraints.NONE;
                c.gridx = 3;
                c.weightx = 0.1;

                enableDisableButtons();



                return null;
        }

        @Override
        public String postProcess() {
                return null;
        }

        @Override
        public String validateInput() {
                if (getSelectedLayers().length == 0) {
                        return I18N.getString("orbisgis.org.orbisgis.core.AtLeastAlayerMustBeSelected");
                }

                return null;
        }

        public Object[] getSelectedLayers() {
                Object[] ret = new Object[listModel.size()];
                for (int i = 0; i < ret.length; i++) {
                        ret[i] = listModel.elementAt(i);
                }
                return ret;
        }

        /**
         * @param client the client to set
         */
        public void setClient(WMSClient client) {
                this.client = client;
                optionTreeModel = new WMSLayerTreeModel(client);
        }

        /**
         * A method to filter the WMS layers by name
         */
        private void doFilter() {
                String filterText = txtFilter.getText();
                if (filterText.trim().length() == 0) {
                        optionTreeModel = new WMSLayerTreeModel(client);
                        FilterTreeModelDecorator model = new FilterTreeModelDecorator(optionTreeModel, treeOption);
                        treeOption.setModel(model);
                        expandAll(treeOption);
                } else {
                        FilterTreeModelDecorator model = (FilterTreeModelDecorator) treeOption.getModel();
                        model.filter(filterText);
                }
        }

        /**
         * Expand configuration tree
         * @param tree
         */
        public void expandAll(JTree tree) {
                for (int row = 0; row < tree.getRowCount(); row++) {
                        tree.expandRow(row);
                }
        }
}
