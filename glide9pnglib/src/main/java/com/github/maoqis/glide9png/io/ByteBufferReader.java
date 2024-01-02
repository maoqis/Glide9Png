package com.github.maoqis.glide9png.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteBufferReader implements Reader {
    protected final ByteBuffer byteBuffer;

    public ByteBufferReader(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
        byteBuffer.order(ByteOrder.BIG_ENDIAN);
    }

    @Override
    public int getUInt32() throws IOException {
        return (getByte() << 24 & 0xFF000000) | (getByte() << 16 & 0xFF0000) | (getByte() << 8 & 0xFF00) | (getByte() & 0xFF);
    }

    public byte[] readByteArray(int length) throws IOException {
        byte[] array = new byte[length];
        int read = read(array, length);
        return array;
    }


    @Override
    public int getUInt16() throws IOException {
        return (getByte() << 8 & 0xFF00) | (getByte() & 0xFF);
    }

    @Override
    public short getUInt8() throws IOException {
        return (short) (getByte() & 0xFF);
    }

    @Override
    public long skip(long total) throws IOException {
        int toSkip = (int) Math.min(byteBuffer.remaining(), total);
        byteBuffer.position(byteBuffer.position() + toSkip);
        return toSkip;
    }

    @Override
    public int read(byte[] buffer, int byteCount) throws IOException {
        int toRead = Math.min(byteCount, byteBuffer.remaining());
        if (toRead == 0) {
            return -1;
        }
        byteBuffer.get(buffer, 0 /*dstOffset*/, toRead);
        return toRead;
    }

    @Override
    public int getByte() throws IOException {
        if (byteBuffer.remaining() < 1) {
            return -1;
        }
        return byteBuffer.get();
    }


}