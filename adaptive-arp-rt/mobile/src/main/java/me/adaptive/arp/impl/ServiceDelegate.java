/**
 --| ADAPTIVE RUNTIME PLATFORM |----------------------------------------------------------------------------------------

 (C) Copyright 2013-2015 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.

 Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 . Unless required by appli-
 -cable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,  WITHOUT
 WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the  License  for the specific language governing
 permissions and limitations under the License.

 Original author:

 * Carlos Lozano Diez
 <http://github.com/carloslozano>
 <http://twitter.com/adaptivecoder>
 <mailto:carlos@adaptive.me>

 Contributors:

 * Ferran Vila Conesa
 <http://github.com/fnva>
 <http://twitter.com/ferran_vila>
 <mailto:ferran.vila.conesa@gmail.com>

 * See source code files for contributors.

 Release:

 * @version v2.0.3

-------------------------------------------| aut inveniam viam aut faciam |--------------------------------------------
 */

package me.adaptive.arp.impl;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseCommunicationDelegate;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IService;
import me.adaptive.arp.api.IServiceContentEncoding;
import me.adaptive.arp.api.IServiceMethod;
import me.adaptive.arp.api.IServiceResultCallback;
import me.adaptive.arp.api.IServiceResultCallbackError;
import me.adaptive.arp.api.IServiceResultCallbackWarning;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceEndpoint;
import me.adaptive.arp.api.ServiceHeader;
import me.adaptive.arp.api.ServicePath;
import me.adaptive.arp.api.ServiceRequest;
import me.adaptive.arp.api.ServiceRequestParameter;
import me.adaptive.arp.api.ServiceResponse;
import me.adaptive.arp.api.ServiceSession;
import me.adaptive.arp.api.ServiceSessionAttribute;
import me.adaptive.arp.api.ServiceSessionCookie;
import me.adaptive.arp.api.ServiceToken;
import me.adaptive.arp.common.Utils;
import me.adaptive.arp.common.parser.xml.XmlParser;

/**
 * Interface for Managing the Services operations
 * Auto-generated implementation of IService specification.
 */
public class ServiceDelegate extends BaseCommunicationDelegate implements IService {

    // logger
    private static final String LOG_TAG = "ServiceDelegate";
    private ILogging logger;

    // List of service sessions
    private Map<String, Session> serviceSession;


    /**
     * Default Constructor.
     */
    public ServiceDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        serviceSession = new HashMap<>();
    }

    /**
     * Obtains a Service token by a concrete uri (http://domain.com/path). This method would be useful when
     * a services response is a redirect (3XX) and it is necessary to make a request to another host and we
     * don't know by advance the name of the services.
     *
     * @param uri Unique Resource Identifier for a Service-Endpoint-Path-Method
     * @return ServiceToken to create a services request or null if the given parameter is not
     * configured in the platform's XML services definition file.
     * @since v2.1.4
     */
    @Override
    public ServiceToken getServiceTokenByUri(String uri) {

        URL url;
        if (!Utils.validateURI(uri, "^https?://.*")) return null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "uri Error: " + e.getLocalizedMessage());
            return null;
        }
        for (Service ser : XmlParser.getInstance().getServices().values())
            for (ServiceEndpoint endpoint : ser.getServiceEndpoints())
                if (!url.getProtocol().concat("://").concat(url.getHost()).equals(endpoint.getHostURI()))
                    continue;
                else
                    for (ServicePath path : endpoint.getPaths())
                        if (!url.getPath().equals(path.getPath()))
                            continue;
                        else
                            return new ServiceToken(ser.getName(), endpoint.getHostURI(), path.getPath(), path.getMethods()[0]);

        return null;
    }

    /**
     * Create a services request for the given ServiceToken. This method creates the request, populating
     * existing headers and cookies for the same services. The request is populated with all the defaults
     * for the services being invoked and requires only the request body to be set. Headers and cookies may be
     * manipulated as needed by the application before submitting the ServiceRequest via invokeService.
     *
     * @param serviceToken ServiceToken to be used for the creation of the request.
     * @return ServiceRequest with pre-populated headers, cookies and defaults for the services.
     * @since v2.0.6
     */
    @Override
    public ServiceRequest getServiceRequest(ServiceToken serviceToken) {

        if (!isServiceRegistered(serviceToken)) {
            return null;
        }
        ServiceRequest request = new ServiceRequest(null, serviceToken);
        request.setContentEncoding(IServiceContentEncoding.Utf8);
        request.setServiceToken(serviceToken);
        AppContextWebviewDelegate webViewDelegate = (AppContextWebviewDelegate) AppRegistryBridge.getInstance().getPlatformContextWeb().getDelegate();
        request.setUserAgent(webViewDelegate.getUserAgent());
        request.setContentType(String.valueOf(XmlParser.getInstance().getContentType(serviceToken)));

        if (serviceSession.containsKey(serviceToken.getEndpointName())) {
            Session session = serviceSession.get(serviceToken.getEndpointName());
            request.setServiceHeaders(session.headers);
            request.setServiceSession(new ServiceSession(session.cookies, session.attributes));
            request.setUserAgent(session.userAgent);
        }

        return request;
    }

    /**
     * Obtains a ServiceToken for the given parameters to be used for the creation of requests.
     *
     * @param serviceName  Service name.
     * @param endpointName Endpoint name.
     * @param functionName Function name.
     * @param method       Method type.
     * @return ServiceToken to create a services request or null if the given parameter combination is not
     * configured in the platform's XML services definition file.
     * @since v2.0.6
     */
    @Override
    public ServiceToken getServiceToken(String serviceName, String endpointName, String functionName, IServiceMethod method) {

        if (XmlParser.getInstance().getServices().containsKey(serviceName)) {
            Service serv = XmlParser.getInstance().getServices().get(serviceName);
            for (ServiceEndpoint endpoint : serv.getServiceEndpoints()) {
                if (endpoint.getHostURI().equals(endpointName)) {
                    for (ServicePath path : endpoint.getPaths()) {
                        if (path.equals(functionName)) {
                            for (IServiceMethod serviceMethod : path.getMethods()) {
                                if (serviceMethod.equals(method)) {
                                    return new ServiceToken(serviceName, endpoint.getHostURI(), path.getPath(), serviceMethod);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns all the possible services tokens configured in the platform's XML services definition file.
     *
     * @return Array of services tokens configured.
     * @since v2.0.6
     */
    @Override
    public ServiceToken[] getServicesRegistered() {

        List<ServiceToken> tokens = new ArrayList<>();
        for (Service serv : XmlParser.getInstance().getServices().values()) {
            for (ServiceEndpoint endpoint : serv.getServiceEndpoints()) {
                for (ServicePath path : endpoint.getPaths()) {
                    for (IServiceMethod method : path.getMethods()) {
                        tokens.add(new ServiceToken(serv.getName(), endpoint.getHostURI(), path.getPath(), method));
                    }
                }
            }
        }
        return tokens.toArray(new ServiceToken[tokens.size()]);
    }

    /**
     * Executes the given ServiceRequest and provides responses to the given callback handler.
     *
     * @param serviceRequest ServiceRequest with the request body.
     * @param callback       IServiceResultCallback to handle the ServiceResponse.
     * @since v2.0.6
     */
    @Override
    public void invokeService(ServiceRequest serviceRequest, IServiceResultCallback callback) {

        if (!Utils.validateService(serviceRequest.getServiceToken())) {
            callback.onError(IServiceResultCallbackError.NotRegisteredService);
            return;
        }

        HttpClient httpClient = new DefaultHttpClient();

        ServiceResponse serviceResponse = new ServiceResponse();
        HttpResponse response = null;
        String url = null;
        try {

            switch (serviceRequest.getServiceToken().getInvocationMethod()) {
                case Get:
                    url = getURL(serviceRequest);
                    response = httpClient.execute(new HttpGet(url));
                    break;
                case Post:
                    break;
                case Head:
                    break;
                default:
            }

            StatusLine statusLine = response.getStatusLine();

            int status = statusLine.getStatusCode();

            if (isBetween(status, 200, 406) || isBetween(status, 500, 599)) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);

                String responseString = out.toString();
                out.close();

                // TODO: Prepare the session attributes
                serviceResponse.setContentType(EntityUtils.getContentCharSet(response.getEntity()));
                serviceResponse.setContentEncoding(IServiceContentEncoding.Utf8);
                serviceResponse.setContent(responseString);
                serviceResponse.setContentLength(responseString.length());
                serviceResponse.setServiceHeaders(getHeaders(response.getAllHeaders()));
                serviceResponse.setStatusCode(status);
                if (!url.startsWith("https://")) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Not Secured URL (https): " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.NotSecure);
                    return;
                }

                if (isBetween(status, 200, 299)) {

                    callback.onResult(serviceResponse);
                    return;
                } else if (isBetween(status, 300, 399)) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Redirected Response");
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.Redirected);
                    return;
                } else if (status == 400) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Wrong params: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.WrongParams);
                    return;
                } else if (status == 401) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Not authenticaded: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.NotAuthenticated);
                    return;
                } else if (status == 402) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Payment Required: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.PaymentRequired);
                    return;
                } else if (status == 403) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Forbidden: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.Forbidden);
                    return;
                } else if (status == 404) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "NotFound: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.NotFound);
                    return;
                } else if (status == 405) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Method not allowed: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.MethodNotAllowed);
                    return;
                } else if (status == 406) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Not allowed: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.NotAllowed);
                    return;
                }
                if (isBetween(status, 500, 599)) {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "Server error: " + url);
                    callback.onWarning(serviceResponse, IServiceResultCallbackWarning.ServerError);
                    return;
                } else {
                    logger.log(ILoggingLogLevel.Warn, LOG_TAG, "The status code received [" + status + "] is not handled by the platform");
                    callback.onError(IServiceResultCallbackError.Unreachable);
                    return;
                }


            } else if (status == 408) {
                logger.log(ILoggingLogLevel.Error, LOG_TAG, "There is a timeout calling the service: " + url);
                callback.onError(IServiceResultCallbackError.TimeOut);
                return;
            } else if (status == 444) {
                logger.log(ILoggingLogLevel.Error, LOG_TAG, "There is no response calling the service: " + url);
                callback.onError(IServiceResultCallbackError.NoResponse);
                return;
            } else {
                logger.log(ILoggingLogLevel.Error, LOG_TAG, "The status code received [" + status + "] is not handled by the platform");
                callback.onError(IServiceResultCallbackError.Unknown);
                return;

            }

        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "invokeService Error: " + e.getLocalizedMessage());
            assert response != null;
            try {
                response.getEntity().getContent().close();
            } catch (IOException e1) {
                logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing the response: " + e1.getLocalizedMessage());
            }
        }

        logger.log(ILoggingLogLevel.Error, LOG_TAG, "The status code received is not handled by the platform");
        callback.onError(IServiceResultCallbackError.Unreachable);


    }

    /**
     * Get ServiceHeader[] from Header[]
     *
     * @param headers array
     * @return serviceHeader array
     */
    private ServiceHeader[] getHeaders(Header[] headers) {
        ServiceHeader[] serviceHeaders = new ServiceHeader[0];

        for (Header header : headers) {
            ServiceHeader serviceHeader = new ServiceHeader(header.getName(), header.getValue());
            serviceHeaders = Utils.addElement(serviceHeaders, serviceHeader);
        }
        return serviceHeaders;
    }

    /**
     * Return the url string from a ServiceRequest
     *
     * @param request Request object
     * @return Url string
     */
    public String getURL(ServiceRequest request) {
        String urlString;
        ServiceToken token = request.getServiceToken();
        String parameters = "";

        if (request.getQueryParameters() != null){
            for (ServiceRequestParameter serviceRequestParameter : request.getQueryParameters()) {
                if (!parameters.isEmpty()) parameters += "&";
                parameters += serviceRequestParameter.getKeyName() + "=" + serviceRequestParameter.getKeyData();
            }
        }

        urlString = token.getEndpointName() + token.getFunctionName() + (parameters.isEmpty() ? "" : ("?" + parameters));
        return urlString;
    }

    /**
     * Checks whether a specific services, endpoint, function and method type is configured in the platform's
     * XML services definition file.
     *
     * @param serviceName  Service name.
     * @param endpointName Endpoint name.
     * @param functionName Function name.
     * @param method       Method type.
     * @return Returns true if the services is configured, false otherwise.
     * @since v2.0.6
     */
    @Override
    public boolean isServiceRegistered(String serviceName, String endpointName, String functionName, IServiceMethod method) {

        if (XmlParser.getInstance().getServices().containsKey(serviceName)) {
            Service serv = XmlParser.getInstance().getServices().get(serviceName);
            for (ServiceEndpoint endpoint : serv.getServiceEndpoints()) {
                if (endpoint.getHostURI().equals(endpointName)) {
                    for (ServicePath path : endpoint.getPaths()) {
                        if (path.getPath().equals(functionName)) {
                            for (IServiceMethod serviceMethod : path.getMethods()) {
                                if (serviceMethod.equals(method))
                                    return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method for testing if a service is registered
     *
     * @param serviceToken Service Token to test
     * @return TRue is registered, false otherwise
     */
    private boolean isServiceRegistered(ServiceToken serviceToken) {
        return isServiceRegistered(serviceToken.getServiceName(), serviceToken.getEndpointName(), serviceToken.getFunctionName(), serviceToken.getInvocationMethod());
    }

    /**
     * Check if a number is between two numbers
     *
     * @param x     Number to check
     * @param lower Lower number
     * @param upper Upper number
     * @return Returns true or false
     */
    private boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    /**
     * Internal class to represent a session object travelling for the ARP
     */
    private class Session {
        ServiceHeader[] headers;
        ServiceSessionCookie[] cookies;
        ServiceSessionAttribute[] attributes;
        String userAgent;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
