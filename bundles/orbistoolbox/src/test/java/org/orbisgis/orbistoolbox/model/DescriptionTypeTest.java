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

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the DescriptionType class.
 *
 * @author Sylvain PALOMINOS
 */

public class DescriptionTypeTest {

    /**
     * Tests if the constructor with a null title returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullTitleConstructorTest() throws URISyntaxException {
        new DescriptionType(null, new URI("test"));
    }

    /**
     * Tests if the constructor with a null identifier returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullURIConstructorTest(){
        new DescriptionType("test", null);
    }

    /**
     * Tests if the constructor with a null title and null identifier returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void nullURITitleConstructorTest(){
        new DescriptionType(null, null);
    }

    /**
     * Tests if setting the title to null returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void setTitleTest() throws URISyntaxException {
        DescriptionType descriptionType = new DescriptionType("test", new URI("test"));
        descriptionType.setTitle(null);
    }

    /**
     * Tests if setting the identifier to null returns an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public final void setIdentifierTest() throws URISyntaxException {
        DescriptionType descriptionType = new DescriptionType("test", new URI("test"));
        descriptionType.setIdentifier(null);
    }

    /**
     * Tests if the keywords methods work well even if null value are used.
     */
    @Test()
    public final void keywordsTest() throws URISyntaxException {
        DescriptionType descriptionType = new DescriptionType("test", new URI("test"));

        List<String> stringList = new ArrayList<>();
        stringList.add("orbisgis");
        stringList.add("orbistoolbox");
        stringList.add("plugin");
        descriptionType.setKeywords(stringList);
        descriptionType.addKeyword("gis");
        descriptionType.removeAllKeywords(null);
        descriptionType.removeKeyword("plugin");

        stringList = new ArrayList<>();
        stringList.add("mapcomposer");
        stringList.add("test");
        stringList.add(null);
        descriptionType.addAllKeywords(stringList);
        descriptionType.addAllKeywords(null);

        stringList = new ArrayList<>();
        stringList.add("mapcomposer");
        stringList.add("orbistoolbox");
        descriptionType.removeAllKeywords(stringList);
        descriptionType.removeKeyword(null);
        descriptionType.addKeyword(null);

        stringList = new ArrayList<>();
        stringList.add("orbisgis");
        stringList.add("gis");
        stringList.add("test");

        Assert.assertTrue(descriptionType.getKeywords().size() == stringList.size()
                && descriptionType.getKeywords().containsAll(stringList));
    }

    /**
     * Tests if the metadata methods work well even if null value are used.
     */
    @Test()
    public final void metadataTest() throws URISyntaxException {
        DescriptionType descriptionType = new DescriptionType("test", new URI("test"));

        Metadata metadata1 = new Metadata("test", new URI("test"), new URI("test"));
        Metadata metadata2 = new Metadata("test", new URI("test"), new URI("test"));
        Metadata metadata3 = new Metadata("test", new URI("test"), new URI("test"));
        Metadata metadata4 = new Metadata("test", new URI("test"), new URI("test"));
        Metadata metadata5 = new Metadata("test", new URI("test"), new URI("test"));
        Metadata metadata6 = new Metadata("test", new URI("test"), new URI("test"));

        List<Metadata> list = new ArrayList<>();
        list.add(metadata1);
        list.add(metadata2);
        list.add(metadata3);
        descriptionType.setMetadata(list);
        descriptionType.addMetadata(metadata4);
        descriptionType.removeAllMetadatas(null);
        descriptionType.removeMetadata(metadata3);

        list = new ArrayList<>();
        list.add(metadata5);
        list.add(metadata6);
        list.add(null);
        descriptionType.addAllMetadata(list);
        descriptionType.addAllMetadata(null);

        list = new ArrayList<>();
        list.add(metadata5);
        list.add(metadata2);
        descriptionType.removeAllMetadatas(list);
        descriptionType.removeMetadata(null);
        descriptionType.addMetadata(null);

        list = new ArrayList<>();
        list.add(metadata1);
        list.add(metadata4);
        list.add(metadata6);

        Assert.assertTrue(descriptionType.getMetadata().size() == list.size()
                && descriptionType.getMetadata().containsAll(list));
    }
}
