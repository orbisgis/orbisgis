package org.orbisgis.core.ui.pluginSystem;

import java.util.List;

public interface WorkbenchProperties {
    public List getPlugInClasses() throws ClassNotFoundException;

    public List getPlugInClasses(ClassLoader classLoader) throws ClassNotFoundException;

    public List getInputDriverClasses() throws ClassNotFoundException;

    public List getOutputDriverClasses() throws ClassNotFoundException;
   
    public List getConfigurationClasses() throws ClassNotFoundException;    
}

