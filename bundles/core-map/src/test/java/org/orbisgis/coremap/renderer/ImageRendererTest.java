package org.orbisgis.coremap.renderer;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.h2gis.utilities.SFSUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.commons.progress.NullProgressMonitor;

import javax.sql.DataSource;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;

/**
 * Rendering test for image renderer
 * @author Nicolas Fortin
 */
public class ImageRendererTest {
    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SFSUtilities.wrapSpatialDataSource(SpatialH2UT.createDataSource(ImageRendererTest.class.getSimpleName(), false));
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
        dataManager = new DataManagerImpl(dataSource);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
        dataManager.dispose();
    }

    private DataManager getDataManager() {
        return dataManager;
    }


    @Test
    public void drawLandcover() throws Exception {
        // Open map context and data
        MapContext mc = new OwsMapContext(getDataManager());
        URI owsFile = ImageRendererTest.class.getResource("../../../../data/landcover2000.ows").toURI();
        mc.setLocation(owsFile);
        mc.read(new FileInputStream(new File(owsFile)));
        mc.open(new NullProgressMonitor());
        // Draw in buffered image
        MapTransform mapTransform = new MapTransform();
        mapTransform.setExtent(mc.getBoundingBox());
        BufferedImage outImage = new BufferedImage(50, 150, BufferedImage.TYPE_4BYTE_ABGR);
        mapTransform.setImage(outImage);
        mc.draw(mapTransform, new NullProgressMonitor());
        assertEquals(new Color(0, 204, 102).getRGB(), outImage.getRGB(23, 86));
        assertEquals(new Color(204,204,0).getRGB(), outImage.getRGB(30, 112));
    }
}
