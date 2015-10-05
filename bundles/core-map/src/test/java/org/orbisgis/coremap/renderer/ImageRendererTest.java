package org.orbisgis.coremap.renderer;

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.h2gis.utilities.SFSUtilities;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.commons.progress.NullProgressMonitor;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.sql.DataSource;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Rendering test for image renderer
 * @author Nicolas Fortin
 */
public class ImageRendererTest {
    private static Connection connection;
    private static DataManager dataManager;
    @Rule
    public TemporaryFolder folder= new TemporaryFolder();

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
        assertTrue(new File(owsFile).exists());
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

    @Test
    public void drawRaster() throws Exception {
        // Transfer raster image into H2 database table
        Statement st = connection.createStatement();
        st.execute("DROP TABLE IF EXISTS TEST");
        st.execute("CREATE TABLE RASTERTEST(ID SERIAL, RAST RASTER)");
        PreparedStatement pst = connection.prepareStatement("INSERT INTO RASTERTEST(RAST) VALUES (" +
                "ST_RasterFromImage(?,-15312, 15350, 257.5, -257.5, 0,\n" +
                "0, 27572))");
        try(InputStream is = ImageRendererTest.class.getResource("../../../../data/ace.tiff").openStream()) {
            pst.setBinaryStream(1, is);
            pst.execute();
        }
        // Open map context and data
        MapContext mc = new OwsMapContext(getDataManager());
        mc.open(new NullProgressMonitor());
        mc.getLayerModel().addLayer(mc.createLayer("RASTERTEST"));
        mc.setBoundingBox(new Envelope(-16000, 16000, -16000, 16000));
        // Draw in buffered image
        MapTransform mapTransform = new MapTransform();
        mapTransform.setExtent(mc.getBoundingBox());
        BufferedImage outImage = new BufferedImage(200, 200, BufferedImage.TYPE_4BYTE_ABGR);
        mapTransform.setImage(outImage);
        mc.draw(mapTransform, new NullProgressMonitor());
        ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("png").next();
        File createdFile= folder.newFile("outtest.png");
        try(RandomAccessFile raf = new RandomAccessFile(createdFile, "rw")) {
            imageWriter.setOutput(ImageIO.createImageOutputStream(raf));
            imageWriter.write(new IIOImage(outImage, null, null));
        }
    }
}
