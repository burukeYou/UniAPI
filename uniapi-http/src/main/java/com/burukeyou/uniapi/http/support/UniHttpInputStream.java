package com.burukeyou.uniapi.http.support;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author caizhihao
 */
public class UniHttpInputStream  extends InputStream {

    protected final InputStream inputStream;
    private final Closeable closeable;

    public UniHttpInputStream(Closeable response, InputStream inputStream) {
        this.inputStream = inputStream;
        this.closeable = response;
    }

    @Override
    public void close() throws IOException {
        try {
            inputStream.close();
        }finally {
            closeable.close();
        }
    }


    @Override
    public int read(byte[] b) throws IOException {
        return inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return inputStream.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return inputStream.skip(n);
    }

    @Override
    public int available() throws IOException {
        return inputStream.available();
    }



    @Override
    public synchronized void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

}