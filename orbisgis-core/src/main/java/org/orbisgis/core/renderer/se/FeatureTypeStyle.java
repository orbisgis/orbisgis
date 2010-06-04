/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.map.MapTransform;


import org.orbisgis.core.renderer.persistance.se.FeatureTypeStyleType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.RuleType;
import org.orbisgis.core.renderer.se.common.Uom;

/**
 *
 * @author maxence
 */
public class FeatureTypeStyle implements SymbolizerNode {

    public FeatureTypeStyle(ILayer layer) {
        rules = new ArrayList<Rule>();
        this.layer = layer;
        this.byLevel = true;
    }


    public FeatureTypeStyle(JAXBElement<FeatureTypeStyleType> ftst, ILayer layer) {
        this(layer);
        FeatureTypeStyleType fts = ftst.getValue();


        if (fts.getName() != null) {
            this.name = fts.getName();
        }

        if (fts.getRule() != null) {
            for (RuleType rt : fts.getRule()) {
                this.addRule(new Rule(rt));
            }
        }
    }

    public void addRule(Rule r) {
        r.setParent(this);
        rules.add(r);
    }

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
            ArrayList<Symbolizer> layerSymbolizers,
            ArrayList<Symbolizer> overlaySymbolizers,
            ArrayList<Rule> rules,
            ArrayList<Rule> fallbackRules) {

        System.out.println("   GetSymbolizers");
        
        for (Rule r : this.rules) {

            System.out.println("      => Rule :" + r);

            if (r.isDomainAllowed(mt)) {
                System.out.println("        Domain OK");
                if (!r.isFallbackRule()) {
                    System.out.println ("not else filter");
                    rules.add(r);
                } else {
                    System.out.println ("else rule");
                    fallbackRules.add(r);
                }
                
                for (Symbolizer s : r.getCompositeSymbolizer().getSymbolizerList()) {
                    if (s instanceof TextSymbolizer) {
                        System.out.println ("Overlay");
                        overlaySymbolizers.add(s);
                    } else {
                        System.out.println ("BaseLayer");
                        layerSymbolizers.add(s);
                    }
                }
            }
            else{
                System.out.println("        Domain NOTOK");
            }
        }

        Collections.sort(layerSymbolizers);
    }

    public void hardSetSymbolizerLevel() {
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

    public ILayer getLayer() {
        return layer;
    }

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

    public boolean isByLevel() {
        return byLevel;
    }

    public void setByLevel(boolean byLevel) {
        this.byLevel = byLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void setRules(ArrayList<Rule> rules) {
        this.rules = rules;
    }

    

    private String name;
    private ArrayList<Rule> rules;
    private ILayer layer;
    private boolean byLevel = false;
}
