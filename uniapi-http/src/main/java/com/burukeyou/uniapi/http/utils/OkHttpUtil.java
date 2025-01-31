package com.burukeyou.uniapi.http.utils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.io.InputStream;

public class OkHttpUtil {

    /**
     * Returns a new request body that transmits the content of {@code file}.
     */
    public static RequestBody create(final MediaType contentType, final InputStream inputStream) {
        if (inputStream == null) throw new NullPointerException("inputStream == null");
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return -1;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                try (Source source = Okio.source(inputStream)) {
                    sink.writeAll(source);
                }
            }
        };
    }

}
