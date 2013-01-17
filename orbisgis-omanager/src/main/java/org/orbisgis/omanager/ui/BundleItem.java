package org.orbisgis.omanager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.osgi.service.obr.Resource;

/**
 * A bundle that can be installed in local or/and on remote repository.
 * @author Nicolas Fortin
 */
public class BundleItem {
    private int bundleId=-1;
    private String name;
    private Resource obrResource;


    /**
     * @return The bundle presentation name (artifact-id by default)
     */
    String getPresentationName() {
        return "";
    }

    /**
     * @return The bundle short description. (empty string if none)
     */
    String getShortDescription() {
        return "";
    }

    /**
     * @return A map of bundle details to show on the right side of the GUI. (Title->Value)
     */
    Map<String,String> getDetails() {
        return new HashMap<String, String>();
    }

    /**
     * @return Bundle tags
     */
    List<String> getBundleCategories() {
        return new ArrayList<String>();
    }
}
