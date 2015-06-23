/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.model;

import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the DataDescription class
 *
 * @author Sylvain PALOMINOS
 */

public class RawDataTest {

    /**
     * Tests if the constructor with a null format returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullFormatConstructorTest() {
        new RawData((Format)null);
    }

    /**
     * Tests if the constructor with a not default format returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void noDefaultFormatConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(false);
        new RawData(f);
    }

    /**
     * Tests if the constructor with a null format list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullFormatListConstructorTest() {
        new RawData((List<Format>)null);
    }

    /**
     * Tests if the constructor with an empty format list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void emptyFormatListConstructorTest() {
        new RawData(new ArrayList<Format>());
    }

    /**
     * Tests if the constructor with a format list without default format returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void noDefaultFormatListConstructorTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(false);
        Format f2 = new Format("test", new URI("http://orbisgis.org"));
        f2.setDefaultFormat(false);

        List<Format> list = new ArrayList<>();
        list.add(f1);
        list.add(f2);

        new RawData(list);
    }

    /**
     * Tests if the setFormat() method with a null format list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullSetFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);

        RawData rawData = new RawData(f1);

        rawData.setFormats(null);
    }

    /**
     * Tests if the setFormat() method with an empty format list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void emptySetFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);

        RawData rawData = new RawData(f1);

        rawData.setFormats(new ArrayList<Format>());
    }

    /**
     * Tests if the setFormat() method with a format list without default format returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void noDefaultSetFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);

        RawData rawData = new RawData(f1);
        Format f2 = new Format("test", new URI("http://orbisgis.org"));
        f2.setDefaultFormat(false);

        List<Format> list = new ArrayList<>();
        list.add(f2);

        rawData.setFormats(list);
    }


    /**
     * Tests if the addFormat() method with a null format list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullAddFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);

        RawData rawData = new RawData(f1);

        rawData.addFormat(null);
    }

    /**
     * Tests if the addFormat() method with two default format returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void multipleDefaultAddFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);
        Format f2 = new Format("test", new URI("http://orbisgis.org"));
        f2.setDefaultFormat(true);

        RawData rawData = new RawData(f1);

        rawData.addFormat(f2);
    }

    /**
     * Tests if the addFormat() method with no default format returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void noDefaultAddFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(false);

        RawData rawData = new RawData(f1);
        Format f2 = new Format("test", new URI("http://orbisgis.org"));
        f2.setDefaultFormat(false);

        rawData.addFormat(f2);
    }

    /**
     * Tests if the removeFormat() method returns an IllegalArgumentException if the last format is removed.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void removeLastFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);

        RawData rawData = new RawData(f1);

        rawData.removeFormat(f1);
    }

    /**
     * Tests if the removeFormat() method returns an IllegalArgumentException if the default format is removed.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void removeDefaultFormatTest() throws URISyntaxException {
        Format f1 = new Format("test", new URI("http://orbisgis.org"));
        f1.setDefaultFormat(true);
        Format f2 = new Format("test", new URI("http://orbisgis.org"));
        f2.setDefaultFormat(false);

        RawData rawData = new RawData(f1);
        rawData.addFormat(f2);

        rawData.removeFormat(f1);
    }
}
