package cn.gy.test.restclient;

import java.io.File;
import java.io.FileInputStream;
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

/**
 * rest client,just support json format<br>
 * support http and https protocol
 * 
 * @author guyan
 *
 */
public class JSONHttpClientUtil {
    public static String URL = "";
    private static PoolingHttpClientConnectionManager pccm = null;
    private static RequestConfig defaultRequestConfig = null;

    static {
        if (pccm == null) {
            SSLConnectionSocketFactory sslsf = null;
            try {
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                // 加载证书文件
                FileInputStream instream = new FileInputStream(new File(System.getProperty("user.dir")
                        + "/src/main/resources/example.com.jks"));
                try {
                    trustStore.load(instream, "keystorepassword".toCharArray());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    instream.close();
                }
                SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStore).build();
                sslsf = new SSLConnectionSocketFactory(sslcontext,
                        SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                /*sslsf = new SSLConnectionSocketFactory(
                      sslcontext, new String[]{"SSLv3"}, new String[]{"SSL"},
                      SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
            defaultRequestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000)
                    .setConnectionRequestTimeout(3000).setStaleConnectionCheckEnabled(true).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf == null ? SSLConnectionSocketFactory.getSocketFactory() : sslsf).build();
            pccm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            pccm.setMaxTotal(10);
            pccm.setDefaultMaxPerRoute(pccm.getMaxTotal());

        }
    }

    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(pccm)
                .setDefaultRequestConfig(defaultRequestConfig).build();
        return httpClient;
    }

    public static int httpMigRequest(String url, String json) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            HttpPost post = new HttpPost(URL + url);
            if (StringUtils.isNotBlank(json)) {
                StringEntity requestEntity = new StringEntity(json, "UTF-8");
                requestEntity.setContentType("application/json;charset=UTF-8");
                post.setEntity(requestEntity);
            } else {
                return -1;
            }
            httpClient = getHttpClient();
            response = httpClient.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
