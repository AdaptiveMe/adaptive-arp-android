package me.adaptive.arp.common.parser.xml;/*
 * =| ADAPTIVE RUNTIME PLATFORM |=======================================================================================
 *
 * (C) Copyright 2013-2014 Carlos Lozano Diez t/a Adaptive.me <http://adaptive.me>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Original author:
 *
 *     * Carlos Lozano Diez
 *                 <http://github.com/carloslozano>
 *                 <http://twitter.com/adaptivecoder>
 *                 <mailto:carlos@adaptive.me>
 *
 * Contributors:
 *
 *     * Francisco Javier Martin Bueno
 *             <https://github.com/kechis>
 *             <mailto:kechis@gmail.com>
 *
 * =====================================================================================================================
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.IServiceMethod;
import me.adaptive.arp.api.IServiceType;
import me.adaptive.arp.api.Locale;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceEndpoint;
import me.adaptive.arp.api.ServicePath;
import me.adaptive.arp.api.ServiceToken;
import me.adaptive.arp.common.core.AppResourceManager;
import me.adaptive.arp.common.parser.plist.PList;
import me.adaptive.arp.common.parser.plist.PListParser;


public class XmlParser {

    private static String COUNTRY_ATT = "country";
    private static String LANGUAGE_ATT = "language";

    private static String SERVICE_TAG = "service";

    private static final String NAME_ATT = "name";
    private static final String ENDPOINT_TAG = "end-point";

    private static final String HOST_ATT = "host";
    private static final String validation_ATT = "validation";

    private static final String PATH_TAG = "path";
    private static final String PATH_ATT = "path";
    private static final String TYPE_ATT = "type";

    private static final String METHOD_TAG = "method";
    private static final String METHOD_ATT = "method";


    private static String RESOURCE_TAG = "resource";
    private static String URL_ATT = "url";


    private static final String APP_DEFINITIONS_CONFIG_PATH = "definitions/";

    private static final String IO_CONFIG_FILENAME = "io-config.xml";

    private static final String IO_CONFIG_DEFINITION_FILENAME = APP_DEFINITIONS_CONFIG_PATH + "i18n-config.xsd";


    private static final String I18N_CONFIG_FILENAME = "i18n-config.xml";

    private static final String I18N_DEFINITIONS_CONFIG_FILENAME = APP_DEFINITIONS_CONFIG_PATH + "i18n-config.xsd";


    private static final String PLIST_EXTENSION = ".plist";
    private static final String DEFAULT_LOCALE_TAG = "default";
    private static final String SUPPORTED_LOCALE_TAG = "supportedLanguage";

    // logger
    private static final String LOG_TAG = "DeviceDelegate";
    private static ILogging logger = AppRegistryBridge.getInstance().getLoggingBridge();


    private List<Locale> locales;

    private static XmlParser instance = null;

    protected XmlParser() {
        // Exists only to defeat instantiation.
        initialize();
    }

    public static XmlParser getInstance() {
        if (instance == null) {
            instance = new XmlParser();
        }
        return instance;
    }

    private List<String> resources = null;
    private Map<String, Service> services = null;

    private Locale defaultLocale;
    private List<Locale> supportedLocale = null;
    private Map<String, PList> i18nData = null;

    public List<Locale> getLocales() {
        return locales;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public List<Locale> getSupportedLocale() {
        return supportedLocale;
    }

    public Map<String, PList> getI18nData() {
        return i18nData;
    }

    public List<String> getResources() {
        return resources;
    }

    public Map<String, Service> getServices() {
        return services;
    }

    private void initialize() {
        services = new HashMap<>();
        resources = new ArrayList<>();

        supportedLocale = new ArrayList<>();
        i18nData = new HashMap<String, PList>();
        InputStream plistIS = null, origin = null, validator = null;

        try {

            origin = new ByteArrayInputStream(AppResourceManager.getInstance().retrieveConfigResource(IO_CONFIG_FILENAME).getData());
            validator = new ByteArrayInputStream(AppResourceManager.getInstance().retrieveConfigResource(IO_CONFIG_DEFINITION_FILENAME).getData());


//            if (validate(origin, validator)) {
//                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "VALID");
//            } else logger.log(ILoggingLogLevel.Error, LOG_TAG, "INVALID");


            Document document = this.parseXml(origin, validator);
            resources = this.getResourceData(document);
            services = this.getIOData(document);


            origin = new ByteArrayInputStream(AppResourceManager.getInstance().retrieveConfigResource(I18N_CONFIG_FILENAME).getData());
            validator = new ByteArrayInputStream(AppResourceManager.getInstance().retrieveConfigResource(I18N_DEFINITIONS_CONFIG_FILENAME).getData());

//            if (validate(origin, validator)) {
//                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "VALID");
//            } else logger.log(ILoggingLogLevel.Error, LOG_TAG, "INVALID");

            document = this.parseXml(origin, validator);
            defaultLocale = this.getLocaleData(document, DEFAULT_LOCALE_TAG).get(0);
            supportedLocale = this.getLocaleData(document, SUPPORTED_LOCALE_TAG);


            for (Locale locale : supportedLocale) {
                plistIS = new ByteArrayInputStream(AppResourceManager.getInstance().retrieveConfigResource(getResourcesFilePath(locale)).getData());
                PList plist = PListParser.getInstance().parse(plistIS);
                i18nData.put(localeToString(locale), plist);
            }
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error Opening xml - Error: " + e.getLocalizedMessage());
        } catch (ParserConfigurationException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error Parsing xml - Error: " + e.getLocalizedMessage());
        } catch (SAXException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error Validating xml - Error: " + e.getLocalizedMessage());
        } finally {
            closeStream(plistIS);
            closeStream(origin);
            closeStream(validator);

        }
    }

    /**
     * Return the Document parsed from InputStream
     * @param xml origin InputStream
     * @param xsd validation InputStream
     * @return Document parsed
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Document parseXml(InputStream xml, InputStream xsd) throws IOException, ParserConfigurationException, SAXException {

        // parse an XML document into a DOM tree
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        DocumentBuilder parser = parserFactory.newDocumentBuilder();

        Document document = parser.parse(xml);

        return document;

    }

    /**
     * Returns the i18n locale data
     * @param document to read
     * @param tag node to read
     * @return locales data
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public List<Locale> getLocaleData(Document document, String tag) throws ParserConfigurationException, SAXException, IOException {

        locales = new ArrayList<>();


        Element docEle = document.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName(tag);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {
                //get the employee element
                Element el = (Element) nl.item(i);
                //get the Employee object
                Locale locale = getLocale(el);
                locales.add(locale);
            }
        }
        return locales;
    }


    /**
     * Returns a Locale from xml element
     *
     * @param empEl containing the data
     * @return a Locale
     */
    private Locale getLocale(Element empEl) {

        String country = empEl.getAttribute(COUNTRY_ATT);
        String language = empEl.getAttribute(LANGUAGE_ATT);

        //Create a new Locale with the value read from the xml nodes
        return new Locale(language, country);

    }

    /**
     * Returns the IO Data from a Document
     *
     * @param document Document
     * @return services data
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public Map<String, Service> getIOData(Document document) throws ParserConfigurationException, SAXException, IOException {
        //List<Service> services = new ArrayList<>();
        Map<String, Service> services = new HashMap<>();


        Element docEle = document.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName(SERVICE_TAG);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);
                Service serv = getService(el);
                services.put(serv.getName(), serv);

            }
        }
        return services;
    }

    /**
     * Returns Service from xml element
     *
     * @param empEl containing the data
     * @return Service
     */
    private Service getService(Element empEl) {
        List<ServiceEndpoint> endpoints = new ArrayList<>();
        NodeList nl = empEl.getElementsByTagName(ENDPOINT_TAG);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);

                endpoints.add(getEndPoint(el));
            }
        }

        String name = empEl.getAttribute(NAME_ATT);
        return new Service(endpoints.toArray(new ServiceEndpoint[endpoints.size()]), name);

    }

    /**
     * Returns and ServiceEndpoint from Xml element
     *
     * @param el xml element
     * @return ServiceEndpoint
     */
    private ServiceEndpoint getEndPoint(Element el) {

        List<ServicePath> paths = new ArrayList<>();
        NodeList nl = el.getElementsByTagName(PATH_TAG);
        for (int i = 0; i < nl.getLength(); i++) {
            Element ele = (Element) nl.item(i);
            paths.add(getPath(ele));
        }

        return new ServiceEndpoint(el.getAttribute(HOST_ATT), paths.toArray(new ServicePath[paths.size()]));
    }

    /**
     * Return the ServicePath from an Element
     *
     * @param el Element
     * @return ServicePath
     */
    private ServicePath getPath(Element el) {

        List<IServiceMethod> methods = new ArrayList<>();
        NodeList nl = el.getElementsByTagName(METHOD_TAG);
        if (nl != null && nl.getLength() > 0) {
            Element ele = (Element) nl.item(0);
            IServiceMethod method;
            switch (ele.getAttribute(METHOD_ATT).toUpperCase(java.util.Locale.ENGLISH)) {
                case "GET":
                    method = IServiceMethod.Get;
                    break;
                case "POST":
                    method = IServiceMethod.Post;
                    break;
                case "HEAD":
                    method = IServiceMethod.Head;
                    break;
                default:
                    method = IServiceMethod.Unknown;
            }
            methods.add(method);
        }

        IServiceType type;
        switch (el.getAttribute(TYPE_ATT).toUpperCase(java.util.Locale.ENGLISH)) {
            case "RESTJSON":
                type = IServiceType.RestJson;
                break;
            case "OCTETBINARY":
                type = IServiceType.OctetBinary;
                break;
            case "RESTXML":
                type = IServiceType.RestXml;
                break;
            case "SOAPXML":
                type = IServiceType.SoapXml;
                break;
            default:
                type = IServiceType.Unknown;
        }

        return new ServicePath(el.getAttribute(PATH_ATT), methods.toArray(new IServiceMethod[methods.size()]), type);

    }

    /**
     * Return all whitelisted resources url
     *
     * @param document source
     * @return Whitelisted urls
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public List<String> getResourceData(Document document) throws ParserConfigurationException, SAXException, IOException {
        List<String> resources = new ArrayList<>();


        Element docEle = document.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName(RESOURCE_TAG);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);

                resources.add(getResource(el));

            }
        }
        return resources;
    }

    /**
     * Return a Resource whitelist url from xml element
     *
     * @param el Xml element
     * @return url string
     */
    private String getResource(Element el) {
        return el.getAttribute(URL_ATT);
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
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing stream: " + ex.getLocalizedMessage());
        }
    }

    /**
     * get the absolute path for resources
     *
     * @param locale data
     * @return The string with the path
     */
    private String getResourcesFilePath(Locale locale) {
        return localeToString(locale) + PLIST_EXTENSION;
    }

    /**
     * Return the String representation of the Locale
     *
     * @param locale object
     * @return String
     */
    private String localeToString(Locale locale) {
        return locale.getLanguage() + "-" + locale.getCountry();
    }

    /**
     * Returns the content type for a ServiceToken
     *
     * @param serviceToken
     * @return IServiceType
     */
    public IServiceType getContentType(ServiceToken serviceToken) {
        if (services.containsKey(serviceToken.getServiceName())) {
            for (ServiceEndpoint serviceEndpoint : services.get(serviceToken.getServiceName()).getServiceEndpoints()) {
                for (ServicePath servicePath : serviceEndpoint.getPaths()) {
                    if (servicePath.getPath().equals(serviceToken.getFunctionName())) {
                        return servicePath.getType();
                    }
                }
            }
        }
        return null;
    }

    /* *
     * Validation method.
     *
     * @param xmlFilePath       The xml file we are trying to validate.
     * @param xmlSchemaFilePath The schema file we are using for the validation. This method assumes the schema file is valid.
     * @return True if valid, false if not valid or bad parse or exception/error during parse.
     * /
    private static boolean validate(InputStream xmlFilePath, InputStream xmlSchemaFilePath) {
        //TODO MAKE THE XSD VALIDATION
        // Try the validation, we assume that if there are any issues with the validation
        // process that the input is invalid.
        try {
            //SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            SchemaFactory factory = new XMLSchemaFactory();
            Source schemaFile = new StreamSource(createFileFromInputStream(xmlSchemaFilePath));
            Source xmlSource = new StreamSource(createFileFromInputStream(xmlFilePath));
            Schema schema = factory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlSource);
        } catch (SAXException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (Exception e) {
            // Catches everything beyond: SAXException, and IOException.

            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing stream: " + e.getLocalizedMessage());
            return false;
        } catch (Error e) {
            // Needed this for debugging when I was having issues with my 1st set of code.
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing stream: " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }*/

    /* Create a File from InputStream */
    private static File createFileFromInputStream(InputStream inputStream) {

        try {
            File f = new File(AppRegistryBridge.getInstance().getFileSystemBridge().getApplicationCacheFolder().getPathAbsolute().concat("/" + new Date().getTime() + ".cache"));
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing stream: " + e.getLocalizedMessage());
        }

        return null;
    }


    /* Create a File from a content String */
    public static void createFileFromString(String fileText, String fileName) {
        try {
            File file = new File(fileName);
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(fileText);
            output.close();
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "Error closing stream: " + e.getLocalizedMessage());
        }
    }


}
