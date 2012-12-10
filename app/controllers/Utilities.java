package controllers;

import com.notnoop.exceptions.InvalidSSLConfig;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;

public class Utilities {


    public static SSLContext newSSLContext(InputStream cert, String password, String ksType, String ksAlgorithm) throws InvalidSSLConfig {
        try {
            KeyStore ks = KeyStore.getInstance(ksType);
            ks.load(cert, password.toCharArray());

            // Get a KeyManager and initialize it
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(ksAlgorithm);
            kmf.init(ks, password.toCharArray());

            // Get a TrustManagerFactory and init with KeyStore
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(ksAlgorithm);
            tmf.init(ks);

            // Get the SSLContext to help create SSLSocketFactory
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(kmf.getKeyManagers(), null, null);
            return sslc;
        } catch (Exception e) {
            throw new InvalidSSLConfig(e);
        }
    }
}
