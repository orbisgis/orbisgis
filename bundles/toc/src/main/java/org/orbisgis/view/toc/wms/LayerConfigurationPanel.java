/**
* OrbisGIS is a GIS application dedicated to scientific spatial simulation.
* This cross-platform GIS is developed at French IRSTV institute and is able to
* manipulate and create vector and raster spatial information.
*
* OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
* SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
*
* Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.wms;

import com.vividsolutions.wms.MapLayer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.orbisgis.sif.CRFlowLayout;
import org.orbisgis.sif.CarriageReturn;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.CustomButton;
import org.orbisgis.view.components.button.JButtonTextField;
import org.orbisgis.view.components.resourceTree.FilterTreeModelDecorator;
import org.orbisgis.view.toc.icons.TocIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 *
 * @author Erwan Bocher
 */
public class LayerConfigurationPanel extends JPanel implements UIPanel {

    private static final I18n I18N = I18nFactory.getI18n(LayerConfigurationPanel.class);
    private static final String ALL_LEFT = "ALL_LEFT";
    private static final String CURRENT_LEFT = "CURRENT_LEFT";
    private static final String CURRENT_RIGHT = "CURRENT_RIGHT";
    private TreeModel optionTreeModel;
    private JButton btnCurrentRight;
    private CustomButton btnCurrentLeft;
    private CustomButton btnAllLeft;
    private JTree treeOption;
    private JList lstSelection;
    private ActionListener actionListener;
    private DefaultListModel listModel;
    private MapLayer client;
    private JButtonTextField txtFilter;
    private final SRSPanel srsPanel;

    /**
     * The layerConfigurationPanel display the list of layers available in a
     * WMSServer. The user can select a set of layers to add them into the
     * geocatalog.
     */
    public LayerConfigurationPanel(SRSPanel srsPanel) {
        this.srsPanel = srsPanel;
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

    /**
     * A method to create a button with its action command
     *
     * @param iconName
     * @param actionCommand
     * @return
     */
    private CustomButton createButton(String iconName, String actionCommand) {
        CustomButton button = new CustomButton(TocIcon.getIcon(iconName));
        button.setActionCommand(actionCommand);
        button.addActionListener(actionListener);
        return button;
    }

    /**
     * A list to manage all selected layers.
     *
     * @return
     */
    private Component getSelectionList() {
        lstSelection = new JList();
        lstSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listModel = new DefaultListModel();
        lstSelection.setModel(listModel);
        lstSelection.setCellRenderer(new WMSLayerListRenderer(lstSelection));
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

    /**
     * Create to the add and remove buttons used to select or unselect a list of
     * layers.
     *
     * @return
     */
    private Component getAddRemoveButtons() {
        JPanel ret = new JPanel();
        ret.setLayout(new CRFlowLayout());
        btnCurrentRight = createButton("go-next", CURRENT_RIGHT);
        btnCurrentLeft = createButton("go-previous", CURRENT_LEFT);
        btnAllLeft = createButton("edit-clear", ALL_LEFT);
        ret.add(btnCurrentRight);
        ret.add(new CarriageReturn());
        ret.add(btnCurrentLeft);
        ret.add(new CarriageReturn());
        ret.add(btnAllLeft);
        return ret;
    }

    /**
     * Create a tree to list and browse all layers avalaible on the server.
     *
     * @return
     */
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
        treeOption.setCellRenderer(new WMSLayerTreeRenderer(treeOption));
        JPanel panel = new JPanel(new BorderLayout());
        JScrollPane ret = new JScrollPane(treeOption);

        panel.add(ret, BorderLayout.CENTER);
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
        panel.add(txtFilter, BorderLayout.NORTH);
        panel.setPreferredSize(new Dimension(1, 1));
        return panel;
    }

    /**
     * Enable the buttons to move the layers from the list of layers.
     */
    private void enableDisableButtons() {
        if (treeOption != null) {
            btnCurrentRight.setEnabled(treeOption.getSelectionCount() > 0);
            int selectedListIndex = lstSelection.getSelectedIndex();
            btnCurrentLeft.setEnabled(selectedListIndex != -1);
        }

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
    public String getTitle() {
        return I18N.tr("Choose a list of layers");
    }

    /**
     * Create the user interface.
     *     
* @return
     */
    public void initialize() {
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
    }

    @Override
    public String validateInput() {
        if (getSelectedLayers().length == 0) {
            return I18N.tr("At least a layer must be selected");
        }
        srsPanel.createSRSList(client);
        return null;
    }

    /**
     * Return the list of selected layers
     *     
* @return
     */
    public Object[] getSelectedLayers() {
        Object[] ret = new Object[listModel.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = listModel.elementAt(i);
        }
        return ret;
    }

    /**
     * The MapLayer used to obtain all informations from the server.
     *
     * @param client
     */
    public void setClient(MapLayer client) {
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
     *     
* @param tree
     */
    public void expandAll(JTree tree) {
        for (int row = 0; row < tree.getRowCount(); row++) {
            tree.expandRow(row);
        }
    }

    /**
     * Return the current WMS client
     *
     * @return
     */
    public MapLayer getMapLayer() {
        return client;
    }
}