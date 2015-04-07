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
import android.content.res.AssetManager;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseCommunicationDelegate;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IService;
import me.adaptive.arp.api.IServiceMethod;
import me.adaptive.arp.api.IServiceResultCallback;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceEndpoint;
import me.adaptive.arp.api.ServiceHeader;
import me.adaptive.arp.api.ServicePath;
import me.adaptive.arp.api.ServiceRequest;
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


    protected static final String APP_CONFIG_PATH = "config/";
    protected static final String APP_DEFINITIONS_CONFIG_PATH = "definitions/";
    protected static final String IO_CONFIG_FILENAME = "io-config.xml";
    protected static final String IO_CONFIG_FILE = APP_CONFIG_PATH+IO_CONFIG_FILENAME;
    protected static final String IO_CONFIG_DEFINITION_FILENAME = "i18n-config.xsd";
    protected static final String IO_CONFIG_DEFINITION_FILE = APP_CONFIG_PATH+APP_DEFINITIONS_CONFIG_PATH+IO_CONFIG_DEFINITION_FILENAME;


    // logger
    private static final String LOG_TAG = "ServiceDelegate";
    private ILogging logger;


    // Context
    private Context context;

    private List<String> resources = null;
    private Map<String, Service> services = null;
    private Map<String, Session> serviceSession;


    /**
     * Default Constructor.
     */
    public ServiceDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    /**
     * Initialize all the relate objects
     */
    private void initialize(){

        AssetManager assetManager;
        services = new HashMap<>();
        resources = new ArrayList<>();
        InputStream plistIS = null, origin = null ,validator = null;
        serviceSession = new HashMap<>();
        try {

            assetManager = context.getAssets();

            origin = assetManager.open(IO_CONFIG_FILE);
            validator = assetManager.open(IO_CONFIG_DEFINITION_FILE);


            /*if(XmlParser.getInstance().validateWithExtXSDUsingSAX(originStr,validatorStr)){
                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "VALID");
            }else logger.log(ILoggingLogLevel.Error, LOG_TAG, "INVALID");*/


            Document document = XmlParser.getInstance().parseXml(origin,validator);
            resources = XmlParser.getInstance().getResourceData(document);
            services = XmlParser.getInstance().getIOData(document);

        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error Opening xml - Error: " + e.getLocalizedMessage());
        } catch (ParserConfigurationException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error Parsing xml - Error: " + e.getLocalizedMessage());
        } catch (SAXException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error Validating xml - Error: " + e.getLocalizedMessage());
        }finally {
            closeStream(plistIS);
        }
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
        if(services == null) initialize();
        URL url = null;
        if(!Utils.validateURI(uri, "^https?://.*")) return null;
        try {
            url = new URL(uri);
        } catch (MalformedURLException e) {
            logger.log(ILoggingLogLevel.Error,LOG_TAG,"uri Error: "+e.getLocalizedMessage());
            return null;
        }
        for(Service ser: services.values())
            for(ServiceEndpoint endpoint: ser.getServiceEndpoints())
                if(!url.getHost().equals(endpoint.getHostURI()))
                    continue;
                else
                    for(ServicePath path: endpoint.getPaths())
                        if(!url.getPath().equals(path))
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
        if(services == null) initialize();
        ServiceRequest request = new ServiceRequest(null,serviceToken);
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
        if(services == null) initialize();
        if(services.containsKey(serviceName)){
            Service serv = services.get(serviceName);
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
        if(services == null) initialize();
        List<ServiceToken> tokens = new ArrayList<>();
        for(Service serv: services.values()){
            for(ServiceEndpoint endpoint: serv.getServiceEndpoints()){
                for(ServicePath path:endpoint.getPaths()){
                    for(IServiceMethod method: path.getMethods()){
                        tokens.add(new ServiceToken(serv.getName(),endpoint.getHostURI(),path.getPath(),method));
                    }
                }
            }
        }
        return (ServiceToken[]) tokens.toArray();
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
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":invokeService");
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
        if(services == null) initialize();
        if(services.containsKey(serviceName)) {
            Service serv = services.get(serviceName);
            for(ServiceEndpoint endpoint: serv.getServiceEndpoints()){
                if(endpoint.equals(endpointName)){
                    for(ServicePath path: endpoint.getPaths()){
                        if(path.equals(functionName)){
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

    /**
     * Close given InputStream
     *
     * @param is inputString
     */
    private void closeStream(InputStream is) {

        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception ex) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing stream: " + ex.getLocalizedMessage());

        }
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
