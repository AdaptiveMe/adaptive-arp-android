package me.adaptive.arp.impl;

import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.FakeHttp;
import org.robolectric.tester.org.apache.http.FakeHttpLayer;
import org.robolectric.tester.org.apache.http.impl.client.DefaultRequestDirector;

import java.util.Arrays;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.IAdaptiveRPGroup;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IService;
import me.adaptive.arp.api.IServiceResultCallback;
import me.adaptive.arp.api.IServiceResultCallbackError;
import me.adaptive.arp.api.IServiceResultCallbackWarning;
import me.adaptive.arp.api.ServiceBridge;
import me.adaptive.arp.api.ServiceRequest;
import me.adaptive.arp.api.ServiceResponse;
import me.adaptive.arp.api.ServiceToken;
import me.adaptive.arp.impl.util.AbstractTest;
import me.adaptive.arp.impl.util.Utils;

/**
 * Test class for Service Delegate.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21)
public class ServiceDelegateTest extends AbstractTest<IService> {

    private DefaultRequestDirector requestDirector;
    private ConnectionKeepAliveStrategy connectionKeepAliveStrategy;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        FakeHttpLayer fakeHttpLayer = FakeHttp.getFakeHttpLayer();
        Assert.assertFalse(fakeHttpLayer.hasPendingResponses());
        Assert.assertFalse(fakeHttpLayer.hasRequestInfos());
        Assert.assertFalse(fakeHttpLayer.hasResponseRules());

        connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                return 0;
            }
        };
        requestDirector = new DefaultRequestDirector(new HttpRequestExecutor(), null, null, connectionKeepAliveStrategy, null, null, null, null, null, null, null, null);

    }

    @Test
    public void testInvokeService() throws Exception {

        ServiceToken serviceToken = bridge.getServiceTokenByUri("http://httpbin.org/user-agent");
        printServiceToken(serviceToken);

        ServiceRequest serviceRequest = bridge.getServiceRequest(serviceToken);
        printServiceToken(serviceRequest.getServiceToken());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "content: " + serviceRequest.getContent());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "content-type: " + serviceRequest.getContentType());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "referer-host: " + serviceRequest.getRefererHost());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "user-agent: " + serviceRequest.getUserAgent());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "body-parameters: " + Arrays.toString(serviceRequest.getBodyParameters()));
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "content-encoding: " + serviceRequest.getContentEncoding().toString());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "content-lenght: " + serviceRequest.getContentLength());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "query-parameters: " + Arrays.toString(serviceRequest.getQueryParameters()));
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "service-headers: " + Arrays.toString(serviceRequest.getServiceHeaders()));
        //Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "service-session: " + serviceRequest.getServiceSession().toString());

        String url = ((ServiceDelegate) ((ServiceBridge) bridge).getDelegate()).getURL(serviceRequest);
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "url: " + url);

        /*connectionKeepAliveStrategy = new ConnectionKeepAliveStrategy() {
            @Override public long getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                return 0;
            }
        };
        requestDirector = new DefaultRequestDirector(null, null, null, connectionKeepAliveStrategy, null, null, null, null, null, null, null, null);


        FakeHttp.addPendingHttpResponse(200, "{\"user-agent\": \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\"}");
        FakeHttpLayer fakeHttpLayer = FakeHttp.getFakeHttpLayer();
        Assert.assertFalse(fakeHttpLayer.hasPendingResponses());
        Assert.assertFalse(fakeHttpLayer.hasRequestInfos());
        Assert.assertFalse(fakeHttpLayer.hasResponseRules());

        FakeHttp.addHttpResponseRule(HttpGet.METHOD_NAME, url, new TestHttpResponse(200, "{\"user-agent\": \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\"}"));

        HttpResponse response = requestDirector.execute(null, new HttpGet(url), null);

        Assert.assertNotNull(response);
        Assert.assertTrue(response.getStatusLine().getStatusCode() == 200);
        //Assert.assertTrue(response.getEntity().getContent().isEqualTo("{\"user-agent\": \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.90 Safari/537.36\"}");

        //bridge.invokeService(serviceRequest, new ServiceResultCallback());*/
    }

    private void printServiceToken(ServiceToken serviceToken) {
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "service: " + serviceToken.getServiceName() + " endpoint: " + serviceToken.getEndpointName() +
                " function: " + serviceToken.getFunctionName() + " method: " + serviceToken.getInvocationMethod());
    }

    @Override
    public IService getBridge() {
        return AppRegistryBridge.getInstance().getServiceBridge();
    }

    private class ServiceResultCallback implements IServiceResultCallback {

        @Override
        public void onError(IServiceResultCallbackError error) {
            Utils.log(ILoggingLogLevel.Error, LOG_TAG, error.toString());
            Assert.assertNotNull(null);
        }

        @Override
        public void onResult(ServiceResponse response) {
            Utils.log(ILoggingLogLevel.Debug, LOG_TAG, response.getContent());
            Assert.assertNull(null);
        }

        @Override
        public void onWarning(ServiceResponse response, IServiceResultCallbackWarning warning) {
            Utils.log(ILoggingLogLevel.Warn, LOG_TAG, warning.toString());
            Utils.log(ILoggingLogLevel.Debug, LOG_TAG, response.getContent());
            Assert.assertNull(null);
        }

        @Override
        public IAdaptiveRPGroup getAPIGroup() {
            return IAdaptiveRPGroup.Communication;
        }

        @Override
        public String getAPIVersion() {
            return AppRegistryBridge.getInstance().getAPIVersion();
        }
    }
}
