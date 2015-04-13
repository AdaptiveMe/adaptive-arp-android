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

import android.content.Context;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
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


    private HttpClient httpClient = null;
    private CookieStore httpCookieStore = new BasicCookieStore();


    // Context
    private Context context;

    private Map<String, Session> serviceSession;


    /**
     * Default Constructor.
     */
    public ServiceDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
        serviceSession = new HashMap<String, Session>();
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

        URL url = null;
        if(!Utils.validateURI(uri, "^https?://.*")) return null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            logger.log(ILoggingLogLevel.Error,LOG_TAG,"uri Error: "+e.getLocalizedMessage());
            return null;
        }
        for(Service ser: XmlParser.getInstance().getServices().values())
            for(ServiceEndpoint endpoint: ser.getServiceEndpoints())
                if(!url.getProtocol().concat("://").concat(url.getHost()).equals(endpoint.getHostURI()))
                    continue;
                else
                    for(ServicePath path: endpoint.getPaths())
                        if(!url.getPath().equals(path.getPath()))
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

        if(!isServiceRegistered(serviceToken)){
            return null;
        }
        ServiceRequest request = new ServiceRequest(null,serviceToken);
        request.setContentEncoding(IServiceContentEncoding.Utf8);
        request.setServiceToken(serviceToken);
        AppContextWebviewDelegate webViewDelegate = (AppContextWebviewDelegate) AppRegistryBridge.getInstance().getPlatformContextWeb().getDelegate();
        request.setUserAgent(webViewDelegate.getUserAgent());
        request.setContentType(String.valueOf(XmlParser.getInstance().getContentType(serviceToken)));

        if(serviceSession.containsKey(serviceToken.getEndpointName())){
            Session session = serviceSession.get(serviceToken.getEndpointName());
            request.setServiceHeaders(session.headers);
            request.setServiceSession(new ServiceSession(session.cookies,session.attribs));
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

        if(XmlParser.getInstance().getServices().containsKey(serviceName)){
            Service serv = XmlParser.getInstance().getServices().get(serviceName);
            for(ServiceEndpoint endpoint: serv.getServiceEndpoints()){
                if(endpoint.getHostURI().equals(endpointName)){
                    for(ServicePath path: endpoint.getPaths()){
                        if(path.equals(functionName)){
                            for(IServiceMethod serviceMethod: path.getMethods()){
                                if(serviceMethod.equals(method)){
                                    return new ServiceToken(serviceName,endpoint.getHostURI(),path.getPath(),serviceMethod);
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
        for(Service serv: XmlParser.getInstance().getServices().values()){
            for(ServiceEndpoint endpoint: serv.getServiceEndpoints()){
                for(ServicePath path:endpoint.getPaths()){
                    for(IServiceMethod method: path.getMethods()){
                        tokens.add(new ServiceToken(serv.getName(),endpoint.getHostURI(),path.getPath(),method));
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
        if(!Utils.validateService(serviceRequest.getServiceToken())){
            callback.onError(IServiceResultCallbackError.NotRegisteredService);
            return;
        }

        httpClient = new DefaultHttpClient();

        ServiceResponse serviceResponse = new ServiceResponse();
        try {
            //TODO PREPARE THE REQUEST
            HttpResponse response = httpClient.execute(new HttpGet(getURL(serviceRequest)));
            StatusLine statusLine = response.getStatusLine();

            int status = statusLine.getStatusCode();
            switch(status){

                case  HttpStatus.SC_OK:

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);

                    String responseString = out.toString();
                    out.close();

                    //..more logic
                    //TODO PREPARE Session
                    serviceResponse.setContentType(EntityUtils.getContentCharSet(response.getEntity()));
                    serviceResponse.setContentEncoding(IServiceContentEncoding.Utf8);
                    serviceResponse.setContent(responseString);
                    serviceResponse.setContentLength(responseString.length());
                    serviceResponse.setServiceHeaders(getHeaders(response.getAllHeaders()));
                    serviceResponse.setStatusCode(status);

                    response.getEntity().getContent().close();

                    callback.onResult(serviceResponse);
                    return;
                case HttpStatus.SC_REQUEST_TIMEOUT:
                    logger.log(ILoggingLogLevel.Error,LOG_TAG, "There is a timeout calling the service: "+status);
                    callback.onError(IServiceResultCallbackError.TimeOut);
                    return;

                default:
                    logger.log(ILoggingLogLevel.Error,LOG_TAG, "The status code received: ["+status+"] is not handled by" +
                            " the plattform" );
                    callback.onError(IServiceResultCallbackError.Unknown);

            }




        } catch (IOException e){
            logger.log(ILoggingLogLevel.Error,LOG_TAG, "invokeService Error: "+e.getLocalizedMessage());
        }


    }

    /**
     * Get ServiceHeader[] from Header[]
     * @param headers array
     * @return serviceHeader array
     */
    private ServiceHeader[] getHeaders(Header[] headers){
        ServiceHeader[] serviceHeaders = new ServiceHeader[0];

        for (Header header : headers) {
            ServiceHeader serviceHeader = new ServiceHeader(header.getName(),header.getValue());
            serviceHeaders = Utils.addElement(serviceHeaders,serviceHeader);
        }
        return serviceHeaders;
    }


    /**
     * Return the url string from a ServiceRequest
     * @param request
     * @return Url string
     */
    private String getURL(ServiceRequest request){
        String urlString;
        ServiceToken token = request.getServiceToken();
        String parameters = "";
        for (ServiceRequestParameter serviceRequestParameter : request.getQueryParameters()) {
            if(!parameters.isEmpty()) parameters += "&";
            parameters += serviceRequestParameter.getKeyName()+"="+serviceRequestParameter.getKeyData();
        }

        String content = request.getContent();
        urlString  = token.getEndpointName()+token.getFunctionName()+token.getFunctionName()+(parameters.isEmpty()?"":"?"+parameters);

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

        if(XmlParser.getInstance().getServices().containsKey(serviceName)) {
            Service serv = XmlParser.getInstance().getServices().get(serviceName);
            for(ServiceEndpoint endpoint: serv.getServiceEndpoints()){
                if(endpoint.getHostURI().equals(endpointName)){
                    for(ServicePath path: endpoint.getPaths()){
                        if(path.getPath().equals(functionName)){
                            for(IServiceMethod serviceMethod: path.getMethods()){
                                if(serviceMethod.equals(method))
                                    return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean isServiceRegistered(ServiceToken serviceToken) {
        return isServiceRegistered(serviceToken.getServiceName(),serviceToken.getEndpointName(),serviceToken.getFunctionName(),serviceToken.getInvocationMethod());
    }


    private class Session {
        ServiceHeader[] headers;
        ServiceSessionCookie[] cookies;
        ServiceSessionAttribute[] attribs;
        String userAgent;
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
