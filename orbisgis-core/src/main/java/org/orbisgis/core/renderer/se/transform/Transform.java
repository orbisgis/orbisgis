/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.persistance.se.MatrixType;
import org.orbisgis.core.renderer.persistance.se.RotateType;
import org.orbisgis.core.renderer.persistance.se.ScaleType;
import org.orbisgis.core.renderer.persistance.se.TransformType;
import org.orbisgis.core.renderer.persistance.se.TranslateType;
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

    public Transform() {
        transformations = new ArrayList<Transformation>();
        consolidated = null;
    }

    public Transform(TransformType t) {
        transformations = new ArrayList<Transformation>();
        consolidated = null;
        

        for (Object o : t.getTranslateOrRotateOrScale()){
            if (o instanceof TranslateType){
                this.transformations.add(new Translate((TranslateType)o));
            }
            else if (o instanceof RotateType){
                this.transformations.add(new Rotate((RotateType)o));
            }
            else if (o instanceof ScaleType){
                this.transformations.add(new Scale((ScaleType)o));
            }
            else if (o instanceof MatrixType){
                this.transformations.add(new Matrix((MatrixType)o));
            }
        }
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
    public AffineTransform getGraphicalAffineTransform(DataSource ds, long fid, boolean isForSpatialFeatures) throws ParameterException, IOException {
        //return consolidateTrasformations(false).getGraphicalAffineTransform();
        this.consolidateTransformations(ds, fid, isForSpatialFeatures);
        
        return consolidated;
    }


    /*
     * This method must be called after each modification of one of its transformations !
     *
     */
    public void consolidateTransformations(DataSource ds, long fid, boolean forGeometries) throws ParameterException, IOException {
        int i;

        // Result is Identity
        consolidated = new AffineTransform();
        for (Transformation t : transformations) {
            if (!forGeometries || t.allowedForGeometries()) {
                AffineTransform at = t.getAffineTransform(ds, fid, this.getUom());
                consolidated.preConcatenate(at);
            }
        }
    }

    public void addTransformation(Transformation t) {
        transformations.add(t);
    }

    public int getNumTransformation() {
        return transformations.size();
    }

    public Transformation getTransformation(int i) {
        return transformations.get(i);
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


    public TransformType getJAXBType(){
        TransformType t = new TransformType();

        List<Object> list = t.getTranslateOrRotateOrScale();


        for (Transformation tr : transformations){
            // TODO CHECK ORDER !
            list.add(tr.getJAXBType());
        }

        return t;
    }

    private SymbolizerNode parent;
    private AffineTransform consolidated;
    private ArrayList<Transformation> transformations;
}
