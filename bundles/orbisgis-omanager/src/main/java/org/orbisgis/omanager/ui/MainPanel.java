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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Dialog that handle bundles.
 * @author Nicolas Fortin
 */
public class MainPanel extends JDialog {
    private static final Dimension DEFAULT_DIMENSION = new Dimension(800,480);
    private static final Dimension MINIMUM_BUNDLE_LIST_DIMENSION = new Dimension(100,50);
    private static final Dimension MINIMUM_BUNDLE_DESCRIPTION_DIMENSION = new Dimension(250,50);
    private static final I18n I18N = I18nFactory.getI18n(MainPanel.class);
    private static final Logger LOGGER = Logger.getLogger("gui."+MainPanel.class);
    private static final int BORDER_PIXEL_GAP = 2;
    private static final int PROPERTY_TEXT_SIZE_INCREMENT = 3;
    private static final int PROPERTY_TITLE_SIZE_INCREMENT = 4;

    // Bundle Category filter
    private JList bundleCategory = new JList(new String[] {"All","DataBase","Network","SQL Functions"});
    private JTextPane bundleDetails = new JTextPane();
    private JList bundleList = new JList();
    private JPanel bundleActions = new JPanel();
    private BundleListModel bundleListModel;
    private JPanel bundleDetailsAndActions = new JPanel(new BorderLayout());
    private JSplitPane splitPane;
    private ActionBundleFactory actionFactory;
    private BundleDetailsTransformer bundleHeader = new BundleDetailsTransformer();
    private RepositoryAdminTracker repositoryAdminTracker;
    /**
     * Constructor of the main plugin panel
     * @param frame MainFrame, in order to place this dialog and release resource automatically.
     * @param bundleContext Bundle context instance in order to manage them.
     */
    public MainPanel(Frame frame,BundleContext bundleContext) {
        super(frame);
        actionFactory = new ActionBundleFactory(bundleContext);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        // Main Panel (South button, center Split Pane)
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        // Buttons on south of main panel
        JPanel southButtons = new JPanel();
        southButtons.setLayout(new BoxLayout(southButtons, BoxLayout.X_AXIS));
        addSouthButtons(southButtons);
        contentPane.add(southButtons,BorderLayout.SOUTH);
        // Right Side of Split Panel, Bundle Description and button action on selected bundle
        bundleActions.setLayout(new BoxLayout(bundleActions,BoxLayout.X_AXIS));
        //bundleDetails.setPreferredSize(DEFAULT_DETAILS_DIMENSION);
        bundleDetails.setEditable(false);
        bundleDetails.setMinimumSize(MINIMUM_BUNDLE_DESCRIPTION_DIMENSION);
        bundleDetailsAndActions.add(new JScrollPane(bundleDetails),BorderLayout.CENTER);
        bundleDetailsAndActions.add(bundleActions,BorderLayout.SOUTH);
        // Left Side of Split Panel (Filters north, Categories west, bundles center)
        JPanel leftOfSplitGroup = new JPanel(new BorderLayout(BORDER_PIXEL_GAP,BORDER_PIXEL_GAP));
        bundleList.setMinimumSize(MINIMUM_BUNDLE_LIST_DIMENSION);
        leftOfSplitGroup.add(new JScrollPane(bundleCategory,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.WEST);

        leftOfSplitGroup.add(createRadioButtons(), BorderLayout.NORTH);
        leftOfSplitGroup.add(new JScrollPane(bundleList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),BorderLayout.CENTER);

        setDefaultDetailsMessage();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftOfSplitGroup,bundleDetailsAndActions);
        contentPane.add(splitPane);
        setSize(DEFAULT_DIMENSION);
        setTitle(I18N.tr("Plug-ins manager"));
        repositoryAdminTracker = new RepositoryAdminTracker(bundleContext);
        bundleListModel =  new BundleListModel(bundleContext);
        bundleList.setModel(bundleListModel);
        bundleListModel.install();
        bundleList.setCellRenderer(new BundleListRenderer(bundleList));
        bundleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bundleList.addListSelectionListener(EventHandler.create(ListSelectionListener.class,this,"onBundleSelectionChange",""));
        bundleList.getModel().addListDataListener(EventHandler.create(ListDataListener.class,this,"onListUpdate"));
    }

    /**
     * The user select another plug-in in the list.
     * @param e Event object
     */
    public void onBundleSelectionChange(ListSelectionEvent e) {
        if(!e.getValueIsAdjusting()) {
            Object selected = bundleList.getSelectedValue();
            if(selected instanceof BundleItem) {
                BundleItem selectedItem = (BundleItem)selected;
                setBundleDetailsAndActions(selectedItem);
            } else {
                setDefaultDetailsMessage();
            }
        }
    }

    /**
     * The Data Model of the Bundle list has been updated.
     */
    public void onListUpdate() {
        Object selected = bundleList.getSelectedValue();
        if(selected instanceof BundleItem) {
            final BundleItem selectedItem = (BundleItem)selected;
            if(!SwingUtilities.isEventDispatchThread()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        setBundleDetailsAndActions(selectedItem);
                    }
                });
            } else {
                setBundleDetailsAndActions(selectedItem);
            }
        } else {
            setDefaultDetailsMessage();
        }
    }
    private void addSouthButtons(JPanel southButtons) {
        JButton repositoryUrls = new JButton(I18N.tr("Manage repositories"));
        repositoryUrls.setToolTipText(I18N.tr("Add/Remove remote bundle repositories."));
        repositoryUrls.addActionListener(EventHandler.create(ActionListener.class,this,"onManageBundleRepositories"));
        southButtons.add(repositoryUrls);

        JButton refreshRepositories = new JButton(I18N.tr("Refresh"));
        refreshRepositories.setToolTipText(I18N.tr("Reload the list of plug-ins from the Internet."));
        refreshRepositories.addActionListener(EventHandler.create(ActionListener.class,this,"onReloadPlugins"));
        southButtons.add(refreshRepositories);
    }
    /**
     * Message on bundle details message frame when no bundle is selected, and remove all actions.
     */
    private void setDefaultDetailsMessage() {
        bundleActions.removeAll();
        bundleDetailsAndActions.setVisible(false);
    }
    private void addDescriptionItem(String propertyKey,String propertyValue ,Document document) {
        addDescriptionItem(propertyKey, propertyValue, document, PROPERTY_TEXT_SIZE_INCREMENT);
    }
    private void addDescriptionItem(String propertyKey,String propertyValue ,Document document, int keySize) {
        try {
            SimpleAttributeSet sc = new SimpleAttributeSet();
            sc.addAttribute(StyleConstants.CharacterConstants.Bold, Boolean.TRUE);
            int standardSize = StyleConstants.CharacterConstants.getFontSize(sc);
            sc.addAttribute(StyleConstants.CharacterConstants.Size, standardSize + keySize);
            document.insertString(document.getLength(), propertyKey, sc);
            if(!propertyValue.isEmpty()) {
                document.insertString(document.getLength()," : "+propertyValue+"\n\n",new SimpleAttributeSet());
            } else {
                document.insertString(document.getLength(),"\n"+propertyValue+"\n\n",new SimpleAttributeSet());
            }
        } catch (BadLocationException ex) {
            LOGGER.error(ex);
        }
    }
    private void addHeaderItem(String property,Map<String,String> headers, Document document) {
        String value = headers.get(property);
        if(value!=null) {
            addDescriptionItem(I18N.tr(property),value,document);
        }
    }

    private void setBundleDetailsAndActions(BundleItem selectedItem) {
        bundleActions.removeAll();
        // Set description
        bundleDetails.setText("");
        Document document = bundleDetails.getDocument();
        Map<String,String> itemDetails = selectedItem.getDetails();
        // Plugin Name
        // Title, Description, Version, Category, then other parameters
        addDescriptionItem(selectedItem.getPresentationName(),"",document,PROPERTY_TITLE_SIZE_INCREMENT);
        // Description
        addHeaderItem(Constants.BUNDLE_DESCRIPTION, itemDetails, document);
        // Version
        addHeaderItem(Constants.BUNDLE_VERSION, itemDetails, document);
        // Categories
        StringBuilder cat = new StringBuilder();
        for(String category : selectedItem.getBundleCategories()) {
            if(cat.length()>0) {
                cat.append(", ");
            }
            cat.append(I18N.tr(category.trim()));
        }
        if(cat.length()>0) {
            addDescriptionItem(I18N.tr(Constants.BUNDLE_CATEGORY),cat.toString(),document);
        }

        // Add other properties
        for(Map.Entry<String,String> entry : itemDetails.entrySet()) {
            String originalKey = entry.getKey();
            String key = bundleHeader.convert(originalKey);
            if(!key.isEmpty() && !entry.getValue().isEmpty()) {
                addDescriptionItem(key,entry.getValue(),document);
            }
        }
        bundleDetails.setCaretPosition(0); // Got to the beginning of the document
        // Set buttons
        List<Action> actions = actionFactory.create(selectedItem);
        for(Action action : actions) {
            JButton actionButton = new JButton(action);
            bundleActions.add(actionButton);
        }
        boolean hidden = !bundleDetailsAndActions.isVisible();
        if(hidden) {
            bundleDetailsAndActions.setVisible(true);
            splitPane.setDividerLocation(-1); // -1 to let swing compute preferred size
        } else {
            splitPane.updateUI(); // Without this instruction buttons are disabled on some L&F
        }
    }
    /**
     * User click on "All states" radio button
     */
    public void onRemoveStateFilter() {

    }
    /**
     * User click on "Installed" radio button
     */
    public void onFilterBundleInstall() {

    }
    /**
     * User click on "Update" radio button
     */
    public void onFilterBundleUpdate() {

    }
    /**
     * User click on an item on the category list.
     */
    public void onFilterByBundleCategory() {

    }
    /**
     * User click on "Refresh" button.
     */
    public void onReloadPlugins() {

    }

    /**
     * User want to manage repository urls.
     */
    public void onManageBundleRepositories() {

    }

    @Override
    public void dispose() {
        super.dispose();
        // Remove trackers and listeners

    }

    private void createRadioButton(String label,String toolTipText,boolean state,String methodName,ButtonGroup filterGroup,JPanel radioBar) {
        JRadioButton noStateFilter = new JRadioButton(label,state);
        noStateFilter.setToolTipText(toolTipText);
        noStateFilter.addActionListener(EventHandler.create(ActionListener.class, this, methodName));
        filterGroup.add(noStateFilter);
        radioBar.add(noStateFilter);
    }
    private JPanel createRadioButtons() {
        // Make main radio panel
        JPanel radioBar = new JPanel();
        radioBar.setLayout(new BoxLayout(radioBar,BoxLayout.X_AXIS));
        // Create radio buttons
        ButtonGroup filterGroup = new ButtonGroup();
        createRadioButton(I18N.tr("All state"),I18N.tr("Do not filter bundle by their state."),true,
                "onRemoveStateFilter",filterGroup,radioBar);
        createRadioButton(I18N.tr("Installed"),I18N.tr("Show only installed bundles."),false,"onFilterBundleInstall",
                filterGroup, radioBar);
        createRadioButton(I18N.tr("Update"), I18N.tr("Show only bundles where an update is available."), false,
                "onFilterBundleUpdate", filterGroup, radioBar);
        return radioBar;
    }
}
