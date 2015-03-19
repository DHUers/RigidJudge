package team.dhuacm.RigidJudge.utils;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * Created by wujy on 15-1-10.
 */
public class HttpClientUtil {

    private HttpClientUtil() {}

    public static CloseableHttpClient get(final int retryCount, int socketTimeout, int connectionTimeout, CredentialsProvider credentialsProvider) {

        System.setProperty("jsse.enableSNIExtension", "false");  // Resolve https issues

        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= retryCount) {
                    // Do not retry if over max retry count
                    return false;
                }
                if (exception instanceof InterruptedIOException) { // represent the number of milliseconds an operation may block for
                    // Timeout
                    return true;
                }
                if (exception instanceof UnknownHostException) {
                    // Unknown host
                    return false;
                }
                if (exception instanceof SSLException) {
                    // SSL handshake exception
                    return false;
                }
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                return !(request instanceof HttpEntityEnclosingRequest);
            }

        };

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout * 1000).setConnectTimeout(connectionTimeout * 1000).build();

        if (credentialsProvider != null) {
            return HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).setDefaultRequestConfig(requestConfig).setRetryHandler(myRetryHandler).build();
        } else {
            return HttpClients.custom().setDefaultRequestConfig(requestConfig).setRetryHandler(myRetryHandler).build();
        }
    }
}
