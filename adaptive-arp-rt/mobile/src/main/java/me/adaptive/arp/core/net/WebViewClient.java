package me.adaptive.arp.core.net;

import android.content.Context;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import me.adaptive.arp.R;
import me.adaptive.arp.api.APIRequest;
import me.adaptive.arp.api.APIResponse;
import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.AppResourceData;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.common.Utils;
import me.adaptive.arp.core.AppResourceManager;
import me.adaptive.arp.core.ServiceHandler;

/**
 * Http Interceptor for handling requests inside an Adaptive Runtime Application. More information
 * on: https://github.com/AdaptiveMe/adaptive-arp-api/wiki/ARP-HTTP-Interceptor
 */
public class WebViewClient extends android.webkit.WebViewClient {

    // Logger
    private static final String LOG_TAG = "WebViewClient";
    private ILogging logger;

    // Context
    private Context context;

    /**
     * Default Constructor.
     */
    public WebViewClient() {
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    /**
     * Notify the host application of a resource request and allow the application to return the
     * data. If the return value is null, the WebView will continue to load the resource as usual.
     * Otherwise, the return response and data will be used. NOTE: This method is called on a thread
     * other than the UI thread so clients should exercise caution when accessing private data or
     * the view system.
     *
     * @param view    The WebView that is requesting the resource.
     * @param request Object containing the details of the request.
     * @return A WebResourceResponse containing the response information or null if the WebView should load the resource itself.
     */
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

        WebResourceResponse response = null;
        String method = request.getMethod();
        String url = request.getUrl().toString();


        if (!(url == null || url.isEmpty() || method == null || method.isEmpty())) {

            if (url.startsWith(context.getString(R.string.arp_url)) && method.equals("GET")) {

                // FILE MANAGEMENT (via Adaptive Packer)

                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Intercepting File request: " + request.getUrl().toString());

                AppResourceData resource = AppResourceManager.getInstance().retrieveWebResource(url);

                // Prepare the response
                response = new WebResourceResponse(resource.getRawType(),
                        "UTF-8", 200, "OK", request.getRequestHeaders(),
                        new ByteArrayInputStream(resource.getData()));
                return response;

            } else if (url.startsWith(context.getString(R.string.arp_url)) && method.equals("POST")) {

                // ADAPTIVE NATIVE CALLS

                // TODO: Parse the content of the request and parse it
                APIRequest apiRequest = AppRegistryBridge.getJSONInstance().create().fromJson(request.getRequestHeaders().get("Content-Body"), APIRequest.class);
                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "Intercepting ARP request: " + apiRequest);

                if (!apiRequest.getApiVersion().equals(AppRegistryBridge.getInstance().getAPIVersion())) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "\"The API version of the Typescript API is not the same as the Platform API version");
                }

                // Call the service and return the data
                APIResponse apiResponse = ServiceHandler.getInstance().handleServiceUrl(apiRequest);

                // Prepare the response
                try {
                    response = new WebResourceResponse("application/javascript; charset=utf-8",
                            "UTF-8", apiResponse.getStatusCode(), apiResponse.getStatusMessage(),
                            request.getRequestHeaders(), new ByteArrayInputStream(AppRegistryBridge.getJSONInstance().create().toJson(apiResponse).getBytes("utf-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return response;

            } else if (Utils.validateRegexp(url, "^data:(.*)\\/(.*);base64,(.*)") && method.equals("GET")) {

                // JAVASCRIPT INLINE REQUESTS

                // TODO: forward response
                return null;
            } else {

                // TODO: external resources
                // TODO: external services
                return null;
            }
        } else {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "The method or the url is received is empty");
            return null;
        }
    }
}