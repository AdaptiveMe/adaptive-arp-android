package me.adaptive.arp.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IService;
import me.adaptive.arp.api.ServiceBridge;
import me.adaptive.arp.api.ServiceRequest;
import me.adaptive.arp.api.ServiceToken;
import me.adaptive.arp.impl.util.AbstractTest;
import me.adaptive.arp.impl.util.Utils;

/**
 * Test class for Service Delegate.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21)
public class ServiceDelegateTest extends AbstractTest<IService> {

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

        Assert.assertEquals(url, "http://httpbin.org/user-agent");
    }

    private void printServiceToken(ServiceToken serviceToken) {
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, "service: " + serviceToken.getServiceName() + " endpoint: " + serviceToken.getEndpointName() +
                " function: " + serviceToken.getFunctionName() + " method: " + serviceToken.getInvocationMethod());
    }

    @Override
    public IService getBridge() {
        return AppRegistryBridge.getInstance().getServiceBridge();
    }
}
