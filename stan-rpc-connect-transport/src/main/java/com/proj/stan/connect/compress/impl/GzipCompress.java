package com.proj.stan.connect.compress.impl;

import com.proj.stan.connect.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @Author: stan
 * @Date: 2021/02/19
 * @Description:
 */
public class GzipCompress implements Compress {

    private static final int BUFFER_CAPACITY = 4 * 1024;


    @Override
    public byte[] compress(byte[] beforeCompress) {
        if (beforeCompress == null) {
            throw new NullPointerException("compress data is null");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream)) {
            gzipOutputStream.write(beforeCompress);
            gzipOutputStream.flush();
            gzipOutputStream.finish();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("gip compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] afterCompress) {
        if (afterCompress == null) {
            throw new NullPointerException("decompress data is null");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(
                new ByteArrayInputStream(afterCompress))) {
            byte[] buffer = new byte[BUFFER_CAPACITY];
            int n;
            while ((n = gzipInputStream.read(buffer)) > -1) {
                outputStream.write(buffer, 0, n);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("gip decompress error", e);
        }
    }
}
