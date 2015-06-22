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

package org.orbisgis.orbistoolbox.process.model;

import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.orbistoolbox.process.model.DataDescription;
import org.orbisgis.orbistoolbox.process.model.Format;
import org.orbisgis.orbistoolbox.process.model.Output;
import org.orbisgis.orbistoolbox.process.model.RawData;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for the Output class.
 *
 * @author Sylvain PALOMINOS
 */

public class OutputTest {
    
    /**
     * Tests if the constructor with a null title returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullTitleConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        new Output(null, new URI("test"), new RawData(f));
    }

    /**
     * Tests if the constructor with a null identifier returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullURIConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        new Output("test", null, new RawData(f));
    }

    /**
     * Tests if the constructor with a null dataDescription returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullDataDescriptionConstructorTest() throws URISyntaxException {
        new Output("test", new URI("test"), (List<Output>)null);
    }

    /**
     * Tests if the constructor with a null output list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullOutputConstructorTest() throws URISyntaxException {
        new Output("test", new URI("test"), (DataDescription)null);
    }

    /**
     * Tests if the constructor with an empty output list returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void emptyOutputConstructorTest() throws URISyntaxException {
        List<Output> emptyList = new ArrayList<>();
        new Output("test", new URI("test"), emptyList);
    }

    /**
     * Tests if the constructor with a output list containing a null output returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void containingNullOutputConstructorTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Output output = new Output("test", new URI("test"), new RawData(f));
        List<Output> nullList = new ArrayList<>();
        nullList.add(output);
        nullList.add(null);
        new Output("test", new URI("test"), nullList);
    }

    /**
     * Tests if setting the output list sets the data description to null.
     */
    @Test()
    public final void setOutputTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Output output1 = new Output("test", new URI("test"), new RawData(f));
        Output output2 = new Output("test", new URI("test"), new RawData(f));

        List<Output> list = new ArrayList<>();
        list.add(output2);

        output1.setOutput(list);

        Assert.assertEquals("The data description should be null.", null, output1.getDataDescription());
        Assert.assertEquals("The output list is not the same as the one given.", list, output1.getOutput());
    }

    /**
     * Tests if setting the data description sets the output list to null.
     */
    @Test()
    public final void setDataDescriptionTest() throws URISyntaxException {
        Format f = new Format("test", new URI("http://orbisgis.org"));
        f.setDefaultFormat(true);
        Output output = new Output("test", new URI("test"), new RawData(f));

        DataDescription dataDescription = new RawData(f);
        output.setDataDescription(dataDescription);

        Assert.assertEquals("The data description should be null.", null, output.getOutput());
        Assert.assertEquals("The output list is not the same as the one given.", dataDescription, output.getDataDescription());
    }
}
