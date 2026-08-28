/*
 *  Copyright (C) 2022 github.com/REAndroid
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.reandroid.utils;

import com.reandroid.utils.io.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Crc32 extends Checksum {

    private final java.util.zip.CRC32 mCrc;
    private long mLength;

    public Crc32() {
        super();
        this.mCrc = new java.util.zip.CRC32();
    }

    @Override
    public long getValue() {
        return mCrc.getValue();
    }

    public long getLength() {
        return mLength;
    }

    @Override
    public void reset() {
        this.mCrc.reset();
        this.mLength = 0;
    }

    @Override
    public void update(byte[] data, int offset, int length) {
        mCrc.update(data, offset, length);
        this.mLength += length;
    }

    public static long of(byte[] bytes) {
        return of(bytes, 0, bytes.length);
    }
    public static long of(byte[] bytes, int offset, int length) {
        Crc32 crc32 = new Crc32();
        crc32.update(bytes, offset, length);
        return crc32.getValue();
    }
    public static long of(File file) throws IOException {
        if (!file.isFile()) {
            throw new FileNotFoundException("No such file: " + file);
        }
        long length = file.length();
        long fiftyMega = 50L * 1024 * 1024;
        if (length > fiftyMega) {
            length = -1;
        }
        return of((int) length, FileUtil.inputStream(file));
    }
    public static long of(InputStream stream) throws IOException {
        return of(-1, stream);
    }
    public static long of(int bufferSize, InputStream stream) throws IOException {
        if ((bufferSize & 0xff000000) != 0) {
            bufferSize = 4 * 1024 * 1024; // 4MB
        }
        byte[] buffer = new byte[bufferSize];
        int length;
        Crc32 crc32 = new Crc32();
        while ((length = stream.read(buffer, 0, bufferSize)) != -1) {
            crc32.update(buffer, 0, length);
        }
        stream.close();
        return crc32.getValue();
    }
}
