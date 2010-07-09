package org.orbisgis.core.renderer.se.parameter;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.xml.bind.JAXBElement;
import org.gdms.data.DataSource;
import org.gdms.data.feature.Feature;
import org.orbisgis.core.renderer.persistance.ogc.ExpressionType;
import org.orbisgis.core.renderer.persistance.se.MapItemType;
import org.orbisgis.core.renderer.persistance.se.ObjectFactory;
import org.orbisgis.core.renderer.persistance.se.ParameterValueType;
import org.orbisgis.core.renderer.persistance.se.RecodeType;

import org.orbisgis.core.renderer.se.parameter.string.*;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.EditFeatureTypeStylePanel;

public abstract class Recode<ToType extends SeParameter, FallbackType extends ToType> implements SeParameter {

    protected Recode(){
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    public Recode(FallbackType fallbackValue, StringParameter lookupValue) {
        this.fallbackValue = fallbackValue;
        this.lookupValue = lookupValue;
        mapItems = new ArrayList<MapItem<ToType>>();
    }

    @Override
    public final boolean dependsOnFeature() {
        if (this.getLookupValue().dependsOnFeature()) {
            return true;
        }

        int i;
        for (i = 0; i < this.getNumMapItem(); i++) {
            if (this.getMapItemValue(i).dependsOnFeature()) {
                return true;
            }
        }

        return false;
    }

    public void setFallbackValue(FallbackType fallbackValue) {
        this.fallbackValue = fallbackValue;
    }

    public FallbackType getFallbackValue() {
        return fallbackValue;
    }

    // TODO  On doit pouvoir récuperer des string ou des couleurs
    public void setLookupValue(StringParameter lookupValue) {
        this.lookupValue = lookupValue;
    }

    public StringParameter getLookupValue() {
        return lookupValue;
    }

    /**
     * Return the number of unique value defined within the function.
     *  @return number of unique value
     */
    public int getNumMapItem() {
        return mapItems.size();
    }

    /**
     * Add a new map item
     * @param key
     * @param value
     */
    public void addMapItem(String key, ToType value) {
        MapItem<ToType> item = new MapItem<ToType>(value, key);

        if (mapItems.contains(item)) {
            //TODO  throw break unique value rules
        } else {
            mapItems.add(item);
        }
    }

    public ToType getMapItemValue(String key) {
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        return mapItems.get(mapItems.indexOf(item)).getValue();
    }

    public ToType getMapItemValue(int i) {
        return mapItems.get(i).getValue();
    }

    public String getMapItemKey(int i) {
        return mapItems.get(i).getKey();
    }

    public void removeMapItem(String key) {
        MapItem<ToType> item = new MapItem<ToType>(null, key);
        mapItems.remove(item);
    }

    public ToType getParameter(Feature feat) {
        try {
            String key = lookupValue.getValue(feat);
            return getMapItemValue(key);
        } catch (Exception e) {
            return fallbackValue;
        }
    }


    @Override
    public ParameterValueType getJAXBParameterValueType()
    {
        ParameterValueType p = new ParameterValueType();
        p.getContent().add(this.getJAXBExpressionType());
        return p;
    }

    @Override
    public JAXBElement<? extends ExpressionType> getJAXBExpressionType() {
        RecodeType r = new RecodeType();

        if (fallbackValue != null) {
            r.setFallbackValue(fallbackValue.toString());
        }
        if (lookupValue != null) {
            r.setLookupValue(lookupValue.getJAXBParameterValueType());
        }

        List<MapItemType> mi = r.getMapItem();

        for (MapItem<ToType> m : mapItems) {
            MapItemType mt = new MapItemType();
            mt.setValue(m.getValue().getJAXBParameterValueType());
            mt.setKey(m.getKey());
            mi.add(mt);
        }
        ObjectFactory of = new ObjectFactory();
        return of.createRecode(r);
    }

	@Override
	public JPanel getEditionPanel(EditFeatureTypeStylePanel ftsPanel){
		throw new UnsupportedOperationException("Not yet implemented ("+ this.getClass() + " )");
	}


    protected FallbackType fallbackValue;
    protected StringParameter lookupValue;
    protected ArrayList<MapItem<ToType>> mapItems; // TODO switch to hash table <k: String, v: ToType>
}
