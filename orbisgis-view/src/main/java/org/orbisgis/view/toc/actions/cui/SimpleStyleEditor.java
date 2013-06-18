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
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import org.gdms.data.types.Type;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.legend.Legend;
import org.orbisgis.legend.thematic.factory.LegendFactory;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.actions.cui.legend.ISELegendPanel;
import org.orbisgis.view.toc.actions.cui.legends.PnlProportionalLineSE;
import org.orbisgis.view.toc.actions.cui.legends.PnlProportionalPointSE;
import org.orbisgis.view.toc.actions.cui.legends.PnlRecodedArea;
import org.orbisgis.view.toc.actions.cui.legends.PnlRecodedLine;
import org.orbisgis.view.toc.actions.cui.legends.PnlRecodedPoint;
import org.orbisgis.view.toc.actions.cui.legends.PnlRule;
import org.orbisgis.view.toc.actions.cui.legends.PnlStyle;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniqueAreaSE;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniqueLineSE;
import org.orbisgis.view.toc.actions.cui.legends.PnlUniquePointSE;
import org.orbisgis.view.toc.wrapper.RuleWrapper;
import org.orbisgis.view.toc.wrapper.StyleWrapper;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This {@link JPanel} contains all the needed information to build a user
 * interface for editing legends.
 *
 * It is currently the dialog that is displayed when the user clicks on "Simple
 * style edition".
 *
 * @author Alexis Guéganno
 * @author Adam Gouge
 * @author others
 */
public class SimpleStyleEditor extends JPanel implements UIPanel, LegendContext {

    /**
     * Tree to display legends.
     */
    private LegendTree legendTree;
    // **********     GRAPHICS     **************
    /**
     * Used to switch between legend instances.
     */
    private CardLayout cardLayout;
    /**
     * Dialog panel.
     */
    private JPanel dialogContainer;
    // **********     INITIALIZATION VARIABLES     **************
    /**
     * Represents the current state of the map.
     */
    private MapTransform mt;
    /**
     * The type of the {@link DataSource} of the legend's layer currently being
     * modified.
     */
    private Type type;
    /**
     * The layer we are editing.
     */
    private ILayer layer;
    // **********     INITIALIZATION-LIKE VARIABLES     **********
    /**
     * {@link SimpleGeometryType}.
     */
    private int geometryType;
    /**
     * {@link StyleWrapper} for the {@link Style}s of the layer we are editing.
     */
    private StyleWrapper styleWrapper;
    /**
     * Inner list of available legends.
     *
     * Used to determine whether a given {@link Legend} can be edited or not.
     */
    private ILegendPanel[] availableLegends;
    // **********     IDS     **************
    /**
     * Id for the {@link CardLayout} panel to be shown when there is no legend.
     */
    private static final String NO_LEGEND_ID = "no-legend";
    /**
     * Id of the most recently added {@link CardLayout} panel.
     */
    private static String lastUID = "";
    // **********     OTHER     **************
    /**
     * Translator.
     */
    private static final I18n I18N = I18nFactory.getI18n(SimpleStyleEditor.class);

    /**
     * Constructor
     *
     * @param mt    Map transform
     * @param type  Layer geometry type
     * @param layer Layer
     * @param style Style
     */
    public SimpleStyleEditor(MapTransform mt,
                        Type type,
                        ILayer layer,
                        Style style) {

        // Initialize the first four paramters.
        this.mt = mt;
        this.type = type;
        this.geometryType = (type == null)
                ? SimpleGeometryType.ALL
                : SimpleGeometryType.getSimpleType(type);

        this.availableLegends = getAvailableLegendPanels();

        this.layer = layer;

        // Set the layout.
        setLayout(new GridBagLayout());

        // Initialize the dialog container.
        cardLayout = new CardLayout();
        dialogContainer = new JPanel(cardLayout);
        dialogContainer.setPreferredSize(new Dimension(600, 650));

        // Add the layer tag and the dialog panel to the EAST side.
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 1;
        c1.gridy = 0;
        c1.fill = GridBagConstraints.BOTH;
        c1.gridwidth = 2;
        c1.weightx = 1.0;
        c1.weighty = 0.01;
        add(getLayerTag(), c1);

        // Add the empty dialog and the dialog container.
        addEmptyDialog();
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 1;
        c2.gridy = 1;
        c2.fill = GridBagConstraints.BOTH;
        c2.gridwidth = 2;
        c2.weightx = 1.0;
        c2.weighty = 1.0;
        add(dialogContainer, c2);

        // Initialize all panels.
        styleWrapper = addAllPanels(style);

        // Initialize a new legend tree and add it to the WEST side.
        legendTree = new LegendTree(this);
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 0;
        c3.fill = GridBagConstraints.BOTH;
        c3.gridheight = 2;
        c3.weightx = 1.0 / 3;
        c3.weighty = 1.0;
        add(legendTree, c3);
    }

    /**
     * Creates an array of all available legend panels.
     *
     * @return Array of available legend panels
     */
    private ILegendPanel[] getAvailableLegendPanels() {

        ArrayList<ILegendPanel> legends = new ArrayList<ILegendPanel>();

        // UniqueLine
        ILegendPanel pnlUniqueLine = new PnlUniqueLineSE();
        pnlUniqueLine.initialize(this);
        legends.add(pnlUniqueLine);
        // UniquePoint
        ILegendPanel pnlUniquePoint = new PnlUniquePointSE();
        pnlUniquePoint.initialize(this);
        legends.add(pnlUniquePoint);
        // UniqueArea
        ILegendPanel pnlUniqueArea = new PnlUniqueAreaSE();
        pnlUniqueArea.initialize(this);
        legends.add(pnlUniqueArea);
        // ProportionalPoint
        ILegendPanel proportionalPoint = new PnlProportionalPointSE();
        proportionalPoint.initialize(this);
        legends.add(proportionalPoint);
        // ProportionalLine
        ILegendPanel proportionalLine = new PnlProportionalLineSE();
        proportionalLine.initialize(this);
        legends.add(proportionalLine);
        // Line Unique Value
        ILegendPanel uniqueLine = new PnlRecodedLine();
        uniqueLine.initialize(this);
        legends.add(uniqueLine);
        // Area Unique Value
        ILegendPanel uniqueArea = new PnlRecodedArea();
        uniqueArea.initialize(this);
        legends.add(uniqueArea);
        // Point Unique Value
        ILegendPanel uniquePoint = new PnlRecodedPoint();
        uniquePoint.initialize(this);
        legends.add(uniquePoint);

        return legends.toArray(new ILegendPanel[legends.size()]);
    }

    /**
     * Puts the layer tag and a separator in a new {@link JPanel}.
     *
     * @return The layer tag and a separator in a new {@link JPanel}
     */
    private JPanel getLayerTag() {
        // Add the layer tag and the dialog panel to the EAST side.
        JPanel right = new JPanel(new BorderLayout());
        // Add the layer tag.
        JLabel layerTag = new JLabel(
                "<html><b>"
                + I18N.tr("Editing layer")
                + "</b>: " + layer.getName());
        layerTag.setHorizontalAlignment(JLabel.CENTER);
        // TODO: Set the size a better way? This is to align with the
        // toolbar on the west side.
        Dimension size = new Dimension(layerTag.getWidth(), 22);
        layerTag.setMinimumSize(size);
        layerTag.setPreferredSize(size);
        right.add(layerTag, BorderLayout.NORTH);
        // Add a separator.
        JSeparator hRule = new JSeparator();
        hRule.setMinimumSize(hRule.getSize());
        right.add(hRule, BorderLayout.CENTER);
        return right;
    }

    /**
     * Adds the empty dialog to the card layout.
     */
    private void addEmptyDialog() {
        JPanel textHolder = new JPanel(new BorderLayout());
        JLabel text = new JLabel(I18N.tr("Add or select a legend."));
        text.setHorizontalAlignment(SwingConstants.CENTER);
        textHolder.add(text, BorderLayout.CENTER);
        dialogContainer.add(NO_LEGEND_ID, textHolder);
    }

    /**
     * Adds the style, rule and symbol panels for the given style and returns
     * the corresponding {@link StyleWrapper}.
     *
     * @param style Style
     *
     * @return The {@link StyleWrapper} corresponding to the style panel
     *
     * @see #addRuleAndSymbolPanels(org.orbisgis.core.renderer.se.Style)
     */
    private StyleWrapper addAllPanels(Style style) {
        // Get a style wrapper based on this style and the previous list
        // of rule wrappers constructed in addRuleAndSymbolPanels.
        StyleWrapper sw = new StyleWrapper(style,
                                           addRuleAndSymbolPanels(style));
        addStylePanel(sw);
        return sw;
    }

    /**
     * Adds the style panel attached to the given {@link StyleWrapper}.
     *
     * @param sw StyleWrapper
     */
    private void addStylePanel(StyleWrapper sw) {
        PnlStyle stylePanel = sw.getPanel();
        stylePanel.setId(createNewID());
        stylePanel.addPropertyChangeListener(
                EventHandler.create(PropertyChangeListener.class, this,
                                    "onNodeNameChange", ""));
        JScrollPane jsp = new JScrollPane(stylePanel.getComponent());
        dialogContainer.add(stylePanel.getId(), jsp);
    }

    /**
     * Adds the rule and symbol panels for the rules and symbols attached to the
     * given style and returns a list of the corresponding {@link RuleWrapper}s.
     *
     * @param style Style
     *
     * @return A list of {@link RuleWrapper}s corresponding to the newly added
     *         rule panels.
     *
     * @see #addSymbolPanels(org.orbisgis.core.renderer.se.Rule)
     */
    private List<RuleWrapper> addRuleAndSymbolPanels(Style style) {
        List<Rule> rules = style.getRules();
        List<RuleWrapper> ruleWrapperList = new LinkedList<RuleWrapper>();
        for (int i = 0; i < rules.size(); i++) {
            // Get the rule.
            Rule rule = rules.get(i);
            // Get a new RuleWrapper based on this rule and the list of
            // symbol panels constructed in addSymbolPanels.
            RuleWrapper ruleWrapper = new RuleWrapper(rule,
                                                      addSymbolPanels(rule));
            addRulePanel(ruleWrapper);
            // Add the rule wrapper panel to the list of rule wrapper panels.
            ruleWrapperList.add(ruleWrapper);
        }
        return ruleWrapperList;
    }

    /**
     * Adds the rule panel attached to the given {@link RuleWrapper}.
     *
     * @param ruleWrapper RuleWrapper
     */
    private void addRulePanel(RuleWrapper ruleWrapper) {
        // Get the panel associated to this RuleWrapper, set its id,
        // initialize it and add a listener for when its node name changes.
        PnlRule rulePanel = (PnlRule) ruleWrapper.getPanel();
        rulePanel.setId(createNewID());
        rulePanel.initialize(this);
        rulePanel.addPropertyChangeListener(
                EventHandler.create(PropertyChangeListener.class, this,
                                    "onNodeNameChange", ""));
        // Add the rule wrapper panel to the container after putting it in
        // a new JScrollPane.
        JScrollPane jsp = new JScrollPane(rulePanel.getComponent());
        dialogContainer.add(rulePanel.getId(), jsp);
    }

    /**
     * Adds the symbol panels for the legends (=symbols) attached to the given
     * rule and returns a list of the corresponding {@link ILegendPanel}s.
     *
     * @param rule Rule
     *
     * @return A list of {@link ILegendPanel}s corresponding to the newly added
     *         symbol panels.
     */
    private List<ILegendPanel> addSymbolPanels(Rule rule) {
        List<ILegendPanel> symbolPanelList = new LinkedList<ILegendPanel>();
        // For each symbol in this rule, add its symbol panel to the list of
        // symbol panels.
        for (Symbolizer symb : rule.getCompositeSymbolizer().
                getSymbolizerList()) {
            symbolPanelList.add(addSymbolPanel(symb));
        }
        return symbolPanelList;
    }

    /**
     * Adds the symbol panel attached to the given {@link Symbolizer} and
     * returns the panel.
     *
     * @param symb Symbolizer
     *
     * @return The newly generated symbol panel
     */
    // TODO: No property change listener on symbol panels?
    private ILegendPanel addSymbolPanel(Symbolizer symb) {
        // Get this symbolizer's panel and give it a new id.
        ILegendPanel symbPanel =
                associatePanel(LegendFactory.getLegend(symb));
        symbPanel.setId(createNewID());
        // Add the symbol panel to the container after putting it in a
        // new JScrollPane.
        JScrollPane jsp = new JScrollPane(symbPanel.getComponent());
        dialogContainer.add(symbPanel.getId(), jsp);
        return symbPanel;
    }

    /**
     * Associates a panel to the given legend. This panel is cloned from one of
     * the available panels.
     *
     * @param legend The legend for which we want a panel
     *
     * @return A newly cloned panel associated to the given legend, or a new
     *         {@link NoPanel} if none are available
     */
    // TODO: This finds the first available legend panel. Is it guaranteed
    // to be unique?
    public ILegendPanel associatePanel(Legend legend) {
        for (ILegendPanel avail : availableLegends) {
            // If the type of the given legend matches the type of 
            // an available legend panel ...
            if (legend.getLegendTypeId().equals(
                    avail.getLegend().getLegendTypeId())) {
                // Create a new instance of the available legend panel, 
                // initializing it with the LegendContext methods implemented
                // in this class.
                ILegendPanel ilp = (ILegendPanel) avail.newInstance();
                ilp.initialize(this);
                // Set the legend to be edited to the given legend
                ilp.setLegend(legend);
                // And return it
                return ilp;
            }
        }
        // If none were found, then return a new NoPanel.
        return new NoPanel(legend);
    }

    /**
     * Creates a new unique ID for retrieving panels in the card layout.
     *
     * @return A new unique ID
     */
    public static String createNewID() {
        String name = "gdms" + System.currentTimeMillis();
        while (name.equals(lastUID)) {
            name = "" + System.currentTimeMillis();
        }
        lastUID = name;
        return name;
    }

    /**
     * Retrieves the currently selected legend in the tree and shows the
     * corresponding dialog in the card layout; shows the empty panel if no
     * legend is selected.
     */
    protected void showDialogForCurrentlySelectedLegend() {
        ISELegendPanel selected = legendTree.getSelectedPanel();
        if (selected != null) {
            cardLayout.show(dialogContainer, selected.getId());
        } else {
            cardLayout.show(dialogContainer, NO_LEGEND_ID);
        }
    }

    // *******************     EventHandler methods     **********************
    /**
     * Updates the name of the selected element when it changes.
     *
     * @param pce The original event
     */
    public void onNodeNameChange(PropertyChangeEvent pce) {
        if (pce.getPropertyName().equals(PnlRule.NAME_PROPERTY)) {
            legendTree.selectedNameChanged();
        }
    }

    /**
     * Initialize the given panel with this as {@link LegendContext}, set its
     * id, add it to the container inside a {@link JScrollPane} and show its
     * dialog.
     *
     * @param panel The panel to initialize and show
     */
    // TODO: Should this method be moved to LegendTree?
    public void legendAdded(ISELegendPanel panel) {
        panel.initialize(this);
        panel.setId(createNewID());
        JScrollPane jsp = new JScrollPane(panel.getComponent());
        dialogContainer.add(panel.getId(), jsp);
        showDialogForCurrentlySelectedLegend();
    }

    /**
     * Remove the given panel from the card layout and refresh the display.
     *
     * @param panel Panel
     */
    public void legendRemoved(ISELegendPanel panel) {
        cardLayout.removeLayoutComponent(panel.getComponent());
        showDialogForCurrentlySelectedLegend();
    }

    // TODO: Find usages / Document.
    public void legendRenamed(int idx, String newName) {
        showDialogForCurrentlySelectedLegend();
    }

    // TODO: Find usages / Document.
    public void legendSelected() {
        showDialogForCurrentlySelectedLegend();
    }

    // *************************     Getters     *****************************
    /**
     * Gets the style wrapper.
     *
     * @return The style wrapper
     */
    public StyleWrapper getStyleWrapper() {
        return styleWrapper;
    }

    /**
     * Gets the available legends.
     *
     * @return The available legends
     */
    public ILegendPanel[] getAvailableLegends() {
        return availableLegends;
    }

    // ******************     LegendContext methods     **********************
    @Override
    public int getGeometryType() {
        return geometryType;
    }

    @Override
    public boolean isPoint() {
        return (geometryType & SimpleGeometryType.POINT) > 0;
    }

    @Override
    public boolean isLine() {
        return (geometryType & SimpleGeometryType.LINE) > 0;
    }

    @Override
    public boolean isPolygon() {
        return (geometryType & SimpleGeometryType.POLYGON) > 0;
    }

    @Override
    public ILayer getLayer() {
        return layer;
    }

    @Override
    public MapTransform getCurrentMapTransform() {
        return mt;
    }

    // **********************     UIPanel methods     ************************
    @Override
    public URL getIconURL() {
        return UIFactory.getDefaultIcon();
    }

    @Override
    public String getTitle() {
        // TODO: No usages. Why simple style edition?
        return I18N.tr("Simple Style Editor");
    }

    @Override
    public String validateInput() {
        if (!legendTree.hasLegend()) {
            return I18N.tr("You must create at least one legend");
        }
        List<String> errors = styleWrapper.validateInput();
        StringBuilder sb = new StringBuilder();
        for (String message : errors) {
            if (message != null && !message.isEmpty()) {
                sb.append(message);
                sb.append("\n");
            }
        }
        String err = sb.toString();
        if (err != null && !err.isEmpty()) {
            return err;
        }
        return null;
    }

    @Override
    public Component getComponent() {
        return this;
    }
}
