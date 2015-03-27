/*
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

package me.adaptive.arp.common.parser.plist;


import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class PListParser {

    private final static String PLIST_TAG = "plist";
    private final static String DICT_TAG = "dict";
    private final static String KEY_TAG = "key";
    private final static String VALUE_TAG = "string";

    public static String APIService = "plistParser";

    private static PListParser instance = null;
    protected PListParser() {
        // Exists only to defeat instantiation.
    }
    public static PListParser getInstance() {
        if(instance == null) {
            instance = new PListParser();
        }
        return instance;
    }



    public static PList parse(InputStream is) {
        PList plist = new PList();

        try {

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, null);

            int event = parser.next();
            while (event != XmlPullParser.END_DOCUMENT) {

                String name;
                switch (event) {
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (PLIST_TAG.equals(name)) {
                            // root node, do nothing
                        } else if (DICT_TAG.equals(name)) {
                            // dictionary node
                            plist.setValues(parseDictionary(parser));
                        } else {
                            throw new XmlPullParserException(
                                    "Unexpected element found [" + event + ","
                                            + name + "]");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        name = parser.getName();
                        if (PLIST_TAG.equals(name)) {
                            // root node, do nothing
                        } else {
                            throw new XmlPullParserException(
                                    "Unexpected element found [" + event + ","
                                            + name + "]");

                        }
                        break;
                }

                event = parser.next();
            }
        } catch (Exception ex) {
            plist = null;
            Log.d(APIService, "Parse Error: " + ex.getLocalizedMessage());
        }


        if (plist != null) {
            Log.d(APIService, "Parse Result is: " + plist.toString());
        } else {
            Log.d(APIService, "Parse Result is null");
        }


        return plist;
    }

    private static Map<String, String> parseDictionary(XmlPullParser parser)
            throws IOException, XmlPullParserException {
        Map<String, String> map = new HashMap<String, String>();

        String key = null;
        String value = null;
        int event = parser.next();
        while ((event != XmlPullParser.END_TAG)
                || !DICT_TAG.equals(parser.getName())) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    String name = parser.getName();
                    if (KEY_TAG.equals(name)) {
                        key = getText(parser, KEY_TAG);
                    } else if (VALUE_TAG.equals(name)) {
                        value = getText(parser, VALUE_TAG);
                        map.put(key, value);
                    }
                    break;
            }
            event = parser.next();
        }

        return map;
    }

    private static String getText(XmlPullParser parser, String tag)
            throws IOException, XmlPullParserException {

        int event = parser.getEventType();
        if ((event != XmlPullParser.START_TAG) || !tag.equals(parser.getName())) {
            throw new XmlPullParserException("Unexpected element found ["
                    + event + "," + parser.getName() + "]");
        }

        StringBuilder sb = new StringBuilder();
        event = parser.next();
        while (event == XmlPullParser.TEXT) {
            sb.append(parser.getText());
            event = parser.next();
        }

        String text = sb.toString();

        if ((event != XmlPullParser.END_TAG) || !tag.equals(parser.getName())) {
            throw new XmlPullParserException("Unexpected element found ["
                    + event + "," + parser.getName() + "]");
        }

        return text;
    }



}