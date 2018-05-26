package org.orbisgis.omanager.plugin.api;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.omanager.plugin.api.RepositoryPluginHandler;

public class DownloadOBRProcess extends SwingWorkerPM {
    RepositoryPluginHandler remotePlugins;

    public DownloadOBRProcess(RepositoryPluginHandler remotePlugins) {
        this.remotePlugins = remotePlugins;
    }

    @Override
    protected Object doInBackground() throws Exception {
        remotePlugins.refresh(getProgressMonitor());
        return null;
    }

    @Override
    public String toString() {
        return "MainPanel#DownloadOBRProcess";
    }
}