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
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseSecurityDelegate;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.ISecurity;
import me.adaptive.arp.api.ISecurityResultCallback;
import me.adaptive.arp.api.ISecurityResultCallbackError;
import me.adaptive.arp.api.ISecurityResultCallbackWarning;
import me.adaptive.arp.api.SecureKeyPair;

/**
 * Interface for Managing the Security operations
 * Auto-generated implementation of ISecurity specification.
 */
public class SecurityDelegate extends BaseSecurityDelegate implements ISecurity {

    // logger
    private static final String LOG_TAG = "SecurityDelegate";
    private static String[] foldersToCheckWriteAccess = {
            "/data",
            "/",
            "/system",
            "/system/bin",
            "/system/sbin",
            "/system/xbin",
            "/vendor/bin",
            "/sys",
            "/sbin",
            "/etc",
            "/proc",
            "/dev"
    };
    private static String[] foldersToCheckReadAccess = {
            "/data"
    };
    private static String[] forbiddenInstalledPackages = {
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine"
    };
    private static String[] SULocation = {
            "/sbin/",
            "/system/bin/",
            "/system/xbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
    };
    private ILogging logger;
    // Context
    private Context context;

    /**
     * Default Constructor.
     */
    public SecurityDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    /**
     * Deletes from the device internal storage the entry/entries containing the specified key names.
     *
     * @param keys             Array with the key names to delete.
     * @param publicAccessName The name of the shared internal storage object (if needed).
     * @param callback         callback to be executed upon function result.
     * @since ARP 1.0
     */
    public void deleteSecureKeyValuePairs(final String[] keys, final String publicAccessName, final ISecurityResultCallback callback) {

        List<SecureKeyPair> successfulKeyPairs = new ArrayList<>();
        List<SecureKeyPair> failedKeyPairs = new ArrayList<>();
        SecureKeyPair sc;
        SharedPreferences settings = GetOtherAppSharedPreferences(publicAccessName);
        if (settings != null) {
            for (String keyName : keys) {
                if (settings.contains(keyName)) {
                    SharedPreferences.Editor ed = settings.edit();
                    ed.remove(keyName);
                    ed.apply();
                    sc = new SecureKeyPair();
                    sc.setSecureKey(keyName);
                    successfulKeyPairs.add(sc);

                } else {
                    sc = new SecureKeyPair();
                    sc.setSecureKey(keyName);
                    failedKeyPairs.add(sc);
                }
            }
        } else {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeStoredKeyValuePairs: Storage Unit is null.");
            callback.onError(ISecurityResultCallbackError.NoMatchesFound);
        }

        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "removeStoredKeyValuePairs: Keys removed from storage unit: " + successfulKeyPairs.size() + "; Keys Not removed from storage unit: " + failedKeyPairs.size());
        if (failedKeyPairs.size() != 0) {
            callback.onWarning(successfulKeyPairs.toArray(new SecureKeyPair[successfulKeyPairs.size()]), ISecurityResultCallbackWarning.Unknown);
        } else {
            callback.onResult(successfulKeyPairs.toArray(new SecureKeyPair[successfulKeyPairs.size()]));
        }

    }

    /**
     * Retrieves from the device internal storage the entry/entries containing the specified key names.
     *
     * @param keys             Array with the key names to retrieve.
     * @param publicAccessName The name of the shared internal storage object (if needed).
     * @param callback         callback to be executed upon function result.
     * @since ARP 1.0
     */
    public void getSecureKeyValuePairs(final String[] keys, final String publicAccessName, final ISecurityResultCallback callback) {

        List<SecureKeyPair> foundKeyPairs = new ArrayList<>();
        try {

            SharedPreferences settings = GetOtherAppSharedPreferences(publicAccessName);
            if (settings != null) {
                for (String keyname : keys) {
                    if (settings != null && settings.contains(keyname)) {
                        SecureKeyPair keyPair = new SecureKeyPair();
                        keyPair.setSecureKey(keyname);
                        keyPair.setSecureData(settings.getString(keyname, null));
                        foundKeyPairs.add(keyPair);
                    }
                }
            } else {
                logger.log(ILoggingLogLevel.Debug, LOG_TAG, "getSecureKeyValuePairs: Storage Unit is null.");
                callback.onError(ISecurityResultCallbackError.NoMatchesFound);
            }
        } catch (Exception ex) {
            callback.onError(ISecurityResultCallbackError.NoMatchesFound);
        }
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "getSecureKeyValuePairs: Keys found in storage unit: " + foundKeyPairs.size());

        callback.onResult(foundKeyPairs.toArray(new SecureKeyPair[foundKeyPairs.size()]));

    }

    /**
     * Stores in the device internal storage the specified item/s.
     *
     * @param keyValues        Array containing the items to store on the device internal memory.
     * @param publicAccessName The name of the shared internal storage object (if needed).
     * @param callback         callback to be executed upon function result.
     * @since ARP 1.0
     */
    public void setSecureKeyValuePairs(final SecureKeyPair[] keyValues, final String publicAccessName, final ISecurityResultCallback callback) {

        List<SecureKeyPair> successfulKeyPairs = new ArrayList<SecureKeyPair>();
        List<SecureKeyPair> failedKeyPairs = new ArrayList<SecureKeyPair>();
        SecureKeyPair sc;
        SharedPreferences settings = GetOtherAppSharedPreferences(publicAccessName);
        if (settings != null) {
            for(SecureKeyPair k : keyValues){
                try {
                    String keyName = k.getSecureKey();
                    String keyValue = k.getSecureData();
                    SharedPreferences.Editor ed = settings.edit();
                    ed.putString(keyName, keyValue);
                    ed.apply();
                    sc = new SecureKeyPair();
                    sc.setSecureKey(keyName);
                    successfulKeyPairs.add(sc);

                } catch (Exception ex) {
                    sc = new SecureKeyPair();
                    sc.setSecureKey(k.getSecureKey());
                    failedKeyPairs.add(sc);
                }
            }
        } else {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG + " setSecureKeyValuePairs", "Storage Unit is null.");
            callback.onError(ISecurityResultCallbackError.NoMatchesFound);
        }
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "setSecureKeyValuePairs: Key stored in storage unit: " + successfulKeyPairs.size() + "; Keys Not stored in storage unit: " + failedKeyPairs.size());

        if (failedKeyPairs.size() != 0) {
            callback.onWarning(failedKeyPairs.toArray(new SecureKeyPair[failedKeyPairs.size()]), ISecurityResultCallbackWarning.Unknown);
        } else {
            callback.onResult(successfulKeyPairs.toArray(new SecureKeyPair[successfulKeyPairs.size()]));
        }
    }

    /**
     * Returns if the device has been modified in anyhow
     *
     * @return true if the device has been modified; false otherwise
     * @since ARP1.0
     */
    public boolean isDeviceModified() {
        if (checkRootMethod1()) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "isDeviceModified: Detected by checkRootMethod1");
            return true;
        }

        if (checkRootMethod2()) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "isDeviceModified: Detected by checkRootMethod2");
            return true;
        }

        if (checkRootMethod3_0()) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "isDeviceModified: Detected by checkRootMethod3_0");
            return true;
        }

        if (checkRootMethod3_1()) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "isDeviceModified: Detected by checkRootMethod3_1");
            return true;
        }

        if (checkRootMethod3_2()) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "isDeviceModified: Detected by checkRootMethod3_2");
            return true;
        }

        if (checkRootMethod3_2()) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "isDeviceModified: Detected by checkRootMethod3_2");
            return true;
        }

        if (findBinary("su")) {
            return true;
        }

        return false;
    }

    /**
     * Whether Rooted keys are present
     *
     * @return true if found; false otherwise
     */
    private boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;

        return buildTags != null && buildTags.contains("test-keys");
    }

    /**
     * Whether SU apk is present
     *
     * @return true if found; false otherwise
     */
    private boolean checkRootMethod2() {
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e) {
            // Nothing to do
        }
        return false;
    }

    /**
     * CHECKS WRITE ACCESS IN FORBIDDEN FOLDERS
     *
     * @return true if the write was successful; false otherwise
     */
    private boolean checkRootMethod3_0() {
        try {
            for (String folder : foldersToCheckWriteAccess) {
                File file = new File(folder);
                if (file.canWrite()) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Nothing to do
        }
        return false;
    }

    /**
     * CHECKS READ ACCESS IN FORBIDDEN FOLDERS
     *
     * @return true if the read was successful; false otherwise
     */
    private boolean checkRootMethod3_1() {
        try {
            for (String folder : foldersToCheckReadAccess) {
                File file = new File(folder);
                if (file.canRead()) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Nothing to do
        }
        return false;
    }

    /**
     * CHECK IF TYPICAL ROOTED PACKAGES ARE INSTALLED
     *
     * @return true if the packages where found; false otherwise
     */
    private boolean checkRootMethod3_2() {
        try {
            List<String> apps = Arrays.asList(forbiddenInstalledPackages);

            List<ApplicationInfo> packages;
            PackageManager pm;
            pm = context.getPackageManager();
            packages = pm.getInstalledApplications(0);
            for (ApplicationInfo packageInfo : packages) {
                if (apps.contains(packageInfo.packageName)) return true;
            }
        } catch (Exception e) {
            // Nothing to do
        }
        return false;
    }

    /**
     * Searches in possible su binary locations
     *
     * @param binaryName filename
     * @return true if found; false otherwise
     */
    private boolean findBinary(String binaryName) {

        boolean found = false;
        for (String where : SULocation) {
            if (new File(where + binaryName).exists()) {
                found = true;

                break;
            }
        }
        return found;
    }

    /**
     * Get the SharedPreferences Object
     *
     * @param publicAccessName name
     * @return SharedPreferences desired object
     */
    private SharedPreferences GetOtherAppSharedPreferences(String publicAccessName) {
        SharedPreferences settings = null;
        try {
            if (publicAccessName != null) {
                settings = context.getSharedPreferences(publicAccessName, Context.MODE_MULTI_PROCESS + Context.MODE_PRIVATE);
            } else {
                String PREFERENCES_FILE_NAME = "AdaptiveSettings";
                settings = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_MULTI_PROCESS + Context.MODE_PRIVATE);
            }


        } catch (Exception e) {
            logger.log(ILoggingLogLevel.Debug, LOG_TAG, "GetOtherAppSharedPreferences: Opening Storage Unit: The storage unit could not be accessed. Unhanlded error. e: " + e.toString());
        }
        return settings;
    }


}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
