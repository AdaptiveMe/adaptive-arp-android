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

import android.os.AsyncTask;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.x509.AccessDescription;
import org.spongycastle.asn1.x509.AuthorityInformationAccess;
import org.spongycastle.asn1.x509.X509Extensions;
import org.spongycastle.cert.ocsp.OCSPReq;
import org.spongycastle.jce.provider.X509CertParser;
import org.spongycastle.jce.provider.X509CertificateObject;
import org.spongycastle.x509.util.StreamParsingException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IService;
import me.adaptive.arp.api.IServiceMethod;
import me.adaptive.arp.api.IServiceResultCallback;
import me.adaptive.arp.api.IServiceResultCallbackError;
import me.adaptive.arp.api.IServiceType;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceEndpoint;
import me.adaptive.arp.api.ServiceRequest;
import me.adaptive.arp.api.ServiceResponse;
import me.adaptive.arp.api.ServiceSession;
import me.adaptive.arp.api.ServiceSessionCookie;
import me.adaptive.arp.api.ServiceToken;

/**
 * Interface for Managing the Services operations
 * Auto-generated implementation of IService specification.
 */
public class ServiceDelegate extends BaseCommunicationDelegate implements IService {


    private static final String DEFAULT_SERVICE_TYPE = "XMLRPC_JSON";
    private static final String DEFAULT_SERVICE_METHOD = "POST";
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String SERVICE_NODE_ATTRIBUTE = "SERVICE";
    private static final String ENDPOINT_NODE_ATTRIBUTE = "END-POINT";
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String REQ_METHOD_ATTRIBUTE = "req-method";
    private static final String HOST_ATTRIBUTE = "host";
    private static final String PORT_ATTRIBUTE = "port";
    private static final String PATH_ATTRIBUTE = "path";
    private static final String PROXY_ATTRIBUTE = "proxy";
    private static final String SCHEME_ATTRIBUTE = "scheme";
    private static final String SERVICE_ATTRIBUTE = "name";
    // private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    public static String APIService = "service";
    protected static Map<IServiceType, String> contentTypes = new HashMap<IServiceType, String>();
    static LoggingDelegate Logger;


    private static CookieStore cookieStore;
    private static DefaultHttpClient httpClient = new DefaultHttpClient();
    private static DefaultHttpClient httpSSLClient = null;
    // TODO - Fingerprints to go into the services config file.
    private static String _VALIDATECERTIFICATES = "$ValidateCertificates$";
    private static String _VALIDATEFINGERPRINTS = "$ValidateFingerprints$";
    private static int DEFAULT_READWRITE_TIMEOUT = 15000; // 15 seconds timeout establishing connection
    private static int DEFAULT_RESPONSE_TIMEOUT = 100000; // 100 seconds timeout reading response
    // reading response parameters
    private static int DEFAULT_BUFFER_READ_SIZE = 4096;    // 4 KB
    private static int MAX_BINARY_SIZE = 8 * 1024 * 1024;  // 8 MB
    protected HashMap<String, String[]> FINGERPRINT;
    private boolean addedGzipHttpResponseInterceptor = false;
    private List<Service> services = new ArrayList<Service>();

    // TODO - Revise - maybe it's better to instance this when it's really needed -> Lazy Instance.
    static {
        // TODO - Enums have been removed or renamed and include only the most common options.
        /**
         contentTypes.put(IServiceType.ServiceTypeXmlRpcJson, "application/json");
         contentTypes.put(IServiceType.ServiceTypeXmlRpcXml, "text/xml");
         contentTypes.put(IServiceType.ServiceTypeRestJson, "application/json");
         contentTypes.put(IServiceType.ServiceTypeRestXml, "text/xml");
         contentTypes.put(IServiceType.ServiceTypeSoapJson, "application/json");
         contentTypes.put(IServiceType.ServiceTypeSoapXml, "text/xml");
         contentTypes.put(IServiceType.ServiceTypeAmfSerialization, "");
         contentTypes.put(IServiceType.ServiceTypeRemotingSerialization, "");
         contentTypes.put(IServiceType.ServiceTypeOctetBinary, "application/octet-stream");
         contentTypes.put(IServiceType.ServiceTypeGwtRpc, "text/x-gwt-rpc; charset=utf-8");
         */
        contentTypes.put(IServiceType.OctetBinary, "application/octet-stream");
        contentTypes.put(IServiceType.RestJson, "application/json; charset=utf-8");
        contentTypes.put(IServiceType.RestXml, "application/xml; charset=utf-8");
        contentTypes.put(IServiceType.SoapXml, "application/soap+xml; charset=utf-8");
        contentTypes.put(IServiceType.Unknown, "application/octet-stream");

        cookieStore = httpClient.getCookieStore();
    }

    /**
     * Default Constructor.
     */
    public ServiceDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    public static boolean validateFingerprints() {
        return Boolean.parseBoolean(ServiceDelegate._VALIDATEFINGERPRINTS);
    }

    /**
     * Get a reference to a registered service by name.
     *
     * @param serviceName Name of service.
     * @return A service, if registered, or null of the service does not exist.
     * @since ARP1.0
     */
    public Service getService(String serviceName) {
        Service response = null;
        for (Service service : services) {
            if (service.getName().equals(serviceName))
                response = service;
        }
        return response;
    }

    /**
     * Request async a service for an Url
     *
     * @param serviceRequest Service Request to invoke
     * @param service        Service to call
     * @param callback       Callback to execute with the result
     * @since ARP1.0
     */
    public void invokeService(ServiceRequest serviceRequest, Service service, IServiceResultCallback callback) {
        ServiceTask asyncTask = new ServiceTask();
        asyncTask.executeOnExecutor(AppContextDelegate.getExecutorService(), new Object[]{serviceRequest, service, callback});
    }

    /**
     * Check whether a service by the given service is already registered.
     *
     * @param service Service to check
     * @return True if the service is registered, false otherwise.
     * @since ARP1.0
     */
    public boolean isRegistered(Service service) {
        return services.contains(service);
    }

    /**
     * Check whether a service by the given name is registered.
     *
     * @param serviceName Name of service.
     * @return True if the service is registered, false otherwise.
     * @since ARP1.0
     */
    public boolean isRegistered(String serviceName) {
        for (Service service : services) {
            if (service.getName().equals(serviceName))
                return true;
        }
        return false;
    }

    /**
     * Register a new service
     *
     * @param service to register
     * @since ARP1.0
     */
    public void registerService(Service service) {
        if (!services.contains(service)) {
            services.add(service);
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "registerService: " + service.toString() + " Added!");
        } else
            Logger.log(ILoggingLogLevel.WARN, APIService, "registerService: " + service.toString() + " is already added!");

    }

    /**
     * Unregister a service
     *
     * @param service to unregister
     * @since ARP1.0
     */
    public void unregisterService(Service service) {
        if (services.contains(service)) {
            services.remove(service);
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "unregisterService" + service.toString() + " Removed!");
        } else
            Logger.log(ILoggingLogLevel.WARN, APIService, "unregisterService: " + service.toString() + " is NOT registered");
    }

    /**
     * Unregister all services.
     *
     * @since ARP1.0
     */
    public void unregisterServices() {
        services.clear();
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "unregisterServices: ALL Services have been removed!");
    }

    public boolean validateCertificates() {
        return Boolean.parseBoolean(ServiceDelegate._VALIDATECERTIFICATES);
    }

    /*public Map getFingerprints(){
        HashMap<String, String[]> result = new HashMap<String, String[]>();
        for (Service service : services) {
            if(service.getEndpoint().getFingerprint() != null){
                try {
                    URL aURL = new URL(service.getEndpoint().getHost());
                    if(!result.containsKey(aURL.getHost()))
                        result.put(aURL.getHost(),service.getEndpoint().getFingerprint().split(","));
                    ServiceLocator.getLogger().log(ILogging.LogLevel.DEBUG, APIService, "fingerprint: "aURL.getHost()+":"+service.getEndpoint().getFingerprint());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }*/

    private String formatRequestUriString(ServiceRequest request, ServiceEndpoint endpoint, String ServiceMethod) {

        // TODO - Revise following new v2.0.6 API - requests are created by the service and pre-populated with default parameters, headers and applicable cookies.
        /*
        String requestUriString = endpoint.getHost() + ":"
                + endpoint.getPort() + endpoint.getPath();
        if (endpoint.getPort() == 0) {
            requestUriString = endpoint.getHost() + endpoint.getPath();
        }

        if (ServiceMethod.equalsIgnoreCase(IServiceMethod.Get.toString())) {
            // add request content to the URI string when GET method.
            if (request.getContent() != null) {
                requestUriString += request.getContent();
            }
        }

        // JUST FOR LOCAL TESTING, DO NOT UNCOMMENT FOR PLATFORM RELEASE
        //Logger.log( ILoggingLogLevel.DEBUG,APIService, "Requesting service: " + requestUriString);
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Request method: " + ServiceMethod);

        return requestUriString;
        */
        return null;
    }

    private boolean applySecurityValidations(String requestUriString) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException {
        if (requestUriString.startsWith(HTTPS_SCHEME)) {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Applying Custom HTTPSClient (requested URI contains HTTPS protocol)");
            if (httpSSLClient == null) {
                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Custom HTTPSClient not yet intialized on first request, forcing creating it...");
                createHttpClients();
            }
            httpClient = httpSSLClient;
        } else {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Applying DefaultHTTPClient");
            httpClient = new DefaultHttpClient();
        }
        return true;
    }

    /**
     * @param request
     * @param endpoint
     * @throws java.net.URISyntaxException
     */
    private void addingHttpClientParms(ServiceRequest request, ServiceEndpoint endpoint) throws URISyntaxException {

        // preserving the cookies between requests
        httpClient.setCookieStore(cookieStore);

        // TODO - Protocols are now configured in the services configuration file. This no longer lives in the request. Revise.
        /*
        if (request.getProtocolVersion() == IServiceProtocolVersion.HttpProtocolVersion11) {
            httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        } else {
            httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_0);  // not chunked requests
        }
        */

        httpClient.getParams().setIntParameter("http.connection.timeout", DEFAULT_READWRITE_TIMEOUT);
        httpClient.getParams().setIntParameter("http.socket.timeout", DEFAULT_RESPONSE_TIMEOUT);

        // TODO - Proxies have been removed from the endpoint configuration.
        /*
        if (endpoint.getProxy() != null
                && !endpoint.getProxy().equals("")
                && !endpoint.getProxy().equals("null")) {
            URI proxyUrl = new URI(endpoint.getProxy());
            HttpHost proxyHost = new HttpHost(proxyUrl.getHost(),
                    proxyUrl.getPort(), proxyUrl.getScheme());
            httpClient.getParams().setParameter(
                    ConnRoutePNames.DEFAULT_PROXY, proxyHost);
        }
        */

        // [MOBPLAT-200] : allow gzip, deflate decompression modes
        if (!addedGzipHttpResponseInterceptor) {
            httpClient.addResponseInterceptor(new GzipHttpResponseInterceptor());
            addedGzipHttpResponseInterceptor = true;
        }
    }


    private HttpEntityEnclosingRequestBase buildWebRequest(ServiceRequest request, Service service, String requestUriString, String ServiceMethod)
            throws UnsupportedEncodingException, URISyntaxException {


        HttpEntityEnclosingRequestBase httpRequest = new HttpAdaptive(new URI(requestUriString), ServiceMethod);

        /*************
         * adding content as entity, for request methods != GET
         *************/
        if (!ServiceMethod.equalsIgnoreCase(IServiceMethod.GET.toString())) {
            if (request.getContent() != null
                    && request.getContent().length() > 0) {
                httpRequest.setEntity(new StringEntity(
                        request.getContent(), HTTP.UTF_8));
            }
        }

        /*************
         * CONTENT TYPE
         *************/
        // TODO - revise using the new service token and servicerequest factory creation introduced in v2.0.6
        String contentType = null; //contentTypes.get(service.getType()).toString();
        if (request.getContentType() != null) {
            contentType = request.getContentType();

        }
        httpRequest.setHeader("Content-Type", contentType);

        /*************
         * CUSTOM HEADERS HANDLING
         *************/
        if (request.getServiceHeaders() != null
                && request.getServiceHeaders().length > 0) {
            for (me.adaptive.arp.api.ServiceHeader header : request.getServiceHeaders()) {
                httpRequest.setHeader(header.getKeyName(),
                        header.getKeyData());
            }
        }

        /*************
         * COOKIES HANDLING
         *************/
        if (request.getServiceSession() != null
                && request.getServiceSession().getCookies() != null
                && request.getServiceSession().getCookies().length > 0) {
            StringBuffer buffer = new StringBuffer();
            ServiceSessionCookie[] cookies = request.getServiceSession().getCookies();
            for (int i = 0; i < cookies.length; i++) {
                ServiceSessionCookie cookie = cookies[i];
                buffer.append(cookie.getCookieName());
                buffer.append("=");
                buffer.append(cookie.getCookieValue());
                if (i + 1 < cookies.length) {
                    buffer.append(" ");
                }
            }
            httpRequest.setHeader("Cookie", buffer.toString());
        }

        /*************
         * DEFAULT HEADERS
         *************/
        httpRequest.setHeader("Accept", contentType); // Accept header should be the same as the request content type used (could be override by the request, or use the service default)
        // httpRequest.setHeader("content-length",
        // String.valueOf(request.getContentLength()));
        httpRequest.setHeader("keep-alive", String.valueOf(false));

        // TODO: set conn timeout ???

        /*************
         * setting user-agent
         *************/
        String userAgent = System.getProperty("http.agent");
        HttpProtocolParams.setUserAgent(httpClient.getParams(),
                userAgent);

        return httpRequest;
    }

    /**
     * @param httpResponse
     * @param service
     * @return
     * @throws IOException
     * @throws IllegalStateException
     */
    private ServiceResponse readWebResponse(HttpResponse httpResponse, Service service) throws IllegalStateException, IOException {

        ServiceResponse response = new ServiceResponse();

        response.setServiceSession(new ServiceSession());

        byte[] resultBinary = null;

        String responseMimeTypeOverride = (httpResponse.getLastHeader("Content-Type") != null ? httpResponse.getLastHeader("Content-Type").getValue() : null);
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "response content type: " + responseMimeTypeOverride);

        // getting response input stream
        InputStream responseStream = httpResponse.getEntity().getContent();

        int lengthContent = -1;
        int bufferReadSize = DEFAULT_BUFFER_READ_SIZE;

        try {
            lengthContent = (int) httpResponse.getEntity().getContentLength();
            if (lengthContent >= 0 && lengthContent <= bufferReadSize) {
                bufferReadSize = lengthContent;
            }
        } catch (Exception e) {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Error while getting Content-Length header from response: " + e.getMessage());
        }

        if (lengthContent > MAX_BINARY_SIZE) {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "WARNING! - file exceeds the maximum size defined in platform (" + MAX_BINARY_SIZE + " bytes)");
        } else {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "reading response stream content length: " + lengthContent);
            /* WE DON'T READ IN A FULL BLOCK ANYMORE
             *
			// Read in block, if content length provided.
			// Create the byte array to hold the data
			resultBinary = new byte[length];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < resultBinary.length
					&& (numRead = responseStream.read(resultBinary,
							offset, resultBinary.length - offset)) >= 0) {
				offset += numRead;
			}

			*/

            // Read in buffer blocks till the end of stream.
            ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();

            byte[] readBuffer = new byte[bufferReadSize];
            int readLen = 0;
            int totalReadLen = 0;
            try {
                while ((readLen = responseStream.read(readBuffer, 0,
                        readBuffer.length)) > 0) {
                    outBuffer.write(readBuffer, 0, readLen);
                    totalReadLen = totalReadLen + readLen;
                }
            } finally {
                resultBinary = outBuffer.toByteArray();
                outBuffer.close();
                outBuffer = null;
            }
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "total read length: " + totalReadLen);
        }

        /*************
         * COOKIES HANDLING
         *************/
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "reading cookies.. ");
        if (cookieStore.getCookies().size() > 0) {

            ServiceSessionCookie[] cookies = new ServiceSessionCookie[cookieStore.getCookies()
                    .size()];
            for (int i = 0; i < cookieStore.getCookies().size(); i++) {
                Cookie cookie = cookieStore.getCookies().get(i);
                ServiceSessionCookie Cookie = new ServiceSessionCookie();
                Cookie.setCookieName(cookie.getName());
                Cookie.setCookieValue(cookie.getValue());
                cookies[i] = Cookie;
            }
            response.getServiceSession().setCookies(cookies);
        }

        /*************
         * CACHE
         *************/
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "reading cache header.. ");
        // preserve cache-control header from remote server, if any
        String cacheControlHeader = (httpResponse.getLastHeader("Cache-Control") != null ? httpResponse.getLastHeader("Cache-Control").getValue() : null);
        if (cacheControlHeader != null && !cacheControlHeader.isEmpty()) {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Found Cache-Control header on response: " + cacheControlHeader + ", using it on internal response...");

            me.adaptive.arp.api.ServiceHeader cacheHeader = new me.adaptive.arp.api.ServiceHeader();
            cacheHeader.setKeyName("Cache-Control");
            cacheHeader.setKeyData(cacheControlHeader);

            List<me.adaptive.arp.api.ServiceHeader> headers = new ArrayList<me.adaptive.arp.api.ServiceHeader>();
            if (response.getServiceHeaders() != null) {
                headers = Arrays.asList(response.getServiceHeaders());
            }
            headers.add(cacheHeader);
            response.setServiceHeaders((me.adaptive.arp.api.ServiceHeader[]) headers.toArray(new me.adaptive.arp.api.ServiceHeader[0]));
        }

        // Close the input stream and return bytes
        responseStream.close();

        Logger.log(ILoggingLogLevel.DEBUG, APIService, "checking binary service type... ");
        // TODO - revise using the new service token and servicerequest factory creation introduced in v2.0.6
        /*
        if (IServiceType.OctetBinary.equals(service.getType())) {
            if (responseMimeTypeOverride != null && !responseMimeTypeOverride.equals(contentTypes.get(service.getType()))) {
                response.setContentType(responseMimeTypeOverride);
            } else {
                response.setContentType(contentTypes.get(service.getType()).toString());
            }
            response.setContentBinary(resultBinary); // Assign binary
            // content here
        } else {
            response.setContentType(contentTypes.get(service.getType())
                    .toString());
            response.setContent(new String(resultBinary));

        }
        */
        Logger.log(ILoggingLogLevel.DEBUG, APIService, "END reading response.. ");
        return response;
    }


    /**
     * @deprecated
     */
    public ServiceResponse invokeService(ServiceRequest request, Service service) {
        // TODO - revise using the new service token and servicerequest factory creation introduced in v2.0.6
        /*
        ServiceEndpoint endpoint = service.getServiceEndpoint();
        ServiceResponse response = new ServiceResponse();

        if (service != null) {
            // JUST FOR LOCAL TESTING, DO NOT UNCOMMENT FOR PLATFORM RELEASE
            //Logger.log( ILoggingLogLevel.DEBUG,APIService, "Request content: " + request.getContent());

            if (endpoint == null) {
                Logger.log(ILoggingLogLevel.DEBUG, APIService,
                        "No endpoint configured for this service name: "
                                + service.getName()
                );
                return response;
            }

            String ServiceMethod = service.getMethod().toString();
            if (request.getMethod() != null && request.getMethod().length() > 0)
                ServiceMethod = request.getMethod().toUpperCase();

            String requestUriString = formatRequestUriString(request, endpoint, ServiceMethod);
            Thread timeoutThread = null;

            try {

                // Security - VALIDATIONS
                if (!this.applySecurityValidations(requestUriString)) {
                    return null;
                }

                // Adding HTTP Client Parameters
                this.addingHttpClientParms(request, endpoint);

                // Building Web Request to send
                HttpEntityEnclosingRequestBase httpRequest = this.buildWebRequest(request, service, requestUriString, ServiceMethod);

                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Downloading service content");

                // Throw a new Thread to check absolute timeout
                timeoutThread = new Thread(new CheckTimeoutThread(httpRequest));
                timeoutThread.start();

                long start = System.currentTimeMillis();
                HttpResponse httpResponse = httpClient.execute(httpRequest);
                Logger.log(ILoggingLogLevel.DEBUG, APIService,
                        "Content downloaded in "
                                + (System.currentTimeMillis() - start) + "ms"
                );


                // Read response
                response = this.readWebResponse(httpResponse, service);
            } catch (Exception ex) {
                Logger.log(ILoggingLogLevel.DEBUG, APIService,
                        "Unnandled Exception requesting service. " + ex);
                response.setContentType(contentTypes.get(IServiceType.ServiceTypeRestJson)
                        .toString());
                response.setContent("Unhandled Exception Requesting Service. Message: " + ex.getMessage());
            } finally {
                // abort any previous timeout checking thread
                if (timeoutThread != null && timeoutThread.isAlive()) {
                    timeoutThread.interrupt();
                }
            }
        }

        Logger.log(ILoggingLogLevel.DEBUG, APIService, "invoke service finished");

        return response;
        */
        return null;
    }


    public void run() {
        if (this.validateCertificates()) {
            try {
                createHttpClients();
            } catch (KeyManagementException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (CertificateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (KeyStoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public void createHttpClients() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {

        SSLSocketFactory socketFactory;
        SchemeRegistry registry = new SchemeRegistry();

        Logger.log(ILoggingLogLevel.DEBUG, APIService,
                "Certificate Validation Enabled = " + this.validateCertificates());

        if (this.validateCertificates()) {
            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
            // Set verifier
            HttpsURLConnection
                    .setDefaultHostnameVerifier(hostnameVerifier);


            /********************************
             * USING DEFAULT ANDROID DEVICE SSLSocketFactory
             * the default factory was throwing errors verifying ssl certificates chains for some specific CA Authorities
             * (for example, Verisign root ceritificate G5 is not available on android devices <=2.3)
             * See more details on jira ticket [MOBPLAT-63]
             ********************************
             SSLSocketFactory socketFactory = SSLSocketFactory
             .getSocketFactory();
             socketFactory
             .setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
             */

			/*
            /********************************
			 * USING VALIDATING SSLSocketFactory - Validating certificates per demand
			 * See more details on jira ticket [MOBPLAT-63]
			 ********************************
			 */
            KeyStore trustStore;

            try {
                trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                String filename = "/system/etc/security/cacerts.bks".replace('/', File.separatorChar);
                FileInputStream is = new FileInputStream(filename);
                trustStore.load(is, "changeit".toCharArray());
                is.close();
            } catch (Exception ex) {
                try {
                    /*
                    /********************************
                     * HTC 2.3.5 Access Keystore problem
                     * See more details on jira ticket [MOBPLAT-91]
                     ********************************
                     */
                    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    String filename = "/system/etc/security/cacerts.bks".replace('/', File.separatorChar);
                    FileInputStream is = new FileInputStream(filename);
                    trustStore.load(is, null);
                    is.close();
                } catch (Exception e) {
                    trustStore = null;
                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "A problem has been detected while accessing the device keystore." + e);
                }
            }

            socketFactory = ValidatingSSLSocketFactory.GetInstance(trustStore);
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Using ValidatingSSLSocketFactory (custom socket Factory)");

        } else {
            /*
             * *******************************
			 * USING CUSTOM SSLSocketFactory - accept all certificates
			 * See more details on jira ticket [MOBPLAT-63]
			 ********************************
			*/
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            socketFactory = new MySSLSocketFactory(trustStore);

            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Using MySSLSocketFactory (custom socket factory - accepting all certificates)");
        }

        registry.register(new Scheme("https", socketFactory, 443));
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(
                new DefaultHttpClient().getParams(), registry);
        httpSSLClient = new DefaultHttpClient(mgr,
                new DefaultHttpClient().getParams());

        Logger.log(ILoggingLogLevel.DEBUG, APIService, "httpSSLClient stored for next HTTPS access");

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

    public static class ValidatingSSLSocketFactory extends SSLSocketFactory {
        protected static SSLContext sslContext;
        protected static Map<Integer, Long> myCertificateList;
        protected static ValidatingSSLSocketFactory singletonFactory;
        public String requestHostUri = null;

        private ValidatingSSLSocketFactory(final KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            sslContext = SSLContext.getInstance("TLS");
            myCertificateList = new HashMap<Integer, Long>();

            Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());

            TrustManager tm = new X509TrustManager() {

                private char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

                public X509Certificate[] getAcceptedIssuers() {
                    try {
                        X509Certificate[] returnIssuers = new X509Certificate[truststore.size()];
                        Enumeration aliases = truststore.aliases();

                        int i = 0;
                        while (aliases.hasMoreElements()) {
                            returnIssuers[i] = (X509Certificate) truststore.getCertificate((String) aliases.nextElement());
                            //Logger.log( ILoggingLogLevel.DEBUG,APIService,"TRUSTED CERT " + i + ": NAME " + returnIssuers[i].getSubjectDN().getName() + " ;ISSUER NAME: " + returnIssuers[i].getIssuerDN().getName());
                            i++;
                        }
                        return returnIssuers;
                    } catch (Exception ex) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Couldnt retrieve Accepted Issuers");
                    }
                    return null;

                }

                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    boolean bErrorsFound = false;
                    try {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Starting Certificate Validation process");
                        if (chain != null && chain.length > 0)
                            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate chain elements: " + chain.length);
                        //check the certificate is in memory
                        X509Certificate endCert = chain[0];
                        if (!certificateIsTheSame(endCert)) {
                            if (certChainIsValid(endCert, chain)) {
                                /* Checking only last certificate in the chain
                                for(int i=0; i< chain.length; i++){
		            				X509Certificate chainCert = chain[i];
		            				Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Certificate Name: " + chainCert.getSubjectDN().getName() + " ;SN: " + chainCert.getSerialNumber().toString() );

				            		X509CertParser parser = new X509CertParser();
				            		ByteArrayInputStream bais = new ByteArrayInputStream(chainCert.getEncoded());
				    	            parser.engineInit(bais);
				    	            bais.close();

				            		X509CertificateObject cert = (X509CertificateObject)parser.engineRead();
				            		if(certIsValidNow(cert)){
				            			if(!certIsSelfSigned(cert)){
				            				//if(certChainIsValid(cert, chain)){
				            					//all checks went OK. Add certificate to memory with current date and time
				            					//myCertificateList.put(Integer.valueOf(chain[0].hashCode()), Long.valueOf(System.currentTimeMillis()));
				            					Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Certificate is Valid");
				            				//}
				            			}else{
				            				bErrorsFound = true;
				            				Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Certificate is Self Signed");
				            			}
					            	}else{
					            		bErrorsFound = true;
					            		Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Certificate is expired");
					            	}
		            			}
		            			*/

                                // ThHis magic is needed to verify self-signed
                                X509CertParser parser = new X509CertParser();
                                ByteArrayInputStream bais = new ByteArrayInputStream(endCert.getEncoded());
                                parser.engineInit(bais);
                                bais.close();

                                X509CertificateObject cert = (X509CertificateObject) parser.engineRead();
                                if (certIsValidNow(cert)) {
                                    if (!certIsSelfSigned(cert)) {


                                        if (ServiceDelegate.validateFingerprints()) {
                                            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: VALIDATING FINGERPRINT");
                                            if (verifyFingerprint(endCert)) {
                                                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate Fingerprint is valid");
                                            } else {
                                                bErrorsFound = true;
                                                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate Fingerprint not valid");
                                            }
                                        } else {
                                            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: DO NOT VALIDATE FINGERPRINT");
                                        }
                                    } else {
                                        bErrorsFound = true;
                                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is Self Signed");
                                    }
                                } else {
                                    bErrorsFound = true;
                                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is expired");
                                }

                                if (!bErrorsFound)
                                    myCertificateList.put(Integer.valueOf(chain[0].hashCode()), Long.valueOf(System.currentTimeMillis()));
                            } else {
                                bErrorsFound = true;
                                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate Chain is not valid");
                            }
                        } else {
                            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Trusted Certificate");
                        }

                    } catch (StreamParsingException e) {
                        bErrorsFound = true;
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate chain error");
                    } catch (IOException e) {
                        bErrorsFound = true;
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Error in certificate chain");
                    } catch (Exception e) {
                        bErrorsFound = true;
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Unhandled error: " + e.getMessage());
                    }
                    if (bErrorsFound) {
                        throw new CertificateException("Certificate Validation: Errors found in the Certificate Chain");
                    }
                }

                /**
                 * Checks the certificate fingerprint is the expected
                 * @param endCert
                 * 				Certificate to check
                 * @return
                 * 				True if the certificate fingerprint was the expected one; false otherwise
                 */
                private boolean verifyFingerprint(X509Certificate endCert) {
                    try {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "ValidatingSSLSocketFactory - verifyFingerprint requestHostUri: " + requestHostUri);
                        MessageDigest md;
                        md = MessageDigest.getInstance("SHA1");
                        md.update(endCert.getEncoded());
                        String thumbprint = dumpHex(md.digest());


                        String[] fingerprint = null;
                        //TODO getFingerprints
                        /*if (ServiceImpl.getFingerprint().containsKey(requestHostUri)) {
                            fingerprint = ServiceImpl.getFingerprint().get(requestHostUri);
                            //Logger.log( ILoggingLogLevel.DEBUG,APIService,"******** Certificate Validation: requestHostUri ["+ requestHostUri +"] fingerprint "+ Arrays.toString(FINGERPRINTS.get(requestHostUri)));
                        }*/


                        if (fingerprint != null) {
                            //Logger.log( ILoggingLogLevel.DEBUG,APIService,"******** Certificate Validation: allowed fringerprint: " + Arrays.toString(fingerprint));
                            //Logger.log( ILoggingLogLevel.DEBUG,APIService,"******** Certificate Validation: tocheck fringerprint: ["+thumbprint+"]");

                            if (!Arrays.asList(fingerprint).contains(thumbprint)) {

                                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: WRONG CERTIFICATE FINGERPRINT");
                                return false;
                            }
                        } else {
                            Logger.log(ILoggingLogLevel.DEBUG, APIService, "WARNING Certificate Validation: NO FINGERPRINT FOUND (you should provide a valid fingerprint in your io-services-config.xml in order to validate HTTPS web certificates)");
                            return false;
                        }
                        return true;
                    } catch (NoSuchAlgorithmException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (CertificateEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                    return false;

                }


                /**
                 * Get certificate fringerprint
                 * @param data
                 * @return fingerprint
                 */
                private String dumpHex(byte[] data) {
                    final int n = data.length;
                    final StringBuilder sb = new StringBuilder(n * 3 - 1);
                    for (int i = 0; i < n; i++) {
                        if (i > 0) {
                            sb.append(' ');
                        }
                        sb.append(HEX_CHARS[(data[i] >> 4) & 0x0F]);
                        sb.append(HEX_CHARS[data[i] & 0x0F]);
                    }
                    return sb.toString();
                }


                /**
                 * Check the certificate is in memory and is still valid
                 * @param cert
                 * 				Certificate to check
                 * @return
                 * 				True if certificate is valid and in memory, otherwise false
                 */
                private boolean certificateIsTheSame(X509Certificate cert) {
                    removeTimedOutCertificates();
                    if (!myCertificateList.isEmpty() &&
                            myCertificateList.containsKey(Integer.valueOf(cert.hashCode())) &&
                            certIsValidNow(cert)) {
                        return true;
                    } else {
                        return false;
                    }
                }

                /**
                 * Remove the certificates that have been accessed and visited after 10 minutes from memory
                 */
                private void removeTimedOutCertificates() {
                    long currentTimeMillis = System.currentTimeMillis();
                    Set<Integer> certsToRemove = new HashSet<Integer>();
                    if (!myCertificateList.isEmpty()) {
                        for (Map.Entry<Integer, Long> listEntry : myCertificateList.entrySet()) {
                            long certAccessTime = listEntry.getValue().longValue();
                            if (TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis - certAccessTime) >= 600) {
                                certsToRemove.add(listEntry.getKey());
                            }
                        }
                        myCertificateList.keySet().removeAll(certsToRemove);
                    }
                }

                /**
                 * Checks the Certificate Chain is well formed and certificates not revoked
                 * @param cert
                 * 				End certificate the request is consuming
                 * @param chain
                 * 				Certificate chain containing the rest of certificates
                 * @return
                 * 				True if the Certificate chain is valid, otherwise False
                 */
                private boolean certChainIsValid(X509Certificate cert, X509Certificate[] chain) {
                    // DO NOT check OCSP revocation URLs. The time consuming this is expensive.
                    // TODO make this configurable and asynchronously in the case of enabled
                    // !verifyCertificateOCSP(chain)  ---> ASYNC
                    //if(!verifyCertificateOCSP(chain)){
                    Logger.log(ILoggingLogLevel.DEBUG, APIService,
                            "*************** OCSP Verification (certificate revocation check) is DISABLED for this build");
                    try {
                        //Selector to point out the end certificate
                        X509CertSelector selector = new X509CertSelector();
                        selector.setCertificate(cert);

                        //Trust anchor to point out the root certificate
                        Set<TrustAnchor> trust = new HashSet<TrustAnchor>();
                        trust.add(new TrustAnchor(chain[chain.length - 1], null));

                        //Params containing the selector, trust anchors and certificate chain. We disable CRL checks
                        PKIXBuilderParameters pParams = new PKIXBuilderParameters(trust, selector);
                        pParams.setRevocationEnabled(false);
                        Set<X509Certificate> setchain = new HashSet<X509Certificate>();
                        for (X509Certificate SCCert : chain) {
                            setchain.add(SCCert);
                        }
                        CertStore allcerts = CertStore.getInstance("Collection", new CollectionCertStoreParameters(setchain));
                        pParams.addCertStore(allcerts);

                        //Create the cert path result, if no exception is thrown means all went OK
                        CertPathBuilder builder = CertPathBuilder.getInstance("PKIX");
                        PKIXCertPathBuilderResult result = (PKIXCertPathBuilderResult) builder.build(pParams);
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate Path is valid");
                        return true;
                    } catch (CertPathBuilderException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Errors found in the Certificate Path ");
                    } catch (NoSuchAlgorithmException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Errors found in the Certificate Path ");
                    } catch (InvalidAlgorithmParameterException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Errors found in the Certificate Path ");
                    }
                    //}else{return false;}
                    return false;
                }


                /**
                 * Checks that all the certificates in the chain are not revoked. The checks are made via OCSP
                 * @param chain
                 * 				Certificate chain to check
                 * @return
                 * 				True if any certificate is Revoked, otherwise False
                 */
                private boolean verifyCertificateOCSP(X509Certificate[] chain) {
                    ArrayList<URL> certsUrls = new ArrayList<URL>();
                    ArrayList<OCSPReq> requestList = new ArrayList<OCSPReq>();
                    boolean bCertificateIsRevoked = false;
                    try {
                        for (X509Certificate cert : chain) {
                            //Read the OCSP extension from the certificates
                            byte[] extensionBytes = cert.getExtensionValue(X509Extensions.AuthorityInfoAccess.getId());
                            if (extensionBytes != null && extensionBytes.length > 0) {

                                DEROctetString derObjAccessDescriptors = (DEROctetString) ASN1Primitive.fromByteArray(extensionBytes);
                                ASN1Primitive primitiveObject = ASN1Primitive.fromByteArray(derObjAccessDescriptors.getOctets());
                                AccessDescription[] descriptors = AuthorityInformationAccess.getInstance(primitiveObject).getAccessDescriptions();
                                //If the URL is already in the list do not add it
                                if (descriptors != null && descriptors.length > 0) {
                                    String urlContent = descriptors[0].getAccessLocation().getName().toString();
                                    if (urlContent.startsWith("http://")) {
                                        URL url = new URL(urlContent);
                                        if (!certsUrls.contains(url)) certsUrls.add(url);
                                    }
                                }
                            }
                        }

                        /*String mess = "";
                        if (!certsUrls.isEmpty()) {
                            //Create the OCSP request content for each certificate
                            OCSPReqBuilder OCSPRequestGenerator;
                            for (int i = 0; i < (chain.length - 1); i++) {
                                X509Certificate x509Cert = (X509Certificate)chain[i + 1];
                                BigInteger serial = x509Cert.getSerialNumber();
                                OCSPReq ocspreq = OCSPManager.generateOCSPRequest(x509Cert,serial);

                                Socket s = new Socket("localhost", 5555);
                                ObjectOutputStream stream = new ObjectOutputStream(s.getOutputStream());
                                stream.writeObject(ocspreq);
                                stream.flush();

                                ObjectInputStream instream = new ObjectInputStream(s.getInputStream());
                                OCSPResp response = (OCSPResp) instream.readObject();
                                //TODO GET CA CERT
                                //mess = OCSPManager.analyseResponse(response, ocspreq, caCert);



                                if (mess.endsWith("good")) {
                                    Logger.log(ILoggingLogLevel.INFO, APIService, "Certificate: " + serial + " is valid !");
                                }else {
                                    throw new CertPathValidatorException("exception verifying certificate: " + serial);
                                    //TODO REMOVE COMMENT
                                    //bCertificateIsRevoked = true;
                                }
                            }

                        }*/
                    } catch (IOException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Errors found in the Certificate OCSP Validation ");
                        bCertificateIsRevoked = true;
                    } /*catch (OCSPException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Errors found in the Certificate OCSP Validation");
                        bCertificateIsRevoked = true;
                    } */ catch (Exception e) {
                        e.printStackTrace();
                    }
                    return bCertificateIsRevoked;
                }

                /**
                 * Checks the certificate is valid at the current date and time
                 * @param cert
                 * 				Certificate to check
                 * @return
                 * 				True if is valid, otherwise false
                 */
                private boolean certIsValidNow(X509Certificate cert) {
                    try {
                        cert.checkValidity();
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is not expired");
                        return true;
                    } catch (CertificateExpiredException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is expired");
                    } catch (CertificateNotYetValidException e) {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is not yet valid");
                    }
                    return false;
                }

                /**
                 * Checks the certificate is self signed
                 * @param cert
                 * 				Certificate to check
                 * @return
                 * 				True if the certificate is self signed, otherwise False
                 */
                private boolean certIsSelfSigned(X509Certificate cert) {
                    try {
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: checking if self-signed");
                        cert.verify(cert.getPublicKey());
                        Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is self-signed");
                        for (X509Certificate trustedCert : this.getAcceptedIssuers()) {
                            //If trusted and cert have same name
                            if (trustedCert.getSubjectX500Principal().getName().equals(cert.getIssuerX500Principal().getName())) {
                                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Trusted self-signed found.");
                                return false;
                            }
                        }
                        /*
                        //ROOT TOKEN READ
						List<String> rootAuth = null;
						boolean bLookForRoots = false;
						if(!_VALIDROOTAUTHORITIES.isEmpty() ){
							Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: List of Root Authorities Found");
							bLookForRoots = true;
							if(_VALIDROOTAUTHORITIES.indexOf(';')!=-1){
								rootAuth = Arrays.asList(_VALIDROOTAUTHORITIES.split("/\\;/g"));
								Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: List of Root Authorities Found : " + rootAuth.get(0));
							}else
							{
								rootAuth = Arrays.asList(new String[]{_VALIDROOTAUTHORITIES});
								Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: List of Root Authorities Found : " + rootAuth.get(0));
							}
						}
						/////////////

						// LOOK FOR TRUSTED ROOT CERTS AND TRUSTED ROOT ISSUER MUST CONTAIN A VALID NAME
						boolean bRootFound = false;
						for(X509Certificate trustedCert : this.getAcceptedIssuers()){
							//If trusted and cert have same name
							if(trustedCert.getSubjectX500Principal().getName().equals(cert.getIssuerX500Principal().getName())){
								//if we have a list of valid Root Authorities
								if(bLookForRoots){
									//If list is filled
									if(rootAuth!= null){
										Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Checking Valid Root Authorities");
										for(String caRootName : rootAuth){
											caRootName = caRootName.replace(';', ' ').trim().toLowerCase();
											if(!caRootName.isEmpty() && trustedCert.getIssuerX500Principal().getName().toLowerCase().contains(caRootName)) {
												bRootFound = true;
												Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Valid Authority found");
												//Found a match, do not look for more
												break;
											}
										}
									}else{
										//SOMETHING WEIRD. CANNOT LOOK FOR AUTHORITIES AND NOT HAVING AUTHORITIES
										bRootFound = false;
										Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Inconsistency. Cannot look for Roots and not having a list");
										//END THE LOOP AND GIVE IT AS A BAD ATTEMPT
										break;
									}
								}else{
									//NOT LOOKING FOR AUTHORITIES, WE FOUND A MATCH WITH NAMES
									bRootFound = true;
									Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: All authorities Valid");
								}

								if(bRootFound){
									Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate Validation: Trusted Certificate found");
									return false;
								}
							}
						}
						/*////////////////

                        return true;
                    } catch (InvalidKeyException e) {
                    } catch (CertificateException e) {
                    } catch (NoSuchAlgorithmException e) {
                    } catch (NoSuchProviderException e) {
                    } catch (SignatureException e) {
                    }
                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Certificate is not self signed");
                    return false;
                }

	            /* NOT TO USE
                 * Reason: Takes too much time depending the CRL file. 1Mb Crl file download in 15 secs

				private boolean verifyCertificateCRLs(X509Certificate cert){
	            	boolean bContinueValidating = true;
	            	try{
	            		List<String> crlDistPoints = getCrlDistributionPoints(cert);
	            		if(crlDistPoints.size()>0){
			                crlDistPoints.get(0);
			                for (int i =0; i< crlDistPoints.size() && bContinueValidating;i++) {
			                	String crlDP = crlDistPoints.get(i);
			                    X509CRL crl = downloadCRL(crlDP);
			                    if (crl!= null && crl.isRevoked(cert)) {
			                    	bContinueValidating = false;
			                        Logger.log( ILoggingLogLevel.DEBUG,APIService,"Certificate " + cert.getSubjectX500Principal().getName() + " is revoked.");
			                    }
			                }
	            		}
		                return bContinueValidating;
	            	}catch(Exception e){
	            		e.printStackTrace();
	            	}
	            	return false;
	            }


	            private X509CRL downloadCRL(String crlURL) throws IOException,CertificateException, CRLException, NamingException {
	                if (crlURL.startsWith("http://") || crlURL.startsWith("https://") || crlURL.startsWith("ftp://")) {
	                	Logger.log( ILoggingLogLevel.DEBUG,APIService,"Downloading CRL from : " + crlURL);
	                    X509CRL crl = downloadCRLFromWeb(crlURL);
	                    return crl;
	                }
	                return null;
	            }

	            private X509CRL downloadCRLFromWeb(String crlURL) throws MalformedURLException, IOException, CertificateException,CRLException {
	                URL url = new URL(crlURL);
	                InputStream crlStream = null;
	                try {
	                	crlStream = url.openStream();
	                    CertificateFactory cf = CertificateFactory.getInstance("X.509");
	                    X509CRL crl = (X509CRL) cf.generateCRL(crlStream);
	                    return crl;
	                } finally {
	                    if(crlStream != null) crlStream.close();
	                }
	            }

	            private List<String> getCrlDistributionPoints(X509Certificate cert) throws IOException{
	                byte[] crldpExt = cert.getExtensionValue(X509Extensions.CRLDistributionPoints.getId());
	                if (crldpExt == null) {
	                    List<String> emptyList = new ArrayList<String>();
	                    return emptyList;
	                }
	                DEROctetString derObjCrlDP = (DEROctetString)ASN1Primitive.fromByteArray(crldpExt);
	                ASN1Primitive pp = ASN1Primitive.fromByteArray(derObjCrlDP.getOctets());
	                CRLDistPoint distPoint = CRLDistPoint.getInstance(pp);
	                List<String> crlUrls = new ArrayList<String>();
	                for (DistributionPoint dp : distPoint.getDistributionPoints()) {
	                    DistributionPointName dpn = dp.getDistributionPoint();
	                    // Look for URIs in fullName
	                    if (dpn != null) {
	                        if (dpn.getType() == DistributionPointName.FULL_NAME) {
	                            GeneralName[] genNames = GeneralNames.getInstance(dpn.getName()).getNames();
	                            // Look for an URI
	                            for (int j = 0; j < genNames.length; j++) {
	                                if (genNames[j].getTagNo() == GeneralName.uniformResourceIdentifier) {
	                                    String url = DERIA5String.getInstance(genNames[j].getName()).getString();
	                                    crlUrls.add(url);
	                                }
	                            }
	                        }
	                    }
	                }
	                return crlUrls;
	            }
	             */


            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        public static ValidatingSSLSocketFactory GetInstance(KeyStore truststore) {
            if (singletonFactory == null) {
                try {
                    singletonFactory = new ValidatingSSLSocketFactory(truststore);
                } catch (KeyManagementException e) {
                    // TODO Auto-generated catch block
                    return null;
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    return null;
                } catch (KeyStoreException e) {
                    // TODO Auto-generated catch block
                    return null;
                } catch (UnrecoverableKeyException e) {
                    // TODO Auto-generated catch block
                    return null;
                }
            }
            return singletonFactory;
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            requestHostUri = host;
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "ValidatingSSLSocketFactory - Create Socket for host requestHostUri: " + requestHostUri);
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }


    private class CheckTimeoutThread implements Runnable {

        private final long ABSOLUTE_INVOKE_TIMEOUT = 60000; // 60 seconds
        private HttpUriRequest httpRequest = null;

        public CheckTimeoutThread(HttpUriRequest httpRequest) {
            super();
            this.httpRequest = httpRequest;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(ABSOLUTE_INVOKE_TIMEOUT);

                Logger.log(ILoggingLogLevel.DEBUG, APIService, "*** INVOKE SERVICE TIMEOUT *** Absolute timeout checking completed.");
                if (httpRequest != null) {
                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "*** INVOKE SERVICE TIMEOUT *** Aborting request...");
                    httpRequest.abort();
                }

            } catch (InterruptedException e) {
                Logger.log(ILoggingLogLevel.DEBUG, APIService, "*** INVOKE SERVICE TIMEOUT *** Absolute timeout checking interrupted.");
            }

        }
    }

    public class HttpAdaptive extends HttpEntityEnclosingRequestBase {

        private String method = null;

        public HttpAdaptive(final URI uri, String method) {
            this.setURI(uri);
            this.method = method;
        }

        @Override
        public String getMethod() {
            return method;
        }
    }

    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "Certificate Validation: Accepting all certificates");
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    /**
     * This response interceptor is used to inflate GZIp responses
     *
     * @author maps
     *         [MOBPLAT-200]: allow gzip, deflate decompression modes
     */
    private class GzipHttpResponseInterceptor implements HttpResponseInterceptor {

        @Override
        public void process(final HttpResponse response, final HttpContext context) {
            final HttpEntity entity = response.getEntity();
            final Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                Logger.log(ILoggingLogLevel.DEBUG, APIService, "Response has content-enconding headers #"
                        + ((encoding.getElements() != null) ? encoding.getElements().length : "NULL"));
                inflateGzip(response, encoding);
            }
        }

        private void inflateGzip(final HttpResponse response, final Header encoding) {
            for (HeaderElement element : encoding.getElements()) {
                if (element.getName().equalsIgnoreCase("gzip")) {
                    Logger.log(ILoggingLogLevel.DEBUG, APIService, "Response GZIP Encoding found. Inflating response content...");
                    response.setEntity(new GzipInflatingEntity(response.getEntity()));
                    break;
                }
            }
        }
    }

    /**
     * Inflated Response Entity
     *
     * @author maps
     *         [MOBPLAT-200]: allow gzip, deflate decompression modes
     */
    private class GzipInflatingEntity extends HttpEntityWrapper {
        public GzipInflatingEntity(final HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            Logger.log(ILoggingLogLevel.DEBUG, APIService, "Returning response entity as GZIPInputStream");
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

    private class ServiceTask extends AsyncTask<Object, Void, ServiceResponse> {
        IServiceResultCallback cb;

        @Override
        protected ServiceResponse doInBackground(Object... params) {
            ServiceRequest req = (ServiceRequest) params[0];
            Service serv = (Service) params[1];
            cb = (IServiceResultCallback) params[2];

            ServiceResponse response = invokeService(req, serv);


            return response;
        }

        @Override
        protected void onPostExecute(ServiceResponse response) {
            super.onPostExecute(response);

            cb.onResult(response);
            //cb.onResult(fromHttpResponse(response));
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            cb.onError(IServiceResultCallbackError.ServerError);
        }
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
