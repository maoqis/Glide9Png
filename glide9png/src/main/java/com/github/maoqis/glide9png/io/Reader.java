package com.github.maoqis.glide9png.io;

import java.io.IOException;

interface Reader {
    int getUInt32() throws IOException;

    int getUInt16() throws IOException;

    short getUInt8() throws IOException;

    long skip(long total) throws IOException;

    int read(byte[] buffer, int byteCount) throws IOException;

    int getByte() throws IOException;
}