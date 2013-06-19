/**
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
package org.orbisgis.core.renderer.se;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.RuleType;
import net.opengis.se._2_0.core.StyleType;
import net.opengis.se._2_0.core.VersionType;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;

/**
 * Usable representation of SE styles. This is the upper node of the symbology
 * encoding implementation. It offers validation and edition mechanisms as well
 * as the ability to render maps from a SE style.
 * @author Maxence Laurent
 * @author Alexis Guéganno
 */
public final class Style extends AbstractSymbolizerNode {

    public static final String PROP_VISIBLE = "visible";
    private static final String DEFAULT_NAME = "Unnamed Style";
    private static final Logger LOGGER = Logger.getLogger(Style.class);
    private String name;
    private ArrayList<Rule> rules;
    private ILayer layer;
    private boolean visible = true;

    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);    
    
    /**
     * Create a new {@code Style} associated to the given {@code ILayer}. If the
     * given boolean is tru, a default {@code Rule} will be added to the Style.
     * If not, the {@code Style} will be let empty.
     * @param layer
     * @param addDefaultRule
     */
    public Style(ILayer layer, boolean addDefaultRule) {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        name = DEFAULT_NAME;
        if (addDefaultRule) {
            this.addRule(new Rule(layer));
        }
    }

    /**
     * Build a new {@code Style} from the given se file and associated to the
     * given {@code ILayer}.
     * @param layer
     * @param seFile
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     * If the SE file can't be read or is not valid against the XML schemas.
     */
    public Style(ILayer layer, String seFile) throws InvalidStyle {
        rules = new ArrayList<Rule>();
        this.layer = layer;

        try {

            Unmarshaller u = Services.JAXBCONTEXT.createUnmarshaller();


            //Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);

            JAXBElement<StyleType> fts = (JAXBElement<StyleType>) u.unmarshal(
                    new FileInputStream(seFile));

            StringBuilder errors = new StringBuilder();
            for (ValidationEvent event : validationCollector.getEvents()) {
                String msg = event.getMessage();
                ValidationEventLocator locator = event.getLocator();
                int line = locator.getLineNumber();
                int column = locator.getColumnNumber();
                errors.append("Error at line ");
                errors.append(line);
                errors.append(" column ");
                errors.append(column);
                errors.append(" (");
                errors.append(msg);
                errors.append(")\n");
            }

            if (errors.length() == 0) {
                this.setFromJAXB(fts);
            } else {
                throw new SeExceptions.InvalidStyle(errors.toString());
            }

        } catch (Exception ex) {
            throw new SeExceptions.InvalidStyle("Error while loading the style (" + seFile + "): " + ex);
        }

    }

    /**
     * Build a new {@code Style} associated to the given {@code ILayer} from the
     * given {@code JAXBElement<StyleType>}.
     * @param ftst
     * @param layer
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    public Style(JAXBElement<StyleType> ftst, ILayer layer) throws InvalidStyle {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        this.setFromJAXB(ftst);
    }

    /**
     * Build a new {@code Style} associated to the given {@code ILayer} from the
     * given {@code StyleType}.
     * @param fts
     * @param layer
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    public Style(StyleType fts, ILayer layer) throws InvalidStyle {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        this.setFromJAXBType(fts);
    }

    private void setFromJAXB(JAXBElement<StyleType> ftst) throws InvalidStyle {
        StyleType fts = ftst.getValue();
        this.setFromJAXBType(fts);
    }

    private void setFromJAXBType(StyleType fts) throws InvalidStyle {
        if (fts.getName() != null) {
            this.name = fts.getName();
        } else {
            name = DEFAULT_NAME;
        }

        if (fts.getRule() != null) {
            for (RuleType rt : fts.getRule()) {
                this.addRule(new Rule(rt, this.layer));
            }
        }
    }

    /**
     *  This method copies all rules from given style and merge them within the current
     * style. Resulting style is done by stacking new rules over rules from current style.
     * (i.e. symbolizer level of new style > level from current one)
     *
     * This may alter the behaviour of ElseRules !
     * @todo let the layer have several style ?
     *
     * @param style
     */
    public void merge(Style style) {
        int offset = findBiggestLevel();

        for (Rule r : style.getRules()) {
            this.addRule(r);
            for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
                s.setLevel(s.getLevel() + offset);
            }
        }
    }

    private int findBiggestLevel() {
        int level = 0;

        for (Rule r : rules) {
            for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
                level = Math.max(level, s.getLevel());
            }
        }
        return level;
    }

    /**
     * This method remove everything in this feature type style
     */
    public void clear() {
        this.rules.clear();
    }

    /**
     * Export this {@code Style} to the given SE file, in XML format.
     * @param seFile
     */
    public void export(String seFile) {
        try {
            JAXBContext jaxbContext = Services.JAXBCONTEXT;
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(getJAXBElement(), new FileOutputStream(seFile));
        } catch (FileNotFoundException ex) {
            LOGGER.error("Can't find the file "+seFile, ex);
        } catch (JAXBException ex) {
            LOGGER.error("Can't export your style into "+seFile+". May there be"
                    + "some error in it ?", ex);
        }
    }

    /**
     * Gets a JAXB representation of this {@code Style}.
     * @return
     */
    public JAXBElement<StyleType> getJAXBElement() {
        StyleType ftst = new StyleType();

        if (this.name != null) {
            ftst.setName(this.name);
        }

        ftst.setVersion(VersionType.VALUE_1); // TODO 

        List<RuleType> ruleTypes = ftst.getRule();
        for (Rule r : rules) {
            ruleTypes.add(r.getJAXBType());
        }

        ObjectFactory of = new ObjectFactory();

        return of.createStyle(ftst);

    }

    /**
     * Return all symbolizers from rules with a filter but not those from
     * a ElseFilter (i.e. fallback) rule
     *
     * @param mt
     * @param layerSymbolizers
     * @param overlaySymbolizers
     *
     * @param rules
     * @param fallbackRules
     * @todo take into account domain constraint
     */
    public void getSymbolizers(MapTransform mt,
            List<Symbolizer> layerSymbolizers,
            //ArrayList<Symbolizer> overlaySymbolizers,
            List<Rule> rules,
            List<Rule> fallbackRules) {
        if(visible){
            for (Rule r : this.rules) {
            // Only process visible rules with valid domain
                if (r.isDomainAllowed(mt)) {
                    // Split standard rules and elseFilter rules
                    if (!r.isFallbackRule()) {
                        rules.add(r);
                    } else {
                        fallbackRules.add(r);
                    }

                    for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
                        // Extract TextSymbolizer into specific set =>
                        // Label are always drawn on top
                        //if (s instanceof TextSymbolizer) {
                        //overlaySymbolizers.add(s);
                        //} else {
                        layerSymbolizers.add(s);
                        //}
                    }
                }
            }
        }
    }

    public void resetSymbolizerLevels() {
        int level = 1;

        for (Rule r : rules) {
            for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
                if (s instanceof TextSymbolizer) {
                    s.setLevel(Integer.MAX_VALUE);
                } else {
                    s.setLevel(level);
                    level++;
                }
            }
        }
    }

    /**
     * Gets the {@code Layer} associated to this {@code Style}.
     * @return
     */
    public ILayer getLayer() {
        return layer;
    }

    /**
     * Sets the {@code Layer} associated to this {@code Style}.
     * @param layer
     */
    public void setLayer(ILayer layer) {
        this.layer = layer;
    }

    @Override
    public SymbolizerNode getParent() {
        return null;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update() {
    }

    /**
     * Gets the name of this Style.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
    * Sets the name of this Style.
    * @param name
    */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the list of {@link Rule} contained in this Style.
     * @return
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Moves the ith {@link Rule} to position i-1 in the list of rules.
     * @param i
     * @return
     */
    public boolean moveRuleUp(int i) {
        try {
            if (i > 0) {
                Rule r = rules.remove(i);
                rules.add(i - 1, r);
                return true;
            }
        } catch (IndexOutOfBoundsException ex) {
        }
        return false;
    }

    /**
     * Moves the ith {@link Rule} to position i+1 in the list of rules.
     * @param i
     * @return
     */
    public boolean moveRuleDown(int i) {
        try {
            if (i < rules.size() - 1) {
                Rule r = rules.remove(i);
                rules.add(i + 1, r);
                return true;
            }

        } catch (IndexOutOfBoundsException ex) {
        }
        return false;
    }

    /**
     * Add a {@link Rule} to this {@code Style}.
     * @param r
     */
    public void addRule(Rule r) {
        if (r != null) {
            r.setParent(this);
            rules.add(r);
        }
    }

    /**
     * Add a {@link Rule} to this {@code Style} at position {@code index}.
     * @param index
     * @param r
     */
    public void addRule(int index, Rule r) {
        if (r != null) {
            r.setParent(this);
            rules.add(index, r);
        }
    }

    /**
     * Delete the ith {@link Rule} from this {@code Style}.
     * @param i
     * @return
     */
    public boolean deleteRule(int i) {
        try {
            rules.remove(i);
            return true;
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    @Override
    public List<SymbolizerNode> getChildren() {
            List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
            ls.addAll(rules);
            return ls;
    }

    /**
     *
     * @return
     * True if the Rule is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * If set to true, the rule is visible.
     * @param visible
     */
    public void setVisible(boolean visible) {
        boolean oldValue = this.visible;
        this.visible = visible;
        propertyChangeSupport.firePropertyChange(PROP_VISIBLE, oldValue, visible);
    }
    

    /**
    * Add a property-change listener for all properties.
    * The listener is called for all properties.
    * @param listener The PropertyChangeListener instance
    * @note Use EventHandler.create to build the PropertyChangeListener instance
    */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
    
    /**
    * Add a property-change listener for a specific property.
    * The listener is called only when there is a change to 
    * the specified property.
    * @param prop The static property name PROP_..
    * @param listener The PropertyChangeListener instance
    * @note Use EventHandler.create to build the PropertyChangeListener instance
    */
    public void addPropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(prop, listener);
    }
    
    /**
    * Remove the specified listener from the list
    * @param listener The listener instance
    */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
    * Remove the specified listener for a specified property from the list
    * @param prop The static property name PROP_..
    * @param listener The listener instance
    */
    public void removePropertyChangeListener(String prop,PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(prop,listener);
    }    
}
