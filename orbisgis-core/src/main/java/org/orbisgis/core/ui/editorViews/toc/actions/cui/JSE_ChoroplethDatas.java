/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Component;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.feature.Feature;
import org.gdms.data.values.Value;
import org.orbisgis.core.renderer.classification.Range;
import org.gdms.data.metadata.Metadata;
import org.gdms.driver.DriverException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.renderer.classification.RangeMethod;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.sif.UIPanel;
import org.orbisgis.core.ui.plugins.toc.CreateChoroplethPlugIn;

/**
 *
 * @author sennj
 */
public class JSE_ChoroplethDatas implements UIPanel{

    private ILayer layer;

    private List<JSE_Datas> datas;

    private List<String> fields;
    private Range[] ranges;
    private Value value;
    private int colIndex;
    private int nbRange;

    JSE_ChoroplethDatas(ILayer layer) {
         super();
        this.layer = layer;
        fields = new ArrayList<String>();

        colIndex=0;
        nbRange=4;
    }

    void readData() throws DriverException
    {
        Metadata metadata = layer.getDataSource().getMetadata();

        // Quick (and hugly) step to fetch the first numeric attribute
        String retainedFiledName = null;
        for (int i = 0; i < metadata.getFieldCount(); i++) {
                int currentType = metadata.getFieldType(i).getTypeCode();

                if (currentType == 4 || (currentType >= 16 && currentType <= 256)) {
                        retainedFiledName = metadata.getFieldName(i);
                        fields.add(retainedFiledName);
                }
        }

        for(int i=1;i<=fields.size();i++)
        {
            System.out.println("fields "+i+" "+fields.get(i-1));
        }
        
        String selectedField = fields.get(colIndex);


        if (selectedField != null) {
            try {
                RealAttribute defaultField = new RealAttribute(selectedField);
                RangeMethod rangesHelper = new RangeMethod(layer.getDataSource(), defaultField, nbRange);
                rangesHelper.disecQuantiles();
                ranges = rangesHelper.getRanges();

                Feature feature = new Feature(metadata);
                value = feature.getValue(selectedField);


            } catch (ParameterException ex) {
                Logger.getLogger(CreateChoroplethPlugIn.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    List<JSE_Datas> getDatas(){
        return null;
    }

     List<JSE_Steps> getSteps(){
        return null;
    }
     
    List<String> getFields(){
         return fields;
    }

    Range[] getRange(){
         return ranges;
    }

    Value getValue()
    {
        return value;
    }

    @Override
    public URL getIconURL() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getTitle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String initialize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String postProcess() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String validateInput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Component getComponent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getInfoText() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
