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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import me.adaptive.arp.api.IServiceMethod;
import me.adaptive.arp.api.IServiceType;
import me.adaptive.arp.api.Locale;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceEndpoint;
import me.adaptive.arp.api.ServicePath;

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

    public String APIService = "XmlParser";
    private List<Locale> locales;

    private static XmlParser instance = null;
    protected XmlParser() {
        // Exists only to defeat instantiation.
    }
    public static XmlParser getInstance() {
        if(instance == null) {
            instance = new XmlParser();
        }
        return instance;
    }


    public Document parseXml(InputStream xml, InputStream xsd) throws IOException, ParserConfigurationException, SAXException {


        // parse an XML document into a DOM tree
        DocumentBuilderFactory parserFactory = DocumentBuilderFactory.newInstance();
        parserFactory.setNamespaceAware(true);
        DocumentBuilder parser = parserFactory.newDocumentBuilder();

        Document document = parser.parse(xml);

        return document;

    }

    public List<Locale> getLocaleData(Document document, String tag) throws ParserConfigurationException, SAXException, IOException {

        locales = new ArrayList<>();


        Element docEle = document.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName(tag);
        if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
                //get the employee element
                Element el = (Element)nl.item(i);
                //get the Employee object
                Locale locale = getLocale(el);
                locales.add(locale);
            }
        }
        return locales;
    }


    /**
     * Returns a Locale from xml element
     * @param empEl containing the data
     * @return a Locale
     */
    private Locale getLocale(Element empEl) {

        String country = empEl.getAttribute(COUNTRY_ATT);
        String language = empEl.getAttribute(LANGUAGE_ATT);

        //Create a new Locale with the value read from the xml nodes
        return new Locale(language,country);

    }

    public List<Service> getIOData(Document document) throws ParserConfigurationException, SAXException, IOException {
        List<Service> services = new ArrayList<>();


        Element docEle = document.getDocumentElement();

        NodeList nl = docEle.getElementsByTagName(SERVICE_TAG);
        if (nl != null && nl.getLength() > 0) {
            for (int i = 0; i < nl.getLength(); i++) {

                Element el = (Element) nl.item(i);

                services.add(getService(el));

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
        if (nl != null && nl.getLength() > 0) {
            Element ele = (Element) nl.item(0);
            paths.add(getPath(ele));
        }

        return new ServiceEndpoint(el.getAttribute(HOST_ATT), paths.toArray(new ServicePath[paths.size()]));
    }

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


    private File createFileFromInputStream(InputStream inputStream, String filename) {

        try{
            File f = new File(filename);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=inputStream.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        }catch (IOException e) {
            //Logging exception
        }

        return null;
    }

    public static boolean validateXMLSchema(InputStream xmlPath, InputStream xsdPath){

        try {
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);//
            Schema schema = factory.newSchema();
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlPath));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        return true;
    }

}
