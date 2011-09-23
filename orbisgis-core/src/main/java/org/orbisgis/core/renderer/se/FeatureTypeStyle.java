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
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.renderer.se;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.util.ValidationEventCollector;
import net.opengis.se._2_0.core.FeatureTypeStyleType;
import net.opengis.se._2_0.core.ObjectFactory;
import net.opengis.se._2_0.core.RuleType;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;


import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 * {@code FeatureTypeStyle} is the entry point taht will define all the
 * symbology definitions that must be used to render a given layer. It embeds
 * a set of {@link Rule} instances, that must be applied to a given
 * {@link ILayer layer}.
 * @author maxence, alexis
 */
public final class FeatureTypeStyle implements SymbolizerNode {
        
    private String name;
    private ArrayList<Rule> rules;
    private ILayer layer;

    public FeatureTypeStyle(ILayer layer, boolean addDefaultRule) {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        if (addDefaultRule) {
            this.addRule(new Rule(layer));
        }
    }

    /**
     * Build a new {@code FeatureTypeStyle} instance. Rules are retrieved in
     * the SE file given in argument, and are applied to the given {@link
     * ILayer}.
     * @param layer
     * @param seFile
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    public FeatureTypeStyle(ILayer layer, String seFile) throws InvalidStyle {
        rules = new ArrayList<Rule>();
        this.layer = layer;

        JAXBContext jaxbContext;
        try {

            jaxbContext = JAXBContext.newInstance(FeatureTypeStyleType.class);

            Unmarshaller u = jaxbContext.createUnmarshaller();


            //Schema schema = u.getSchema();
            ValidationEventCollector validationCollector = new ValidationEventCollector();
            u.setEventHandler(validationCollector);

            JAXBElement<FeatureTypeStyleType> fts = (JAXBElement<FeatureTypeStyleType>) u.unmarshal(
                    new FileInputStream(seFile));

            StringBuffer errors = new StringBuffer();
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
            Logger.getLogger(FeatureTypeStyle.class.getName()).log(Level.SEVERE, "Error while loading style", ex);
            throw new SeExceptions.InvalidStyle("Error while loading the style (" + seFile + "): " + ex);
        }

    }

    /**
     * Build a new {@code FeatureTypeStyle}, using the {@code JAXBElement} in
     * argument, and applying it to {@code layer}. {@code ftst} must embed
     * a {@code {@code FeatureTypeStyleType}.
     * @param ftst
     * @param layer
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    public FeatureTypeStyle(JAXBElement<FeatureTypeStyleType> ftst, ILayer layer) throws InvalidStyle {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        this.setFromJAXB(ftst);
    }

    /**
     * Build a new {@code FeatureTypeStyle}, using the {@code
     * FeatureTypeStyleType} given in argument, and applying it to {@code
     * layer}.
     * @param fts
     * @param layer
     * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
     */
    public FeatureTypeStyle(FeatureTypeStyleType fts, ILayer layer) throws InvalidStyle {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        this.setFromJAXBType(fts);
    }

    private void setFromJAXB(JAXBElement<FeatureTypeStyleType> ftst) throws InvalidStyle {
        FeatureTypeStyleType fts = ftst.getValue();
        this.setFromJAXBType(fts);
    }

    private void setFromJAXBType(FeatureTypeStyleType fts) throws InvalidStyle {
        if (fts.getName() != null) {
            this.name = fts.getName();
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
    public void merge(FeatureTypeStyle style) {
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
     * Export this {@code FeatureTypeStyle} in seFile. The resulting file is
     * compatible with the Symbology Encoding specification.
     * @param seFile
     */
    public void export(String seFile) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(FeatureTypeStyleType.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(getJAXBElement(), new FileOutputStream(seFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FeatureTypeStyle.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(FeatureTypeStyle.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get a JAXB representation of this {@code FeatureTypeStyle}.
     * @return
     */
    public JAXBElement<FeatureTypeStyleType> getJAXBElement() {
        FeatureTypeStyleType ftst = new FeatureTypeStyleType();

        if (this.name != null) {
            ftst.setName(this.name);
        }
        List<RuleType> ruleTypes = ftst.getRule();
        for (Rule r : rules) {
            ruleTypes.add(r.getJAXBType());
        }

        ObjectFactory of = new ObjectFactory();

        return of.createFeatureTypeStyle(ftst);
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

        for (Rule r : this.rules) {
            // Only process visible rules with valid domain
            if (r.isVisible() && r.isDomainAllowed(mt)) {
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

        Collections.sort(layerSymbolizers);
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
     * Get the associated layer.
     * @return
     * The layer as an {@link ILayer}.
     */
    public ILayer getLayer() {
        return layer;
    }

    /**
     * Set the associated {@link ILayer} to {@code layer}.
     * @param layer
     */
    public void setLayer(ILayer layer) {
        this.layer = layer;
    }

    @Override
    public Uom getUom() {
        return null;
    }

    @Override
    public SymbolizerNode getParent() {
        return null;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Get the name of this {@code FeatureTypeStyle}.
     * @return
     * The name as a {@code String}.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this {@code FeatureTypeStyle}.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the {@link Rule}s contained in this {@code FeatureTypeStyle}.
     * @return
     * The {@code Rule}s, in a {@link List}.
     */
    public List<Rule> getRules() {
        return rules;
    }

    /**
     * Move the ith {@code Rule} up, ie swap it with the Rule at index (i-1).
     * @param i
     * @return
     * {@code true} if the move was possible, ie if and ony if
     * {@code i>0 && i < getRules().size() }
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
     * Move the ith {@code Rule} down, ie swap it with the Rule at index (i+1).
     * @param i
     * @return
     * {@code true} if the move was possible, ie if and ony if
     * {@code i>=0 && i < getRules().size() -1}
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
     * Add a {@link Rule} to this {@code FeatureTypeStyle}.
     * @param r
     */
    public void addRule(Rule r) {
        if (r != null) {
            r.setParent(this);
            rules.add(r);
        }
    }

    /**
     * Delete the ith {@link Rule} of this {@code FeatureTypeStyle}.
     * @param i
     * @return
     * {@code true} if i was a valid index in the inner {@code link} before the
     * deletion, so that a {@code Rule} as indeed been removed, false otherwise.
     */
    public boolean deleteRule(int i) {
        try {
            rules.remove(i);
            return true;
        } catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }
}
