/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog;


import org.apache.log4j.Logger;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DataSourceListener;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.FieldEditionEvent;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.edition.AbstractEditableElement;
import org.orbisgis.view.edition.EditableElementException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * EditableElement that hold a DataSource.
 * 
 * Open/Close , open and close the DataSource
 */
public class EditableSource extends AbstractEditableElement {

	public static final String EDITABLE_RESOURCE_TYPE = "EditableSource";

	private String sourceName;
        private boolean editing = false;
	private DataSource ds;
	private NameChangeSourceListener listener = new NameChangeSourceListener();
    private GDMSSourceListener dataSourceListener = new GDMSSourceListener();
    private final Logger logger = Logger.getLogger(EditableSource.class);
    private final I18n i18n = I18nFactory.getI18n(EditableSource.class);

    /**
     * Construct a source from name. A new instance of DataSource will be created.
     * @param sourceName
     */
	public EditableSource(String sourceName) {
                if(sourceName==null) {
                        throw new IllegalArgumentException("Source name must "
                                + "not be null");
                }
                this.sourceName = sourceName;
                setId(sourceName);
	}

    /**
     * Construct a source from DataSource instance.
     * @param ds
     */
    public EditableSource(DataSource ds) {
        this(ds.getName());
        this.ds = ds;
    }

    @Override
    public String toString() {
        return i18n.tr("Source {0}",sourceName);
    }

    @Override
	public void close(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
        ds.removeDataSourceListener(dataSourceListener);
        if(ds.isEditable()) {
            try {
                ds.removeEditionListener(dataSourceListener);
                ds.removeMetadataEditionListener(dataSourceListener);
            } catch (UnsupportedOperationException ex) {
                // Ignore
                logger.debug(ex.getLocalizedMessage(),ex);
            }
        }
        setOpen(false);
		Services.getService(DataManager.class).getSourceManager()
				.removeSourceListener(listener);
	}

        @Override
	public String getTypeId() {
		return EDITABLE_RESOURCE_TYPE;
	}

        @Override
	public void open(ProgressMonitor progressMonitor)
			throws UnsupportedOperationException, EditableElementException {
            try {
                DataManager dataManager = Services.getService(DataManager.class);
                ds = dataManager.getDataSource(sourceName);
                if(!ds.isOpen()) {
                    ds.open();
                }
                dataManager.getSourceManager().addSourceListener(listener);
                ds.addDataSourceListener(dataSourceListener);
                if(ds.isEditable()) {
                    try {
                        ds.addEditionListener(dataSourceListener);
                        ds.addMetadataEditionListener(dataSourceListener);
                    } catch (UnsupportedOperationException ex) {
                        // Ignore
                        logger.debug(ex.getLocalizedMessage(),ex);
                    }
                }
                setOpen(true);
            } catch (DriverException e) {
                throw new EditableElementException("Cannot open the source", e);
            } catch (DriverLoadException e) {
                throw new EditableElementException("Cannot open the source", e);
            } catch (NoSuchTableException e) {
                throw new EditableElementException("Cannot open the source", e);
            } catch (DataSourceCreationException e) {
                throw new EditableElementException("Cannot open the source", e);
            }
	}

        @Override
	public boolean equals(Object obj) {
		if (obj instanceof EditableSource) {
			EditableSource er = (EditableSource) obj;
			return sourceName.equals(er.sourceName);
		} else {
			return false;
		}
	}

        @Override
	public int hashCode() {
		return sourceName.hashCode();
	}

	public DataSource getDataSource() {
		return ds;
	}

	public boolean isEditable() {
		if (ds.getSource().isSystemTableSource() || ds.getSource().isLiveSource()) {
			return false;
		}
		return editing && ds.isEditable();
	}

        /**
         * Get the data source name
         * @return 
         */
        public String getSourceName() {
                return sourceName;
        }
        
        

        /**
         * @return the Editing
         */
        public boolean isEditing() {
                return editing;
        }

        /**
         * @param Editing the Editing to set
         */
        public void setEditing(boolean Editing) {
                this.editing = Editing;
        }

        @Override
        public void save() throws UnsupportedOperationException, EditableElementException {
                if(ds!=null) {
                    try {
                        ds.commit();
                    } catch (Exception ex) {
                        throw new EditableElementException(i18n.tr("Cannot save the source modifications"),ex);
                    }
                }
        }

        @Override
        public Object getObject() throws UnsupportedOperationException {
                return ds;
        }

	private class NameChangeSourceListener implements SourceListener {

                @Override
		public void sourceAdded(SourceEvent e) {
		}

                @Override
		public void sourceNameChanged(SourceEvent e) {
			sourceName = e.getNewName();
                        setId(sourceName);
		}

                @Override
		public void sourceRemoved(SourceRemovalEvent e) {
		}
	}

    private class GDMSSourceListener implements EditionListener,
    MetadataEditionListener, DataSourceListener

    {
        @Override
        public void singleModification(EditionEvent e) {
            setModified(ds.isModified());
        }

        @Override
        public void multipleModification(MultipleEditionEvent e) {
            setModified(ds.isModified());
        }

        @Override
        public void fieldAdded(FieldEditionEvent event) {
            setModified(ds.isModified());
        }

        @Override
        public void fieldRemoved(FieldEditionEvent event) {
            setModified(ds.isModified());
        }

        @Override
        public void fieldModified(FieldEditionEvent event) {
            setModified(ds.isModified());
        }

        @Override
        public void open(DataSource ds) {
        }

        @Override
        public void cancel(DataSource ds) {
            setModified(ds.isModified());
        }

        @Override
        public void commit(DataSource ds) {
            setModified(ds.isModified());
        }
    }
}
