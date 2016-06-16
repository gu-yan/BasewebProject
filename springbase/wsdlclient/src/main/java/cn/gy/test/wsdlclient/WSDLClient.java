package cn.gy.test.wsdlclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.gy.test.configs.constants.ConstantBasic;
import cn.gy.test.configs.fileutil.ConfigFileUtils;

/**
 * wsdl client.Call webservic.
 * @author guyan
 *
 */
public class WSDLClient {

    private static final Logger logger = LoggerFactory
            .getLogger(WSDLClient.class);

    /**
     * keystore password
     */
    private static final String KEYSTORE_PASS = ConfigFileUtils
            .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "keystore.pass");
    
    /**
     * path place keystore,it is a relative path<br>
     * the realpath will be System.getProperty("user.dir") + KEYSTORE_PATH
     */
    private static final String KEYSTORE_PATH = System.getProperty("user.dir") + File.pathSeparator
            + ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "keystore.path");

    /**
     * truststore password
     */
    private static final String TRUSTSTORE_PASS = ConfigFileUtils
            .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "trusttore.pass");
    
    /**
     * path place truststore,it is a relative path<br>
     * the realpath will be System.getProperty("user.dir") + TRUSTSTORE_PATH
     */
    private static final String TRUSTSTORE_PATH = System.getProperty("user.dir") + File.pathSeparator
            + ConfigFileUtils.getPropertyValue(ConstantBasic.CONFIG_FILE[0], "trusttore.path");

    private static Integer CONNECT_TIMEOUT = Integer
            .valueOf(ConfigFileUtils
                    .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "wsdl.connect.timeout"));
    
    private static KeyManager[] loadKeyManager() 
            throws NoSuchAlgorithmException, KeyStoreException, 
            UnrecoverableKeyException {
        //init key store manage factory with default algorithm
        KeyManagerFactory keyManager = KeyManagerFactory.
                getInstance(KeyManagerFactory.getDefaultAlgorithm());
        //get keystore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        //load keystore file
        try (FileInputStream instream = new FileInputStream(
                new File(KEYSTORE_PATH))) {
            keyStore.load(instream, KEYSTORE_PASS.toCharArray());
        } catch (IOException | CertificateException e) {
            logger.error("load keystore fail", e);
        }
        keyManager.init(keyStore, KEYSTORE_PASS.toCharArray());
        return keyManager.getKeyManagers();
    }
    
    private static TrustManager[] loadTrustManager() 
            throws NoSuchAlgorithmException, KeyStoreException {
        ////init trust store manage factory with default algorithm
        TrustManagerFactory trustManager = TrustManagerFactory.
                getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream instream = new FileInputStream(
                new File(TRUSTSTORE_PATH))) {
            trustStore.load(instream, TRUSTSTORE_PASS.toCharArray());
        } catch (IOException | CertificateException e) {
            logger.error("load truststore fail", e);
        }
        trustManager.init(trustStore);
        return trustManager.getTrustManagers();
    }
    
    private static SSLSocketFactory getSSLSocketFactory() 
            throws KeyManagementException, UnrecoverableKeyException,
            NoSuchAlgorithmException, KeyStoreException {
        SSLContext sslcontext = SSLContext.getInstance("TLS");
//      sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
        sslcontext.init(loadKeyManager(), loadTrustManager(), new SecureRandom());
        return sslcontext.getSocketFactory();
    }
    
    public static StringBuffer callWSDL(String url, String content) 
            throws IOException, KeyManagementException, 
            UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        URL callUrl = new URL(url);
        HttpsURLConnection https = (HttpsURLConnection)callUrl.openConnection();
        https.setSSLSocketFactory(getSSLSocketFactory());
        https.setConnectTimeout(CONNECT_TIMEOUT);
        https.setRequestMethod("POST");
        https.setDoInput(true);
        https.setDoOutput(true);
        https.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        byte[] bs = content.getBytes();
        https.setRequestProperty("Content-Length", String.valueOf(bs.length));
        
        OutputStream out = https.getOutputStream();
        out.write(bs);
        out.close();
        
        try (InputStreamReader isr = new InputStreamReader(https.getInputStream());
              BufferedReader br = new BufferedReader(isr);) {
            String responseStr = null;
            StringBuffer sbf = new StringBuffer();
            while ((responseStr = br.readLine()) != null) {
                sbf.append(responseStr);
            }
            return sbf;
        } catch (IOException e) {
            logger.error("read response fail", e);
        }
        return new StringBuffer();
    }
}
