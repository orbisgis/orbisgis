/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.opengis.se._2_0.core.*;
import org.gdms.data.DataSource;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;

/**
 *
 * This class contains a collection of {@code Transformation}s.
 *
 * @author maxence, alexis
 */
public class Transform implements SymbolizerNode, UomNode {

        private Uom uom;
        private SymbolizerNode parent;
        private AffineTransform consolidated;
        private ArrayList<Transformation> transformations;

        /**
         * Gets a {@code String} representation of this {@code Transform}, by
         * printing the {@code String} representation of each inner
         * {@code Transformation}.
         * @return
         * A {@code String} representation of this {@code Transform}
         */
        @Override
        public String toString() {
                String r = "";
                for (Transformation t : transformations) {
                        r += t.toString();
                }
                return r;
        }

        /**
         * Build a new {@code Transform}, that does not contain any {@code 
         * Transformation}
         */
        public Transform() {
                transformations = new ArrayList<Transformation>();
                consolidated = null;
        }

        /**
         * Build a new {@code Transform}, that will conatin only the
         * {@code Transformation} represented by {@code t}.
         * @param t
         * @throws org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle
         */
        public Transform(TransformType t) throws InvalidStyle {
                transformations = new ArrayList<Transformation>();
                consolidated = null;

                if (t.getUom() != null) {
                        this.setUom(Uom.fromOgcURN(t.getUom()));
                }

                for (Object o : t.getTranslateOrRotateOrScale()) {
                        if (o instanceof TranslateType) {
                                this.transformations.add(new Translate((TranslateType) o));
                        } else if (o instanceof RotateType) {
                                this.transformations.add(new Rotate((RotateType) o));
                        } else if (o instanceof ScaleType) {
                                this.transformations.add(new Scale((ScaleType) o));
                        } else if (o instanceof MatrixType) {
                                this.transformations.add(new Matrix((MatrixType) o));
                        }
                }
        }

        /**
         * Move the ith {@code Transformation} up in this {@code Transform}.
         * @param i
         * @return
         * {@code true} if the ith exists and has been moved successfully (ie
         * if i represents an existing element that is not the last one.)
         */
        public boolean moveDown(int i) {
                if (i >= 0 && i < transformations.size() - 1) {
                        Transformation remove = transformations.remove(i);
                        transformations.add(i + 1, remove);
                        return true;
                } else {
                        return false;
                }
        }

        /**
         * Move the ith {@code Transformation} down in this {@code Transform}.
         * @param i
         * @return
         * {@code true} if the ith exists and has been moved successfully (ie
         * if i represents an existing element that is not the first one.)
         */
        public boolean moveUp(int i) {
                if (i > 0 && i < transformations.size()) {
                        Transformation remove = transformations.remove(i);
                        transformations.add(i - 1, remove);
                        return true;
                } else {
                        return false;
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
        public AffineTransform getGraphicalAffineTransform(boolean isForSpatialFeatures, DataSource sds, long fid, MapTransform mt, Double width, Double height) throws ParameterException, IOException {
                //return consolidateTrasformations(false).getGraphicalAffineTransform();
                this.consolidateTransformations(sds, fid, isForSpatialFeatures, mt, width, height);
                return consolidated;
        }

        /**
         * Ensure that this {@code Transform} instance contains actually the
         * representation of the combination of its inner {@code Transformation}
         * instances.
         * This method must be called after each modification of one of its transformations !
         * @param sds
         * @param fid
         * @param forGeometries
         * @param mt
         * @param width
         * @param height
         * @throws ParameterException
         * @throws IOException
         */
        public void consolidateTransformations(DataSource sds, long fid, boolean forGeometries,
                MapTransform mt, Double width, Double height) throws ParameterException, IOException {

                // Result is Identity
                consolidated = new AffineTransform();
                for (Transformation t : transformations) {
                        if (!forGeometries || t.allowedForGeometries()) {
                                AffineTransform at = t.getAffineTransform(sds, fid, this.getUom(), mt, width, height);
                                consolidated.preConcatenate(at);
                        }
                }
        }

        /**
         * Add a {@code Transformation} to this {@code Transform}.
         * @param t
         */
        public void addTransformation(Transformation t) {
                transformations.add(t);
        }

        /**
         * Remove the ith {@code Transformation} of this {@code Transform}
         * @param i
         * @return
         * {@code true} if the removal has been successful.
         */
        public boolean removeTransformation(int i) {
                try {
                        transformations.remove(i);
                        return true;
                } catch (Exception e) {
                        return false;
                }
        }

        /**
         * Get the number of {@code Transformation}s registered in this
         * {@code Transform}.
         * @return
         */
        public int getNumTransformation() {
                return transformations.size();
        }

        /**
         * Get the ith {@code Transformation} registered in this {@code
         * Transform}.
         * @param i
         * @return
         */
        public Transformation getTransformation(int i) {
                return transformations.get(i);
        }

        @Override
        public SymbolizerNode getParent() {
                return parent;
        }

        @Override
        public void setParent(SymbolizerNode node) {
                parent = node;
        }

        /**
         * Get a new representation of this {@code Transform} as a JAXB
         * {@code TransformType}
         * @return
         */
        public TransformType getJAXBType() {
                TransformType t = new TransformType();

                if (getOwnUom() != null) {
                        t.setUom(getOwnUom().toURN());
                }

                List<Object> list = t.getTranslateOrRotateOrScale();

                for (Transformation tr : transformations) {
                        list.add(tr.getJAXBType());
                }

                return t;
        }

        @Override
        public HashSet<String> dependsOnFeature() {
                HashSet<String> result = new HashSet<String>();
                for (Transformation t : this.transformations) {
                        result.addAll(t.dependsOnFeature());
                }
                return result;
        }

        @Override
        public Uom getUom() {
                if (uom != null) {
                        return this.uom;
                } else {
                        return parent.getUom();
                }
        }

        @Override
        public Uom getOwnUom() {
                return uom;
        }

        @Override
        public void setUom(Uom uom) {
                this.uom = uom;
        }
}
