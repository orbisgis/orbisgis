package org.orbisgis.view.background;

import org.h2gis.h2spatialapi.ProgressVisitor;
import org.orbisgis.progress.ProgressMonitor;

/**
 * Wrapper between ProgressVisitor and ProgressMonitor.
 * @author Nicolas Fortin
 */
public class H2GISProgressMonitor implements ProgressVisitor {
    private ProgressMonitor progressMonitor;

    public H2GISProgressMonitor(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
    }

    @Override
    public void endOfProgress() {
        progressMonitor.endTask();
    }

    @Override
    public ProgressVisitor subProcess(int i) {
        progressMonitor.startTask("", i);
        return this;
    }

    @Override
    public void endStep() {
        progressMonitor.progressTo(progressMonitor.getCurrentProgress() + 1);
    }

    @Override
    public void setStep(int i) {
        progressMonitor.progressTo(i);
    }

    @Override
    public int getStepCount() {
        return progressMonitor.getOverallProgress();
    }

    @Override
    public double getProgression() {
        return progressMonitor.getCurrentProgress() / progressMonitor.getOverallProgress();
    }
}
