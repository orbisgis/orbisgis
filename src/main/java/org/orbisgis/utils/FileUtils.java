/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Contains some utility functions for dealing with files.
 */
public final class FileUtils {

        private static final int BUF_SIZE = 1024 * 64;

        /**
         * Copies the specified folder in the destination folder
         *
         * @param sourceDir
         * @param destDir
         * @throws IOException
         */
        public static void copyDirsRecursively(File sourceDir, File destDir)
                throws IOException {
                File[] sourceChildren = sourceDir.listFiles();
                for (File file : sourceChildren) {
                        if (file.isDirectory()) {
                                File childDir = new File(destDir, file.getName());
                                if (!childDir.exists() && !childDir.mkdirs()) {
                                        throw new IOException("Cannot create: " + childDir);
                                }
                                copyDirsRecursively(file, childDir);
                        } else {
                                copyFileToDirectory(file, destDir);
                        }
                }
        }

        /**
         * Deletes a file if exists
         *
         * @param file
         * @return true iif the file vas deleted
         */
        public static boolean deleteFile(File file) {
                if (file != null && file.exists()) {
                        return file.delete();
                }
                return false;
        }

        /**
         * A simple method to delete all shape files : dbf, shp, shx, prj.
         *
         * @param fileShp
         * @throws IOException 
         */
        public static void deleteSHPFiles(File fileShp) throws IOException {
                File fileShx = getFileWithExtension(fileShp, "shx");
                File fileDbf = getFileWithExtension(fileShp, "dbf");
                File filePrj = getFileWithExtension(fileShp, "prj");

                deleteFile(fileShp);
                deleteFile(fileShx);
                deleteFile(fileDbf);
                deleteFile(filePrj);
        }

        /**
         * Delete all the files in the directory.
         * This method takes care to remove all the files in the child directories
         * (recursively) before removing the directories.
         * This method does not remove the parameter File dir.
         * @param dir
         * @return
         *      false is a problem is encountered, true otherwise
         */
        public static boolean deleteFileInDir(File dir) {
                if (dir.isDirectory()) {
                        // we iterate over the children
                        String[] children = dir.list();
                        for (int i = 0; i < children.length; i++) {
                                final File file = new File(dir, children[i]);
                                // we try to delete the inner files/dirs
                                if (deleteFileInDir(file)) {
                                        // then we try to delete the file/folder
                                        // it it was a file, it does not exist anymore
                                        // else (directory) we delete it
                                        if (file.exists() && !file.delete()) {
                                                // failed to delete the file/folder
                                                return false;
                                        }
                                } else {
                                        // failed to delete the inner files/dirs
                                        return false;
                                }
                        }
                } else {
                        // it is a file, let's delete it
                        if (!dir.delete()) {
                                // failed to delete the file
                                return false;
                        }
                }

                // everything happened ok.
                return true;
        }

        /**
         * Delete a directory and its contents
         * @param dir
         * @return
         *      true if successful, false otherwise.
         */
        public static boolean deleteDir(File dir) {
                return deleteFileInDir(dir) && dir.delete();
        }

        /**
         * Copy file in the directory destDir. If destDir doesn't exist, it will be created.
         * @param file
         *          The file to  be copied
         * @param destDir
         *          the destination directory.
         * @throws IOException
         *          If destDir does not exist and can't be created.
         */
        public static void copyFileToDirectory(File file, File destDir)
                throws IOException {
                if (!destDir.exists() && !destDir.mkdirs()) {
                        throw new IOException("Cannot create directory: " + destDir);
                }

                File output = new File(destDir, file.getName());
                copy(file, output);
        }

        /**
         * Copy the content of input in output.
         * @param input
         * @param output
         * @return
         * @throws IOException
         *          If there is a problem while copying the file.
         */
        public static long copy(File input, File output) throws IOException {
                FileInputStream in = null;
                try {
                        in = new FileInputStream(input);
                        return copy(in, output);
                } finally {
                        if (in != null) {
                                in.close();
                        }
                }
        }

        /**
         * Copy input into output, using a given Buffer to transmit the data.
         * @param input
         * @param output
         * @param copyBuffer
         * @return
         * @throws IOException
         *          If a problem is encountered during the copy.
         */
        public static long copy(File input, File output, byte[] copyBuffer)
                throws IOException {
                FileInputStream in = null;
                FileOutputStream out = null;
                try {
                        try {
                                in = new FileInputStream(input);
                                out = new FileOutputStream(output);
                                return copy(in, out, copyBuffer);
                        } finally {
                                if (in != null) {
                                        in.close();
                                }
                        }
                } finally {
                        if (out != null) {
                                out.close();
                        }
                }
        }

        /**
         * Copy the content of in in the outputFile.
         * @param in
         * @param outputFile
         * @return
         * @throws IOException
         *      If a probem is encountered during the copy.
         */
        public static long copy(InputStream in, File outputFile) throws IOException {
                FileOutputStream out = null;
                try {
                        out = new FileOutputStream(outputFile);
                        return copy(in, out);
                } finally {
                        if (out != null) {
                                out.close();
                        }
                }
        }

        /**
         * Copy the stream in to the stream out
         * @param in
         * @param out
         * @return
         * @throws IOException
         *      If a problem is encountered during the copy.
         */
        public static long copy(InputStream in, OutputStream out)
                throws IOException {
                byte[] buf = new byte[BUF_SIZE];
                return copy(in, out, buf);
        }

        /**
         * Copy the stream in to the stream out, given an intermediate copy buffer
         * @param in
         * @param out
         * @param copyBuffer
         * @return
         * @throws IOException
         *      If a problem is encountered during the copy.
         */
        public static long copy(InputStream in, OutputStream out, byte[] copyBuffer)
                throws IOException {
                long bytesCopied = 0;
                int read = in.read(copyBuffer, 0, copyBuffer.length);

                while (read != -1) {
                        out.write(copyBuffer, 0, read);
                        bytesCopied += read;
                        read = in.read(copyBuffer, 0, copyBuffer.length);
                }
                return bytesCopied;
        }

        /**
         * Retrieve a file pointed by url, store it in file.
         * @param url
         * @param file
         * @throws IOException
         *          If a problem is encountered while downloading the file.
         */
        public static void download(URL url, File file) throws IOException {
                OutputStream out = null;
                InputStream in = null;
                try {
                        try {
                                out = new BufferedOutputStream(new FileOutputStream(file));
                                URLConnection conn = url.openConnection();
                                in = conn.getInputStream();
                                byte[] buffer = new byte[BUF_SIZE];
                                int numRead = in.read(buffer);
                                while (numRead != -1) {
                                        out.write(buffer, 0, numRead);
                                        numRead = in.read(buffer);
                                }
                        } finally {
                                if (in != null) {
                                        in.close();
                                }
                        }
                } finally {
                        if (out != null) {
                                out.close();
                        }
                }
        }

        /**
         * Zips the specified file or folder
         *
         * @param toZip
         * @param outFile
         * @throws IOException
         */
        public static void zip(File toZip, File outFile) throws IOException {
                ZipOutputStream out = null;
                try {
                        out = new ZipOutputStream(new BufferedOutputStream(
                                new FileOutputStream(outFile)));

                        byte[] data = new byte[BUF_SIZE];
                        ArrayList<File> listToZip = new ArrayList<File>();
                        listToZip.add(toZip);

                        while (listToZip.size() > 0) {
                                File file = listToZip.remove(0);
                                if (file.isDirectory()) {
                                        File[] children = file.listFiles();
                                        listToZip.addAll(Arrays.asList(children));
                                } else {
                                        BufferedInputStream in = null;
                                        try {
                                                in = new BufferedInputStream(new FileInputStream(file),
                                                        BUF_SIZE);

                                                out.putNextEntry(new ZipEntry(getRelativePath(toZip, file)));
                                                int count = in.read(data, 0, BUF_SIZE);
                                                while (count != -1) {
                                                        out.write(data, 0, count);
                                                        count = in.read(data, 0, BUF_SIZE);
                                                }
                                                out.closeEntry(); // close each entry
                                        } finally {
                                                if (in != null) {
                                                        in.close();
                                                }
                                        }
                                }
                        }
                        out.flush();
                } finally {
                        if (out != null) {
                                out.close();
                        }
                }
        }

        /**
         * Unzip an archive into the directory destDir.
         * @param zipFile
         * @param destDir
         * @throws IOException
         */
        public static void unzip(File zipFile, File destDir) throws IOException {
                ZipInputStream zis = null;

                try {
                        FileInputStream fis = new FileInputStream(zipFile);
                        zis = new ZipInputStream(new BufferedInputStream(fis));
                        ZipEntry entry = zis.getNextEntry();
                        while (entry != null) {
                                byte data[] = new byte[BUF_SIZE];
                                // write the files to the disk
                                File newFile = new File(destDir, entry.getName());
                                File parentFile = newFile.getParentFile();
                                if (!parentFile.exists() && !parentFile.mkdirs()) {
                                        throw new IOException("Cannot create directory:" + parentFile);
                                }
                                if (!entry.isDirectory()) {
                                        BufferedOutputStream dest = null;
                                        try {
                                                FileOutputStream fos = new FileOutputStream(newFile);
                                                dest = new BufferedOutputStream(fos, BUF_SIZE);
                                                int count = zis.read(data, 0, BUF_SIZE);
                                                while (count != -1) {
                                                        dest.write(data, 0, count);
                                                        count = zis.read(data, 0, BUF_SIZE);
                                                }
                                                dest.flush();
                                        } finally {
                                                if (dest != null) {
                                                        dest.close();
                                                }
                                        }
                                }
                                entry = zis.getNextEntry();
                        }
                } finally {
                        if (zis != null) {
                                zis.close();
                        }
                }
        }

        /**
         * get the relative path to file,  according to the path to base
         * @param base
         * @param file
         * @return
         */
        public static String getRelativePath(File base, File file) {
                String absolutePath = file.getAbsolutePath();
                String path = absolutePath.substring(base.getAbsolutePath().length());
                while (path.startsWith("/")) {
                        path = path.substring(1);
                }
                return path;
        }

        /**
         * Retrieve the content of the file as an array of bytes.
         * @param file
         * @return
         * @throws IOException
         */
        public static byte[] getContent(File file) throws IOException {
                FileInputStream fis = new FileInputStream(file);
                return getContent(fis);
        }

        /**
         * Retrieve the content of the Inputstream as an array of bytes.
         * @param fis
         * @return
         * @throws IOException
         */
        public static byte[] getContent(InputStream fis) throws IOException {
                DataInputStream dis = null;
                byte[] buffer;
                try {
                        dis = new DataInputStream(fis);
                        buffer = new byte[dis.available()];
                        dis.readFully(buffer);

                } finally {
                        if (dis != null) {
                                dis.close();
                        }
                }
                return buffer;
        }

        /**
         * compute the MD5 sum of the file.
         * @param file
         * @return
         * @throws IOException
         *      if there is a problem while computing the file
         * @throws NoSuchAlgorithmException
         *      if the MD5 algorithm can't be found.
         */
        public static byte[] getMD5(File file) throws IOException, NoSuchAlgorithmException {
                byte[] content = getContent(file);
                MessageDigest m = MessageDigest.getInstance("MD5");
                m.update(content, 0, content.length);
                return m.digest();
        }

        /**
         * Transform an array of bytes to a hexadecimal String
         * @param messageDigest
         * @return
         */
        public static String toHexString(byte[] messageDigest) {
                StringBuilder hexString = new StringBuilder();
                for (int i = 0; i < messageDigest.length; i++) {
                        hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                }

                return hexString.toString();
        }

        /**
         * Write content into file
         * @param file
         * @param content
         * @throws IOException
         */
        public static void setContents(File file, String content)
                throws IOException {
                BufferedOutputStream bos = null;
                try {
                        FileOutputStream fos = new FileOutputStream(file);
                        bos = new BufferedOutputStream(fos);
                        bos.write(content.getBytes());
                } finally {
                        if (bos != null) {
                                bos.close();
                        }
                }
        }

        /**
         * Get the name of a filie without its extension.
         * @param file
         * @return
         */
        public static String getFileNameWithoutExtensionU(File file) {
                String name = file.getName();
                int extensionStart = name.lastIndexOf('.');
                String ret = name;
                if (extensionStart != -1) {
                        ret = name.substring(0, name.indexOf(name.substring(extensionStart)));
                }

                return ret;
        }

        /**
         * Get a file according an extension
         *
         * @param file
         * @param extension
         * @return
         * @throws IOException
         */
        public static File getFileWithExtension(File file, final String extension)
                throws IOException {
                if (!file.isDirectory()) {
                        final String ret = FileUtils.getFileNameWithoutExtensionU(file);
                        File[] files = file.getParentFile().listFiles(new FilenameFilter() {

                                @Override
                                public boolean accept(File arg0, String name) {

                                        if (name.toLowerCase().endsWith(extension)) {
                                                int extensionStart = name.lastIndexOf('.');
                                                if (extensionStart == -1) {
                                                        return false;
                                                }
                                                String ret2 = name.substring(0, extensionStart);
                                                return ret2.equals(ret);
                                        }
                                        return false;

                                }
                        });
                        if (files.length > 0) {
                                return new File(files[0].getAbsolutePath());
                        } else {
                                return null;
                        }
                } else {
                        throw new IOException(file.getAbsolutePath() + " is a directory");
                }
        }
        
        /**
         * Gets a convenient name from an URI.
         * 
         * The URI scheme can be file or anything with a 'tablename' querystring parameter.
         * Anything else is unsupported.
         * 
         * @param u an URI
         * @return a name for it
         * @throws UnsupportedOperationException if the URI is unsupported.
         */
        public static String getNameFromURI(URI u) {
                if ("file".equalsIgnoreCase(u.getScheme())) {
                        return getFileNameWithoutExtensionU(new File(u.getPath()));
                } else {
                        String q = u.getQuery();
                        if (q != null && !q.isEmpty()) {
                                String[] pat = q.split("&");
                                for (int i = 0; i < pat.length; i++) {
                                        if (pat[i].toLowerCase().startsWith("tablename=")) {
                                                return pat[i].toLowerCase().substring(10);
                                        }
                                }
                        }
                }
                
                throw new UnsupportedOperationException();
        }

        private FileUtils() {
        }
}
