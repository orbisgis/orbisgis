/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import biz.source_code.base64Coder.Base64Coder;

import org.gdms.driver.DriverException;

public final class DigestUtilities {

        public static byte[] getDigest(DataSource ds)
                throws NoSuchAlgorithmException, DriverException {
                return getDigest(ds, ds.getRowCount());
        }

        public static boolean equals(byte[] digest1, byte[] digest2) {
                if (digest1.length != digest2.length) {
                        return false;
                }
                for (int i = 0; i < digest2.length; i++) {
                        if (digest1[i] != digest2[i]) {
                                return false;
                        }
                }

                return true;
        }

        public static byte[] getDigest(DataSource ds,
                long rowCount)
                throws DriverException, NoSuchAlgorithmException {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                for (int i = 0; i < rowCount; i++) {
                        for (int j = 0; j < ds.getFieldCount(); j++) {
                                md5.update(ds.getFieldValue(i, j).getBytes());
                        }
                }
                return md5.digest();
        }

        public static String getBase64Digest(DataSource ds)
                throws NoSuchAlgorithmException, DriverException {
                return String.valueOf(Base64Coder.encode(getDigest(ds)));
        }

        private DigestUtilities() {
        }
}
