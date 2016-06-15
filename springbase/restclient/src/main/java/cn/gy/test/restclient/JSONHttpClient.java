package cn.gy.test.restclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import cn.gy.test.configs.constants.ConstantBasic;
import cn.gy.test.configs.fileutil.ConfigFileUtils;
import cn.gy.test.domains.dto.HttpResponseDTO;

/**
 * rest client,just support json format<br>
 * support http and https protocol
 * <br>
 * @author guyan
 *
 */
public class JSONHttpClient {

    private static final Logger logger = LoggerFactory
            .getLogger(JSONHttpClient.class);

    public static String URL = "";

    private static PoolingHttpClientConnectionManager pccm = null;

    private static RequestConfig defaultRequestConfig = null;

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

    private static Integer SOCKET_TIMEOUT = Integer
            .valueOf(ConfigFileUtils
                    .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "rest.socket.timeout"));

    private static Integer CONNECT_TIMEOUT = Integer
            .valueOf(ConfigFileUtils
                    .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "rest.connect.timeout"));

    private static Integer CONNECTION_REQUEST_TIMEOUT = Integer
            .valueOf(ConfigFileUtils
                    .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "rest.connection.request.timeout"));

    /**
     * each router less than 200
     */
    private static Integer CONNECTION_MAX = Integer
            .valueOf(ConfigFileUtils
                    .getPropertyValue(ConstantBasic.CONFIG_FILE[0], "rest.connection.max"));

    static {
        if (pccm == null) {
            SSLConnectionSocketFactory sslsf = buildSSLFactory();

            defaultRequestConfig = RequestConfig.custom()
                    .setSocketTimeout(SOCKET_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                    .setStaleConnectionCheckEnabled(true)
                    .build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", 
                            sslsf == null ? SSLConnectionSocketFactory.getSocketFactory() : sslsf)
                    .build();
            pccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            pccm.setMaxTotal(CONNECTION_MAX);
            pccm.setDefaultMaxPerRoute(pccm.getMaxTotal());

        }
    }
    
    /**
     * Build SSLConnectionSocketFactory by keystore file.
     * Set protocol and hostname verifier
     * @return
     */
    public static SSLConnectionSocketFactory buildSSLFactory() {
        try (// load truststore
                FileInputStream instream = new FileInputStream(
                        new File(KEYSTORE_PATH));) {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(instream, KEYSTORE_PASS.toCharArray());
            
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(trustStore)
                    .build();
            return new SSLConnectionSocketFactory(sslcontext,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            /*
             * sslsf = new SSLConnectionSocketFactory( sslcontext, new
             * String[]{"SSLv3"}, new String[]{"SSL"},
             * SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
             */
        } catch (Exception e) {
            logger.error("Fail to load keystore", e);
        }
        return null;
    }

    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(pccm)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
        return httpClient;
    }

    /**
     * post rest
     * @param url full rest url
     * @param json request body
     * @return
     */
    public static HttpResponseDTO postRequest (String url, String json) {
        CloseableHttpClient httpClient = null;
        // do not close response using response.close, it will close automaticly
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(URL + url);
            if (StringUtils.isNotBlank(json)) {
                StringEntity requestEntity = new StringEntity(json, 
                        ConstantBasic.DEFAULT_ENCODING);
                requestEntity.setContentType(
                        MediaType.APPLICATION_JSON_UTF8_VALUE);
                post.setEntity(requestEntity);
            }

            post.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);
            post.addHeader("Accept-Charset", ConstantBasic.DEFAULT_ENCODING);

            httpClient = getHttpClient();
            response = httpClient.execute(post);
        } catch (Exception e) {
            logger.error("error occurs when post reuqest for {}", url, e);
        }

        int statusCode = response.getStatusLine().getStatusCode();

        StringBuffer sbf = new StringBuffer("");
        try (BufferedReader br = new 
                BufferedReader(new 
                        InputStreamReader(response.getEntity().getContent()))) {

            String strTemp = null;
            while ((strTemp = br.readLine()) != null) {
                sbf.append(strTemp);
            }
        } catch (Exception e) {
            logger.error("parse response body fail", e);
        }

        return 
                new HttpResponseDTO(String.valueOf(statusCode), sbf.toString());
    }
    
}
