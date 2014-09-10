package org.orbisgis.view.background;

import org.h2gis.h2spatialapi.ProgressVisitor;
import org.orbisgis.progress.ProgressMonitor;

import java.beans.PropertyChangeListener;
import java.sql.Statement;

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
        return new H2GISProgressMonitor(progressMonitor.startTask(i));
    }

    @Override
    public void endStep() {
        progressMonitor.endTask();
    }

    @Override
    public void setStep(int i) {
        progressMonitor.progressTo(i);
    }

    @Override
    public int getStepCount() {
        return (int)progressMonitor.getEnd();
    }

    @Override
    public double getProgression() {
        return progressMonitor.getOverallProgress();
    }

    @Override
    public boolean isCanceled() {
        return progressMonitor.isCancelled();
    }

    @Override
    public void cancel() {
        progressMonitor.setCancelled(true);
    }

    @Override
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        progressMonitor.addPropertyChangeListener(property.equals(PROPERTY_CANCELED)
                ? ProgressMonitor.PROP_CANCEL: property, listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        progressMonitor.removePropertyChangeListener(listener);
    }
}
