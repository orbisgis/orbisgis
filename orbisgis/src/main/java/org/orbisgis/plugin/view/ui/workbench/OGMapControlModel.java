/**
 *
 */
package org.orbisgis.plugin.view.ui.workbench;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.ExecutionException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SyntaxException;
import org.gdms.driver.DriverException;
import org.gdms.geotoolsAdapter.FeatureCollectionAdapter;
import org.gdms.geotoolsAdapter.GeometryAttributeTypeAdapter;
import org.gdms.spatial.SpatialDataSource;
import org.gdms.spatial.SpatialDataSourceDecorator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.Style;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.plugin.TempPluginServices;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.ILayerAction;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerCollectionListener;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;
import org.orbisgis.plugin.view.layerModel.RasterLayer;
import org.orbisgis.plugin.view.layerModel.VectorLayer;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

public class OGMapControlModel implements MapControlModel {

    private LayerCollection root;

    private MapContext mc;

    private MapControl mapControl;

    private StreamingRenderer streamingRenderer;

    private List<Exception> problems = new ArrayList<Exception>();

    private LayerListener layerListener;

	private LinkedList<LayerStackEntry> drawingStack;

    public OGMapControlModel(LayerCollection root) {
        this.root = root;
        layerListener = new LayerListener();
        listen(root);

        problems.clear();
        prepareStreamingRenderer(null);
    }

    public void setMapControl(MapControl mapControl) {
        this.mapControl = mapControl;
    }

    private void listen(ILayer node) {
        LayerCollection.processLayersNodes(node, new ILayerAction() {
            public void action(ILayer layer) {
                layer.addLayerListener(layerListener);
                if (layer instanceof LayerCollection) {
                    ((LayerCollection) layer)
                            .addCollectionListener(layerListener);
                }
            }
        });
    }

    private class LayerStackEntry {
        private Object fcOrgc;

        private Style style;

        public LayerStackEntry(final Object fcOrgc, final Style style) {
            this.fcOrgc = fcOrgc;
            this.style = style;
        }

        public Object getFcOrgc() {
            return fcOrgc;
        }

        public Style getStyle() {
            return style;
        }
    }

    private void prepareStreamingRenderer(final Rectangle2D bbox) {
        drawingStack = new LinkedList<LayerStackEntry>();

        mc = new DefaultMapContext( root.getCoordinateReferenceSystem());
        GeometryAttributeTypeAdapter.currentCRS = root
                .getCoordinateReferenceSystem();

        LayerCollection.processLayersLeaves(root, new ILayerAction() {
            public void action(ILayer layer) {
                if (layer instanceof VectorLayer) {
                    try {
                        VectorLayer vl = (VectorLayer) layer;
                        if ( vl.isVisible()) {
                            SpatialDataSource sds = vl.getDataSource();
                            sds.open();
                            if (bbox != null) {
                                Rectangle2D extent = mapControl
                                        .getAdjustedExtent();
                                DataSourceFactory dsf = TempPluginServices.dsf;
                                String sql = "select * from "
                                        + sds.getName()
                                        + " where Intersects(GeomFromText('POLYGON (( "
                                        + extent.getMinX() + " "
                                        + extent.getMinY() + ", "
                                        + extent.getMaxX() + " "
                                        + extent.getMinY() + ", "
                                        + extent.getMaxX() + " "
                                        + extent.getMaxY() + ", "
                                        + extent.getMinX() + " "
                                        + extent.getMaxY() + ", "
                                        + extent.getMinX() + " "
                                        + extent.getMinY() + "))'), "
                                        + sds.getDefaultGeometry() + " )";
                                System.out.println(sql);
                                DataSource filtered = dsf.executeSQL(sql);
                                sds.cancel();
                                sds = new SpatialDataSourceDecorator(filtered);
                                sds.open();
                            }
                            drawingStack.addFirst(new LayerStackEntry(
                                    new FeatureCollectionAdapter(sds), vl
                                            .getStyle()));
                            // mc.addLayer(new FeatureCollectionAdapter(sds), vl
                            // .getStyle());
                        }
                    } catch (DriverException e) {
						reportProblem(e);
                    } catch (SyntaxException e) {
						reportProblem(e);
                    } catch (DriverLoadException e) {
						reportProblem(e);
                    } catch (NoSuchTableException e) {
						reportProblem(e);
                    } catch (ExecutionException e) {
						reportProblem(e);
                    }
                } else if (layer instanceof RasterLayer) {
                    RasterLayer rl = (RasterLayer) layer;
                    if (rl.isVisible()) {
                        GridCoverage gc = rl.getGridCoverage();
                        drawingStack
                                .addFirst(new LayerStackEntry(gc, rl.getStyle()));
                        // mc.addLayer(gc, rl.getStyle());
                    }
                }
            }
        });

        for (LayerStackEntry item : drawingStack) {
            if (item.getFcOrgc() instanceof FeatureCollection) {
                mc.addLayer((FeatureCollection) item.getFcOrgc(), item
                        .getStyle());
            } else if (item.getFcOrgc() instanceof GridCoverage) {
                mc.addLayer((GridCoverage) item.getFcOrgc(), item.getStyle());
            }
        }

        streamingRenderer = new StreamingRenderer();
        streamingRenderer.setContext(mc);
    }

    public void draw(BufferedImage image, Rectangle2D bbox, int imageWidth,
            int imageHeight, Color backColor) {
        prepareStreamingRenderer(bbox);
        streamingRenderer.paint(image.createGraphics(), new Rectangle(0, 0,
                imageWidth, imageHeight), getEnvelope(bbox, root
                .getCoordinateReferenceSystem()));
        closeDataSources();
    }

    private void closeDataSources() {
    	for (LayerStackEntry stackEntry : drawingStack) {
			Object fc = stackEntry.fcOrgc;
			if (fc instanceof FeatureCollectionAdapter) {
				try {
					((FeatureCollectionAdapter)fc).getDataSource().cancel();
				} catch (AlreadyClosedException e) {
					reportProblem(e);
				} catch (DriverException e) {
					reportProblem(e);
				}
			}
		}
	}

	private void reportProblem(Exception e) {
		problems.add(e);
		throw new RuntimeException(e);
	}

	private ReferencedEnvelope getEnvelope(Rectangle2D bbox,
            CoordinateReferenceSystem crs) {
        Envelope env = new Envelope(new Coordinate(bbox.getMinX(), bbox
                .getMinY()), new Coordinate( bbox.getMaxX(), bbox.getMaxY()));
        return new ReferencedEnvelope(env, crs);
    }

    public Exception[] getProblems() {
        return problems.toArray(new Exception[0]);
    }

    public Rectangle2D getMapArea() {
        LayerAction la = new LayerAction();
        LayerCollection.processLayersLeaves(root, la);
        Envelope globalEnv = la.getGlobalEnvelope();
        return (null == globalEnv) ? null : new Rectangle2D.Double(globalEnv
                .getMinX(), globalEnv.getMinY(), globalEnv.getWidth(),
                globalEnv.getHeight());
        // Envelope e = mc.getAreaOfInterest();
        // return new Rectangle2D.Double(e.getMinX(), e.getMinY(), e.getWidth(),
        // e.getHeight());
    }

    private class LayerListener implements LayerCollectionListener,
            org.orbisgis.plugin.view.layerModel.LayerListener {

        public void layerAdded(LayerCollectionEvent listener) {
            for (ILayer layer : listener.getAffected()) {
                layer.addLayerListener(this);
            }
        }

        public void layerMoved(LayerCollectionEvent listener) {
        }

        public void layerRemoved(LayerCollectionEvent listener) {
            for (ILayer layer : listener.getAffected()) {
                layer.removeLayerListener(this);
            }
        }

        public void nameChanged(LayerListenerEvent e) {
        }

        public void visibilityChanged(LayerListenerEvent e) {
            mapControl.drawMap();
        }
    }
}