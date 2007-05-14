/*
 * Created on 11-jul-2005
 *
 * gvSIG. Sistema de Informacin Geogrfica de la Generalitat Valenciana
 * 
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 * 
 *    or
 * 
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 * 
 *   +34 963163400
 *   dac@iver.es
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

/**
 * @author Fjp
 *
 * Clase para trabajar con ficheros grandes. Mapea solo un trozo
 * de fichero en memoria, y cuando intentas acceder fuera de esa
 * zona, se ocupa de leer automticamente el trozo que le falta.
 */
public class BigByteBuffer2 {

    private static long DEFAULT_SIZE = 8*1024; // 8 Kbytes
    
    ByteBuffer bb;
    // byte[] buff = new byte[1024 * 1024];;
    FileChannel fc;
    long minAbs, maxAbs, posAbs;
    int minRel,  maxRel, posRel;
    long sizeChunk, amountMem;
    long fileSize;
    FileChannel.MapMode mode;
    
    /**
     * Revisa la posicin absoluta, y si hace falta, carga el buffer
     * con la parte de fichero que toca.
     * @throws IOException 
     */
    private synchronized void prepareBuffer(long posActual, int numBytesToRead) 
    {
        long desiredPos = posActual + numBytesToRead;
        if ((desiredPos > maxAbs) || (posActual < minAbs))
        {
            // Quiero leer fuera:
            sizeChunk = Math.min(fileSize-posActual, amountMem);
            try {
                mapFrom(posActual);
                // System.out.println("BigByteBuffer: min=" + minAbs 
                //     + " maxAbs=" + maxAbs + " posAbs = " + posAbs);
                
            } catch (IOException e) {
                e.printStackTrace();
                
            }
                
        }
        // Dejamos posAbs apuntando a donde va a quedar
        // "a priori", antes de leer de verdad, que se hace
        // al salir de esta funcin.
        posAbs = desiredPos;
        
    }

    /**
     * @param posActual
     * @throws IOException
     */
    private synchronized ByteBuffer mapFrom(long newPos) throws IOException {
        ByteOrder lastOrder = bb.order();
        // bb = fc.map(mode, newPos, sizeChunk);
        fc.position(newPos);
        // bb = ByteBuffer.wrap(buff);
        // bb = ByteBuffer.allocate((int)sizeChunk);
        bb.position(0);
        int numRead = fc.read(bb);
        bb.position(0);
        // System.out.println("Mapeo desde " + newPos + " con sizeChunk= " + sizeChunk + " numRead = " + numRead);
        minAbs = newPos;
        maxAbs = sizeChunk + newPos;
        bb.order(lastOrder);
        return bb;
    }
    
    public BigByteBuffer2(FileChannel fc, FileChannel.MapMode mode, long amountMem) throws IOException
    {
        this.amountMem = amountMem;
        // this.buff = new byte[(int) amountMem];
        this.fc = fc;
        this.fileSize = fc.size();
        this.mode = mode;
        sizeChunk = Math.min(fc.size(), amountMem);
        // bb = fc.map(mode, 0L, sizeChunk);
        // bb = ByteBuffer.wrap(buff);
        bb = ByteBuffer.allocateDirect((int)sizeChunk);
        int numRead = fc.read(bb);
        bb.position(0);
        minAbs = 0;
        maxAbs = sizeChunk;
    }
    public BigByteBuffer2(FileChannel fc, FileChannel.MapMode mode) throws IOException
    {
        this.amountMem = DEFAULT_SIZE;
        this.fc = fc;
        this.fileSize = fc.size();
        this.mode = mode;
        sizeChunk = Math.min(fc.size(), amountMem);
        // bb = fc.map(mode, 0L, sizeChunk);
        bb = ByteBuffer.allocateDirect((int)sizeChunk);
        int numRead = fc.read(bb);
        bb.position(0);
        minAbs = 0;
        maxAbs = sizeChunk;
    } 

    
    public synchronized byte get() {
        prepareBuffer(posAbs,1);
        return bb.get();
    }
    public synchronized ByteBuffer get(byte[] dst)
    {
        prepareBuffer(posAbs, dst.length);
        return bb.get(dst);
    }

    public synchronized char getChar() {
        prepareBuffer(posAbs,2);
        return bb.getChar();
    }

    public synchronized double getDouble() {
        prepareBuffer(posAbs,8);
        return bb.getDouble();
    }

    public synchronized float getFloat() {
        prepareBuffer(posAbs,4);
        return bb.getFloat();
    }

    public synchronized int getInt() {
        prepareBuffer(posAbs,4);
        return bb.getInt();
    }

    public synchronized long getLong() {
        prepareBuffer(posAbs,8);
        return bb.getLong();
    }

    public synchronized short getShort() {
        prepareBuffer(posAbs,2);
        return bb.getShort();
    }

    public boolean isDirect() {
        return bb.isDirect();
    }

    public synchronized byte get(int index) {
        prepareBuffer(index,1);
        return bb.get(index - (int) minAbs);
    }

    public synchronized char getChar(int index) {
        prepareBuffer(index,2);
        return bb.getChar(index - (int) minAbs);
    }

    public synchronized double getDouble(int index) {
        prepareBuffer(index,8);
        return bb.getDouble(index - (int) minAbs);
    }

    public synchronized float getFloat(int index) {
        prepareBuffer(index,4);
        return bb.getFloat(index - (int) minAbs);
    }

    public synchronized int getInt(int index) {
        prepareBuffer(index,4);
        return bb.getInt(index - (int) minAbs);
    }

    public synchronized long getLong(int index) {
        prepareBuffer(index,8);
        return bb.getLong(index - (int) minAbs);
    }

    public synchronized short getShort(int index) {
        prepareBuffer(index,2);
        return bb.getShort(index - (int) minAbs);
    }

    public ByteBuffer asReadOnlyBuffer() { 
        return bb.asReadOnlyBuffer();
    }

    public ByteBuffer compact() {
        return bb.compact();
    }

    public ByteBuffer duplicate() {
        return bb.duplicate();
    }

    public synchronized ByteBuffer slice() {
        return bb.slice();
    }

    public synchronized ByteBuffer put(byte b) {
        prepareBuffer(posAbs,1);
        return bb.put(b);
    }

    public synchronized ByteBuffer putChar(char value) {
        prepareBuffer(posAbs,2);
        return bb.putChar(value);
    }

    public synchronized ByteBuffer putDouble(double value) {
        prepareBuffer(posAbs,8);
        return bb.putDouble(value);
    }

    public synchronized ByteBuffer putFloat(float value) {
        prepareBuffer(posAbs,4);
        return bb.putFloat(value);
    }

    public synchronized ByteBuffer putInt(int value) {
        prepareBuffer(posAbs,4);
        return bb.putInt(value);
    }

    public synchronized ByteBuffer put(int index, byte b) {
        prepareBuffer(index,1);
        return bb.put(index- (int) minAbs, b);
    }

    public synchronized ByteBuffer putChar(int index, char value) {
        prepareBuffer(index,2);
        return bb.putChar(index- (int) minAbs, value);
    }

    public synchronized ByteBuffer putDouble(int index, double value) {
        prepareBuffer(index,8);
        return bb.putDouble(index- (int) minAbs, value);
    }

    public synchronized ByteBuffer putFloat(int index, float value) {
        prepareBuffer(index,4);
        return bb.putFloat(index- (int) minAbs, value);
    }

    public synchronized ByteBuffer putInt(int index, int value) {
        prepareBuffer(index,4);
        return bb.putInt(index- (int) minAbs, value);
    }

    public synchronized ByteBuffer putLong(int index, long value) {
        prepareBuffer(index,8);
        return bb.putLong(index- (int) minAbs, value);
    }

    public synchronized ByteBuffer putShort(int index, short value) {
        prepareBuffer(index,2);
        return bb.putShort(index- (int) minAbs, value);
    }

    public synchronized ByteBuffer putLong(long value) {
        prepareBuffer(posAbs,8);
        return bb.putLong(value);
    }

    public synchronized ByteBuffer putShort(short value) {
        prepareBuffer(posAbs,2);
        return bb.putShort(value);
    }

    public CharBuffer asCharBuffer() {
        return bb.asCharBuffer();
    }

    public DoubleBuffer asDoubleBuffer() {
        return bb.asDoubleBuffer();
    }

    public FloatBuffer asFloatBuffer() {
        return bb.asFloatBuffer();
    }

    public IntBuffer asIntBuffer() {
        return bb.asIntBuffer();
    }

    public LongBuffer asLongBuffer() {
        return bb.asLongBuffer();
    }

    public ShortBuffer asShortBuffer() {
        return bb.asShortBuffer();
    }

    public boolean isReadOnly() {
        return bb.isReadOnly();
    }
    
    public synchronized final ByteOrder order()
    {
        return bb.order();
    }

    public synchronized final ByteBuffer order(ByteOrder bo)
    {
        return bb.order(bo);
    }
    public synchronized final long position()
    {
        return posAbs;
    }
    
    public synchronized final Buffer position(long newPosition)
    {
        prepareBuffer(newPosition,0);
        int relPos = (int) (newPosition - minAbs);
        if (relPos < 0)
            System.out.println("Position=" + newPosition);
        return bb.position(relPos);
    }

}
