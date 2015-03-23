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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseApplicationDelegate;
import me.adaptive.arp.api.IGlobalization;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.KeyPair;
import me.adaptive.arp.api.Locale;

/**
 * Interface for Managing the Globalization results
 * Auto-generated implementation of IGlobalization specification.
 */
public class GlobalizationDelegate extends BaseApplicationDelegate implements IGlobalization {

    protected static final String I18N_CONFIG_FILE = "config/i18n-config.xml";
    protected static final String APP_CONFIG_PATH = "config";
    protected static final String PLIST_EXTENSION = ".plist";
    protected static final String ENCODING = "UTF-8";
    protected static final String DEFAULT_LOCALE_TAG = "default";
    protected static final String SUPPORTED_LOCALE_TAG = "supportedLanguage";
    protected static final String SUPPORTED_LOCALES_TAG = "supportedLanguages";
    protected static final String LANGUAGE_ATTR = "language";
    protected static final String COUNTRY_ATTR = "country";

    public static String APIService = "globalization";
    static LoggingDelegate Logger;

    /**
     * Default Constructor.
     */
    public GlobalizationDelegate() {
        super();
        Logger = ((LoggingDelegate) AppRegistryBridge.getInstance().getLoggingBridge().getDelegate());

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
     * Returns the default locale of the application defined in the configuration file
     *
     * @return Default Locale of the application
     * @since ARP1.0
     */
    public Locale getDefaultLocale() {
        Locale response;
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":getDefaultLocale");
        // return response;
    }

    /**
     * List of supported locales for the application defined in the configuration file
     *
     * @return List of locales
     * @since ARP1.0
     */
    public Locale[] getLocaleSupportedDescriptors() {
        Map<String, String> result = null;
        List<Locale> supported = new ArrayList<>();

        Logger.log(ILoggingLogLevel.Debug, APIService, "getLocaleSupportedDescriptors");

        BufferedInputStream bis = null;
        try {
            Context context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
            AssetManager assetManager = context.getAssets();
            bis = new BufferedInputStream(assetManager.open(I18N_CONFIG_FILE));
            // parse configuration file
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(bis, ENCODING);
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                if (event == XmlPullParser.START_TAG) {
                    if (DEFAULT_LOCALE_TAG.equalsIgnoreCase(parser.getName())) {
                        // default locale
                        String defaultLanguage = parser.getAttributeValue(null,
                                LANGUAGE_ATTR);
                        String defaultCountry = parser.getAttributeValue(null,
                                COUNTRY_ATTR);
                    } else if (SUPPORTED_LOCALE_TAG.equalsIgnoreCase(parser
                            .getName())) {
                        // supported locale
                        String language = parser.getAttributeValue(null,
                                LANGUAGE_ATTR);
                        String country = parser.getAttributeValue(null,
                                COUNTRY_ATTR);

                        supported.add(new Locale(language, (country != null ? country : "")));
                    }
                }
                event = parser.next();
            }
        } catch (Exception ex) {
            Logger.log(ILoggingLogLevel.Error, APIService, "Error: " + ex.getLocalizedMessage());
            return null;
        } finally {
            closeStream(bis);
        }

        return supported.toArray(new Locale[supported.size()]);
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
        String response;
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":getResourceLiteral");
        // return response;
    }

    /**
     * Gets the full application configured literals (key/message pairs) corresponding to the given locale.
     *
     * @param locale The locale object to get localized message, or the locale desciptor ("language" or "language-country" two-letters ISO codes.
     * @return Localized texts in the form of an object.
     * @since ARP1.0
     */
    public KeyPair[] getResourceLiterals(Locale locale) {
        Map<String, String> result = null;
        BufferedInputStream bis = null;

        try {
            Context context = ((AppContextDelegate) AppRegistryBridge.getInstance().getPlatformContext().getDelegate()).getMainActivity().getApplicationContext();
            AssetManager assetManager = context.getAssets();
            bis = new BufferedInputStream(assetManager.open(getResourcesFilePath(locale.toString())));
            PList plist = PListParser.parse(bis);
            result = new HashMap<String, String>();
            result.putAll(plist.getValues());
        } catch (IOException ex) {
            Logger.log(ILoggingLogLevel.Error, APIService, "GetResourceLiterals Error: " + ex.getLocalizedMessage());
        } finally {
            closeStream(bis);

        }
        Logger.log(ILoggingLogLevel.Error, APIService, "GetResourceLiterals: " + result.toString());
        return fromMap(result);
    }

    /**
     * get the absolute path for resources
     *
     * @param localeDescriptor language
     * @return The string with the path
     */
    private String getResourcesFilePath(String localeDescriptor) {
        return APP_CONFIG_PATH + "/" + localeDescriptor + PLIST_EXTENSION;
    }

    /**
     * Convert a Map object to KeyPair[]
     *
     * @param p Map object
     * @return The resulting KeyPair[]
     */
    private KeyPair[] fromMap(Map<String, String> p) {
        List<KeyPair> result = new ArrayList<KeyPair>();
        for (Map.Entry<String, String> entry : p.entrySet()) {
            result.add(new KeyPair(entry.getKey(), entry.getValue()));
        }

        return result.toArray(new KeyPair[result.size()]);

    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
