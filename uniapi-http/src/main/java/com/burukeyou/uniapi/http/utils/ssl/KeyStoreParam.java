package com.burukeyou.uniapi.http.utils.ssl;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class KeyStoreParam implements Serializable {

    private String keyStore;

    /**
     * the password used to access the key store
     */
    private String keyStorePassword;

    /**
     * the alias that identifies the key in the key store.
     */
    private String keyAlias;

    /**
     * the password used to access the key in the key store.
     */
    private String keyPassword;

    /**
     * the type of the key store
     */
    private String keyStoreType;

    /**
     * the provider for the key store.
     */
    private String keyStoreProvider;

    public KeyStoreParam() {
    }


}
