package com.burukeyou.uniapi.http.support;

import lombok.Getter;

import java.io.File;
import java.io.InputStream;

/**
 * File Info
 * @author  caihzihao
 */
@Getter
public class HttpFile {

    private String fileName;
    private Object file;

    public HttpFile() {
    }

    public HttpFile(String fileName, byte[] file) {
        this.fileName = fileName;
        this.file = file;
    }

    public HttpFile(String fileName, InputStream file) {
        this.fileName = fileName;
        this.file = file;
    }

    public HttpFile(String fileName, File file) {
        this.fileName = fileName;
        this.file = file;
    }

    public void setFile(InputStream file) {
        this.file = file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
