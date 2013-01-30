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
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.opengis.se._2_0.core.*;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.AbstractSymbolizerNode;
import org.orbisgis.core.renderer.se.SeExceptions.InvalidStyle;
import org.orbisgis.core.renderer.se.SymbolizerNode;
import org.orbisgis.core.renderer.se.UomNode;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 *
 * This class contains a collection of {@code Transformation}s.
 *
 * @author Maxence Laurent, Alexis Guéganno
 */
public class Transform extends AbstractSymbolizerNode implements UomNode {

        private Uom uom;
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
                        Transformation trans = getTransformationFromJAXB(o);
                        if(trans != null){
                                this.transformations.add(trans);
                                trans.setParent(this);
                        }
                }
        }

        private Transformation getTransformationFromJAXB(Object o) throws InvalidStyle {
                if (o instanceof TranslateType) {
                        return new Translate((TranslateType) o);
                } else if (o instanceof RotateType) {
                        return new Rotate((RotateType) o);
                } else if (o instanceof ScaleType) {
                        return new Scale((ScaleType) o);
                } else if (o instanceof MatrixType) {
                        return new Matrix((MatrixType) o);
                }
                return null;

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
         * @param isForSpatialFeatures
         * @param map
         * @param mt
         * @param width
         * @param height
         * @return
         * @throws ParameterException
         * @throws IOException
         */
        public AffineTransform getGraphicalAffineTransform(boolean isForSpatialFeatures, 
                    Map<String,Value> map, MapTransform mt, Double width, Double height)
                    throws ParameterException, IOException {
                //return consolidateTrasformations(false).getGraphicalAffineTransform();
                this.consolidateTransformations(map, isForSpatialFeatures, mt, width, height);
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
        public void consolidateTransformations(Map<String,Value> map, boolean forGeometries,
                MapTransform mt, Double width, Double height) throws ParameterException, IOException {

                // Result is Identity
                consolidated = new AffineTransform();
                for (Transformation t : transformations) {
                        if (!forGeometries || t.allowedForGeometries()) {
                                AffineTransform at = t.getAffineTransform(map, this.getUom(), mt, width, height);
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
        public UsedAnalysis getUsedAnalysis() {
                UsedAnalysis result = new UsedAnalysis();
                for (Transformation t : this.transformations) {
                        result.merge(t.getUsedAnalysis());
                }
                return result;
        }

        @Override
        public List<SymbolizerNode> getChildren() {
                List<SymbolizerNode> ls = new ArrayList<SymbolizerNode>();
                ls.addAll(transformations);
                return ls;
        }

        @Override
        public Uom getUom() {
                if (uom != null) {
                        return uom;
                } else if(getParent() instanceof UomNode){
                        return ((UomNode)getParent()).getUom();
                } else {
                        return Uom.PX;
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
