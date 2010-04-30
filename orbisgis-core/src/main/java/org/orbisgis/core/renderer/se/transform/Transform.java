/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.orbisgis.core.renderer.se.transform;

import com.vividsolutions.jts.geom.util.AffineTransformation;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * This class contains a collection of affine transformation
 *
 * @author maxence
 */
public class Transform implements SymbolizerNode {


    public Transform(){
        transformations = new ArrayList<Transformation>();
        consolidated = null;
    }

    /**
     *
     *
     * @param ds 
     * @param fid 
     * @return
     * @throws ParameterException
     */
    public AffineTransformation getSpatialAffineTransform(DataSource ds, int fid) throws ParameterException{
        //return consolidateTransformations(true).getSpatialAffineTransform();
        this.consolidateTransformations(true);
        return consolidated.getSpatialAffineTransform(ds, fid); // TODO useful ? check with LiteShape !
    }

    /**
     * Return an affine transformation for java Shape object.
     * The purpose is to transfom se.graphics
     *
     * @param ds 
     * @param fid 
     * @param isForSpatialFeatures
     * @return AffineTransofrm
     * @throws ParameterException
     */
    public AffineTransform getGraphicalAffineTransform(DataSource ds, int fid, boolean isForSpatialFeatures) throws ParameterException{
        //return consolidateTrasformations(false).getGraphicalAffineTransform();
        this.consolidateTransformations(isForSpatialFeatures);
        return consolidated.getGraphicalAffineTransform(ds, fid, this.getUom()); // TODO uom
    }


    /*
     * This method must be called after each modification of one of its transformations !
     *
     */
    public void consolidateTransformations(boolean forGeometries){
        int i;
        ArrayList<Matrix> mx = new ArrayList<Matrix>();

        for (i=0;i<transformations.size();i++){
            Transformation t = transformations.get(i);
            if (!forGeometries || t.allowedForGeometries()){
                mx.addAll(t.getMatrix());
            }
        }

        // Result is Identity
        consolidated = new Matrix();

        for (i=0;i<mx.size();i++){
            consolidated = mx.get(i).product(consolidated);
        }
        
        try {
            consolidated.simplify();
        } catch (ParameterException ex) {
            Logger.getLogger(Transform.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTransformation(Transformation t){
        transformations.add(t);
    }
    
    public int getNumTransformation(){
        return transformations.size();
    }

    public Transformation getTransformation(int i){
        return transformations.get(i);
    }


    public Matrix getConsolidatedMatrix(){
        return consolidated;
    }

    
    @Override
    public Uom getUom() {
        return parent.getUom();
    }

    @Override
    public SymbolizerNode getParent() {
        return parent;
    }

    @Override
    public void setParent(SymbolizerNode node) {
        parent = node;
    }


    private SymbolizerNode parent;
    private Matrix consolidated;

    private ArrayList<Transformation> transformations;

}
