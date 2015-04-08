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

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseApplicationDelegate;
import me.adaptive.arp.api.IGlobalization;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.KeyPair;
import me.adaptive.arp.api.Locale;
import me.adaptive.arp.common.parser.plist.PList;
import me.adaptive.arp.common.parser.xml.XmlParser;

/**
 * Interface for Managing the Globalization results
 * Auto-generated implementation of IGlobalization specification.
 */
public class GlobalizationDelegate extends BaseApplicationDelegate implements IGlobalization {



    // logger
    private static final String LOG_TAG = "GlobalizationDelegate";
    private ILogging logger;



    /**
     * Default Constructor.
     */
    public GlobalizationDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
    }




    /**
     * Returns the default locale of the application defined in the configuration file
     *
     * @return Default Locale of the application
     * @since ARP1.0
     */
    public Locale getDefaultLocale() {
        return XmlParser.getInstance().getDefaultLocale();
    }

    /**
     * List of supported locales for the application defined in the configuration file
     *
     * @return List of locales
     * @since ARP1.0
     */
    public Locale[] getLocaleSupportedDescriptors() {
        return XmlParser.getInstance().getSupportedLocale().toArray(new Locale[XmlParser.getInstance().getSupportedLocale().size()]);
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

        PList plist = XmlParser.getInstance().getI18nData().get(localeToString(locale));
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

        return XmlParser.getInstance().getI18nData().get(localeToString(locale)).getKeyPair();
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
