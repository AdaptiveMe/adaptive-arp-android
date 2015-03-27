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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseApplicationDelegate;
import me.adaptive.arp.api.IGlobalization;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.KeyPair;
import me.adaptive.arp.api.Locale;
import me.adaptive.arp.common.parser.plist.PList;
import me.adaptive.arp.common.parser.plist.PListParser;
import me.adaptive.arp.common.parser.xml.XmlParser;

/**
 * Interface for Managing the Globalization results
 * Auto-generated implementation of IGlobalization specification.
 */
public class GlobalizationDelegate extends BaseApplicationDelegate implements IGlobalization {


    protected static final String APP_CONFIG_PATH = "app/config/";
    protected static final String APP_DEFINITIONS_CONFIG_PATH = "definitions/";
    protected static final String I18N_CONFIG_FILENAME = "i18n-config.xml";
    protected static final String I18N_CONFIG_FILE = APP_CONFIG_PATH+I18N_CONFIG_FILENAME;
    protected static final String I18N_DEFINITIONS_CONFIG_FILENAME = "i18n-config.xsd";
    protected static final String I18N_CONFIG_VALIDATOR_FILE = APP_CONFIG_PATH+APP_DEFINITIONS_CONFIG_PATH+I18N_DEFINITIONS_CONFIG_FILENAME;


    protected static final String PLIST_EXTENSION = ".plist";
    protected static final String DEFAULT_LOCALE_TAG = "default";
    protected static final String SUPPORTED_LOCALE_TAG = "supportedLanguage";

    public static String APIService = "globalization";
    static LoggingDelegate Logger;


    private Locale defaultLocale;
    private List<Locale> supportedLocale = null;
    private Map<String,PList> i18nData = null;
    /**
     * Default Constructor.
     */
    public GlobalizationDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

    }

    /**
     * Initialize all the relate objects
     */
    private void initialize(){
        supportedLocale = new ArrayList<>();
        i18nData = new HashMap<String, PList>();
        InputStream plistIS = null, origin = null ,validator = null;
        Context context;
        AssetManager assetManager;
        try {
            context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
            assetManager = context.getAssets();

            origin = assetManager.open(I18N_CONFIG_FILE);
            validator = assetManager.open(I18N_CONFIG_VALIDATOR_FILE);

            Document document = XmlParser.getInstance().parseXml(origin,validator);
            defaultLocale = XmlParser.getInstance().getLocaleData(document, DEFAULT_LOCALE_TAG).get(0);
            supportedLocale = XmlParser.getInstance().getLocaleData(document,SUPPORTED_LOCALE_TAG);


            for(Locale locale: supportedLocale){
                plistIS = assetManager.open(getResourcesFilePath(locale));
                PList plist = PListParser.getInstance().parse(plistIS);
                i18nData.put(localeToString(locale),plist);
            }
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
     * Returns the default locale of the application defined in the configuration file
     *
     * @return Default Locale of the application
     * @since ARP1.0
     */
    public Locale getDefaultLocale() {
        if(defaultLocale == null)
            initialize();
        return defaultLocale;
    }

    /**
     * List of supported locales for the application defined in the configuration file
     *
     * @return List of locales
     * @since ARP1.0
     */
    public Locale[] getLocaleSupportedDescriptors() {
        if(supportedLocale == null)
            initialize();
        return supportedLocale.toArray(new Locale[supportedLocale.size()]);
    }

    /**
     * Gets the text/message corresponding to the given key and locale.
     *
     * @param key    to match text
     * @param locale The locale object to get localized message, or the locale desciptor ("language" or "language-country" two-letters ISO codes.
     * @return Localized text.
     * @since ARP1.0
     */
    public String getResourceLiteral(String key, Locale locale) {
        if(i18nData == null)
            initialize();
        PList plist = i18nData.get(localeToString(locale));
        if(plist != null){
            return plist.getKey(key);
        }
        return null;

    }

    /**
     * Gets the full application configured literals (key/message pairs) corresponding to the given locale.
     *
     * @param locale The locale object to get localized message, or the locale desciptor ("language" or "language-country" two-letters ISO codes.
     * @return Localized texts in the form of an object.
     * @since ARP1.0
     */
    public KeyPair[] getResourceLiterals(Locale locale) {
        if(i18nData == null)
            initialize();
        return i18nData.get(locale).getKeyPair();
    }

    /**
     * get the absolute path for resources
     *
     * @param locale data
     * @return The string with the path
     */
    private String getResourcesFilePath(Locale locale) {
        return APP_CONFIG_PATH + localeToString(locale) + PLIST_EXTENSION;
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


    /**
     * Return the String representation of the Locale
     * @param locale object
     * @return String
     */
    private String localeToString(Locale locale) {
        return locale.getLanguage() + "-" + locale.getCountry();
    }
}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
