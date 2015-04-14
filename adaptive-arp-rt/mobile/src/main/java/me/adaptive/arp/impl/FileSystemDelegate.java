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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.util.Date;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseDataDelegate;
import me.adaptive.arp.api.FileDescriptor;
import me.adaptive.arp.api.IFileSystem;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.common.Utils;

/**
 * Interface for Managing the File System operations
 * Auto-generated implementation of IFileSystem specification.
 */
public class FileSystemDelegate extends BaseDataDelegate implements IFileSystem {

    // logger
    private static final String LOG_TAG = "FileSystemDelegate";
    private ILogging logger;

    // Context
    private Context context;

    /**
     * Default Constructor.
     */
    public FileSystemDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
        context = (Context) AppRegistryBridge.getInstance().getPlatformContext().getContext();
    }

    /**
     * Creates a new reference to a new or existing location in the filesystem.
     * This method does not create the actual file in the specified folder.
     *
     * @param parent Parent directory.
     * @param name   Name of new file or directory.
     * @return A reference to a new or existing location in the filesystem.
     * @since ARP1.0
     */
    public FileDescriptor createFileDescriptor(FileDescriptor parent, String name) {

        FileDescriptor response = new FileDescriptor();
        response.setName(name);
        response.setPath(parent.getPath()+"/"+name);
        response.setPathAbsolute(parent.getPathAbsolute() + getSeparator() + name);
        response.setDateCreated(new Date().getTime());
        response.setDateModified(response.getDateCreated());
        logger.log(ILoggingLogLevel.Debug,LOG_TAG,"createFileDescriptor: "+response.getPathAbsolute());
        return response;
    }

    /**
     * Returns a reference to the cache folder for the current application.
     * This path must always be writable by the current application.
     * This path is volatile and may be cleaned by the OS periodically.
     *
     * @return Path to the application's cache folder.
     * @since ARP1.0
     */
    public FileDescriptor getApplicationCacheFolder() {

        FileDescriptor response;
        if(Utils.isExternalStorageReadable())
            response = Utils.toArp(context.getExternalCacheDir());
        else
            response = Utils.toArp(context.getCacheDir());
        logger.log(ILoggingLogLevel.Debug,LOG_TAG,"getApplicationCacheFolder: "+response.getPathAbsolute());
        return response;
    }

    /**
     * Returns a reference to the cloud synchronizable folder for the current application.
     * This path must always be writable by the current application.
     *
     * @return Path to the application's cloud storage folder.
     * @since ARP1.0
     */
    public FileDescriptor getApplicationCloudFolder() {
        FileDescriptor response;
        // TODO: Not implemented.
        throw new UnsupportedOperationException(this.getClass().getName() + ":getApplicationCloudFolder");
        // return response;
    }

    /**
     * Returns a reference to the documents folder for the current application.
     * This path must always be writable by the current application.
     *
     * @return Path to the application's documents folder.
     * @since ARP1.0
     */
    public FileDescriptor getApplicationDocumentsFolder() {

        FileDescriptor response;
        if(Utils.isExternalStorageReadable())
            response = Utils.toArp(context.getExternalFilesDir(null));
        else
            response = Utils.toArp(context.getFilesDir());
        logger.log(ILoggingLogLevel.Debug,LOG_TAG,"getApplicationDocumentsFolder: "+response.getPathAbsolute());
        return response;
    }

    /**
     * Returns a reference to the application installation folder.
     * This path may or may not be directly readable or writable - it usually contains the app binary and data.
     *
     * @return Path to the application folder.
     * @since ARP1.0
     */
    public FileDescriptor getApplicationFolder() {

        FileDescriptor response = new FileDescriptor();
        response.setPath(context.getApplicationInfo().dataDir);
        response.setPathAbsolute(context.getApplicationContext().getFilesDir().getAbsolutePath());

        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getApplicationContext().getPackageName();
            PackageInfo pi = pm.getPackageInfo(packageName,0);
            response.setDateCreated(pi.firstInstallTime);
            response.setDateModified(pi.lastUpdateTime);

            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            File file = new File(applicationInfo.publicSourceDir);
            response.setSize(file.length());

        } catch (PackageManager.NameNotFoundException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "getApplicationFolder Exception: "+e.getLocalizedMessage());
        }

        logger.log(ILoggingLogLevel.Debug,LOG_TAG,"getApplicationFolder: "+response.getPathAbsolute());
        return response;
    }

    /**
     * Returns a reference to the protected storage folder for the current application.
     * This path must always be writable by the current application.
     *
     * @return Path to the application's protected storage folder.
     * @since ARP1.0
     */
    public FileDescriptor getApplicationProtectedFolder() {
        FileDescriptor response = Utils.toArp(context.getFilesDir());
        logger.log(ILoggingLogLevel.Debug,LOG_TAG,"getApplicationProtectedFolder: "+response.getPathAbsolute());
        return response;
    }

    /**
     * Returns the file system dependent file separator.
     *
     * @return char with the directory/file separator.
     * @since ARP1.0
     */
    public char getSeparator() {
        return '/';
    }

    /**
     * Returns a reference to the external storage folder provided by the OS. This may
     * be an external SSD card or similar. This type of storage is removable and by
     * definition, not secure.
     * This path may or may not be writable by the current application.
     *
     * @return Path to the application's documents folder.
     * @since ARP1.0
     */
    public FileDescriptor getSystemExternalFolder() {
        FileDescriptor response = null;
        if(Utils.isExternalStorageReadable())
            response = Utils.toArp(context.getExternalFilesDir(null));
        else response = new FileDescriptor();
        logger.log(ILoggingLogLevel.Debug,LOG_TAG,"getSystemExternalFolder: "+response.getPathAbsolute());
        return response;
    }


}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
