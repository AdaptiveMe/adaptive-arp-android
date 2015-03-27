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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseCommunicationDelegate;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IService;
import me.adaptive.arp.api.IServiceMethod;
import me.adaptive.arp.api.IServiceResultCallback;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceRequest;
import me.adaptive.arp.api.ServiceToken;
import me.adaptive.arp.common.parser.xml.XmlParser;

/**
 * Interface for Managing the Services operations
 * Auto-generated implementation of IService specification.
 */
public class ServiceDelegate extends BaseCommunicationDelegate implements IService {

    protected static final String APP_CONFIG_PATH = "app/config/";
    protected static final String APP_DEFINITIONS_CONFIG_PATH = "definitions/";
    protected static final String IO_CONFIG_FILENAME = "io-config.xml";
    protected static final String IO_CONFIG_FILE = APP_CONFIG_PATH+IO_CONFIG_FILENAME;
    protected static final String IO_CONFIG_DEFINITION_FILENAME = "/i18n-config.xsd";
    protected static final String IO_CONFIG_DEFINITION_FILE = APP_CONFIG_PATH+APP_DEFINITIONS_CONFIG_PATH+IO_CONFIG_DEFINITION_FILENAME;

    public static String APIService = "service";
    static LoggingDelegate Logger;


    private List<String> resources = null;
    private List<Service> service = null;

    /**
     * Default Constructor.
     */
    public ServiceDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());
    }

    /**
     * Initialize all the relate objects
     */
    private void initialize(){
        Context context;
        AssetManager assetManager;
        service = new ArrayList<>();
        resources = new ArrayList<>();
        InputStream plistIS = null, origin = null ,validator = null;
        try {
            context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
            assetManager = context.getAssets();

            origin = assetManager.open(IO_CONFIG_FILE);
            validator = assetManager.open(IO_CONFIG_DEFINITION_FILE);
            Document document = XmlParser.getInstance().parseXml(origin,validator);
            resources = XmlParser.getInstance().getResourceData(document);
            service = XmlParser.getInstance().getIOData(document);

        } catch (IOException e) {
            Logger.log(ILoggingLogLevel.Error, APIService, "Error Opening xml - Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Logger.log(ILoggingLogLevel.Error, APIService, "Error Parsing xml - Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (SAXException e) {
            Logger.log(ILoggingLogLevel.Error, APIService, "Error Validating xml - Error: " + e.getLocalizedMessage());
            e.printStackTrace();
        }finally {
            closeStream(plistIS);
        }
    }

    /**
     * Obtains a Service token by a concrete uri (http://domain.com/path). This method would be useful when
     * a service response is a redirect (3XX) and it is necessary to make a request to another host and we
     * don't know by advance the name of the service.
     *
     * @param uri Unique Resource Identifier for a Service-Endpoint-Path-Method
     * @return ServiceToken to create a service request or null if the given parameter is not
     * configured in the platform's XML service definition file.
     * @since v2.1.4
     */
    @Override
    public ServiceToken getServiceTokenByUri(String uri) {
        return null;
    }

    /**
     * Create a service request for the given ServiceToken. This method creates the request, populating
     * existing headers and cookies for the same service. The request is populated with all the defaults
     * for the service being invoked and requires only the request body to be set. Headers and cookies may be
     * manipulated as needed by the application before submitting the ServiceRequest via invokeService.
     *
     * @param serviceToken ServiceToken to be used for the creation of the request.
     * @return ServiceRequest with pre-populated headers, cookies and defaults for the service.
     * @since v2.0.6
     */
    @Override
    public ServiceRequest getServiceRequest(ServiceToken serviceToken) {
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":getServiceRequest");
    }

    /**
     * Obtains a ServiceToken for the given parameters to be used for the creation of requests.
     *
     * @param serviceName  Service name.
     * @param endpointName Endpoint name.
     * @param functionName Function name.
     * @param method       Method type.
     * @return ServiceToken to create a service request or null if the given parameter combination is not
     * configured in the platform's XML service definition file.
     * @since v2.0.6
     */
    @Override
    public ServiceToken getServiceToken(String serviceName, String endpointName, String functionName, IServiceMethod method) {
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":getServiceToken");
    }

    /**
     * Returns all the possible service tokens configured in the platform's XML service definition file.
     *
     * @return Array of service tokens configured.
     * @since v2.0.6
     */
    @Override
    public ServiceToken[] getServicesRegistered() {
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":getServicesRegistered");
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
     * Checks whether a specific service, endpoint, function and method type is configured in the platform's
     * XML service definition file.
     *
     * @param serviceName  Service name.
     * @param endpointName Endpoint name.
     * @param functionName Function name.
     * @param method       Method type.
     * @return Returns true if the service is configured, false otherwise.
     * @since v2.0.6
     */
    @Override
    public boolean isServiceRegistered(String serviceName, String endpointName, String functionName, IServiceMethod method) {
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":isServiceRegistered");
    }

    /**
     * Close given InputStream
     *
     * @param is inputString
     */
    private static void closeStream(InputStream is) {

        try {
            if (is != null) {
                is.close();
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.Error, APIService, "Error closing stream: " + ex.getLocalizedMessage());
        }
    }


}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
