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

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.BaseDataDelegate;
import me.adaptive.arp.api.FileDescriptor;
import me.adaptive.arp.api.IFile;
import me.adaptive.arp.api.IFileDataLoadResultCallback;
import me.adaptive.arp.api.IFileDataLoadResultCallbackError;
import me.adaptive.arp.api.IFileDataStoreResultCallback;
import me.adaptive.arp.api.IFileDataStoreResultCallbackError;
import me.adaptive.arp.api.IFileListResultCallback;
import me.adaptive.arp.api.IFileListResultCallbackError;
import me.adaptive.arp.api.IFileResultCallback;
import me.adaptive.arp.api.IFileResultCallbackError;
import me.adaptive.arp.api.IFileSystemSecurity;
import me.adaptive.arp.api.IFileSystemStorageType;
import me.adaptive.arp.api.IFileSystemType;
import me.adaptive.arp.api.ILogging;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.common.Utils;

/**
 * Interface for Managing the File operations
 * Auto-generated implementation of IFile specification.
 */
public class FileDelegate extends BaseDataDelegate implements IFile {

    // logger
    private static final String LOG_TAG = "FileDelegate";
    private ILogging logger;

    /**
     * Default Constructor.
     */
    public FileDelegate() {
        super();
        logger = AppRegistryBridge.getInstance().getLoggingBridge();
    }

    /**
     * Determine whether the current file/folder can be read from.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return True if the folder/file is readable, false otherwise.
     * @since ARP1.0
     */
    public boolean canRead(FileDescriptor descriptor) {
        return new File(descriptor.getPathAbsolute()).canRead();
    }

    /**
     * Determine whether the current file/folder can be written to.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return True if the folder/file is writable, false otherwise.
     * @since ARP1.0
     */
    public boolean canWrite(FileDescriptor descriptor) {
        return new File(descriptor.getPathAbsolute()).canWrite();
    }

    /**
     * Creates a file with the specified name.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param callback   Result of the operation.
     * @since ARP1.0
     */
    public void create(FileDescriptor descriptor, IFileResultCallback callback) {
        try {
            new File(descriptor.getPathAbsolute()).createNewFile();
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "error: "+e.getLocalizedMessage());
            callback.onError(IFileResultCallbackError.Unauthorized);
        }
        callback.onResult(descriptor);
    }

    /**
     * Deletes the given file or path. If the file is a directory and contains files and or subdirectories, these will be
     * deleted if the cascade parameter is set to true.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param cascade    Whether to delete sub-files and sub-folders.
     * @return True if files (and sub-files and folders) whether deleted.
     * @since ARP1.0
     */
    public boolean delete(FileDescriptor descriptor, boolean cascade) {
        boolean response = true;
        try {
            File file = new File(descriptor.getPathAbsolute());

            if (cascade && file.isDirectory()) {
                    String[] children = file.list();
                for (String aChildren : children) {
                    new File(file, aChildren).delete();
                }

            }else file.delete();
        }catch (Exception e) {
            logger.log(ILoggingLogLevel.Error, LOG_TAG, "delete Error: " + e.getLocalizedMessage());
            response = false;
        }
        return response;

    }

    /**
     * Check whether the file/path exists.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return True if the file exists in the filesystem, false otherwise.
     * @since ARP1.0
     */
    public boolean exists(FileDescriptor descriptor) {
        return new File(descriptor.getPathAbsolute()).exists();
    }

    /**
     * Loads the content of the file.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param callback   Result of the operation.
     * @since ARP1.0
     */
    public void getContent(FileDescriptor descriptor, IFileDataLoadResultCallback callback) {
        File file = new File(descriptor.getPathAbsolute());
        if(!file.exists())
            callback.onError(IFileDataLoadResultCallbackError.InexistentFile);
        else{
            try {
                callback.onResult(Utils.readFile(file));
            } catch (IOException e) {
                callback.onError(IFileDataLoadResultCallbackError.Unauthorized);
            }
        }
    }

    /**
     * Returns the file storage type of the file
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return Storage Type file
     * @since ARP1.0
     */
    public IFileSystemStorageType getFileStorageType(FileDescriptor descriptor) {

        IFileSystemStorageType response = IFileSystemStorageType.Unknown;
        String path = descriptor.getPathAbsolute();
        logger.log(ILoggingLogLevel.Debug, LOG_TAG, "getFileStorageType -> Path: "+path);

        // TODO: Review this code
        if(path.startsWith(Environment.getExternalStorageDirectory().getAbsolutePath())){
                response = IFileSystemStorageType.External;
        }else if(path.startsWith(Environment.getDataDirectory().getAbsolutePath())){
            response = IFileSystemStorageType.Application;
        }

        return response;
    }

    /**
     * Returns the file type
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return Returns the file type of the file
     * @since ARP1.0
     */
    public IFileSystemType getFileType(FileDescriptor descriptor) {

        IFileSystemType response;
        File file = new File(descriptor.getPathAbsolute());
        if(!file.exists()) response = IFileSystemType.Unknown;
        else if(file.isDirectory())
            response = IFileSystemType.Directory;
        else response = IFileSystemType.File;
        return response;
    }

    /**
     * Returns the security type of the file
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return Security Level of the file
     * @since ARP1.0
     */
    public IFileSystemSecurity getSecurityType(FileDescriptor descriptor) {

        // TODO: Review this code
        if(descriptor.getPathAbsolute().startsWith(AppRegistryBridge.getInstance().getFileSystemBridge().getApplicationProtectedFolder().getPathAbsolute())){
            return  IFileSystemSecurity.Protected;
        }
        return IFileSystemSecurity.Default;
    }

    /**
     * Check whether this is a path of a file.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @return true if this is a path to a folder/directory, false if this is a path to a file.
     * @since ARP1.0
     */
    public boolean isDirectory(FileDescriptor descriptor) {
        return new File(descriptor.getPathAbsolute()).isDirectory();
    }

    /**
     * List all the files contained within this file/path reference. If the reference is a file, it will not yield
     * any results.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param callback   Result of operation.
     * @since ARP1.0
     */
    public void listFiles(FileDescriptor descriptor, IFileListResultCallback callback) {
        if(isDirectory(descriptor)){
            File file = new File(descriptor.getPathAbsolute());
            List<FileDescriptor> descriptors = walk(file);
            callback.onResult(descriptors.toArray(new FileDescriptor[descriptors.size()]));

        }else callback.onError(IFileListResultCallbackError.InexistentFile);
    }

    /**
     * Recursive list directory
     * @param root Root entry point
     * @return FileDescriptor
     */
    private List<FileDescriptor> walk(File root) {
        List<FileDescriptor> descriptors = new ArrayList<>();
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                logger.log(ILoggingLogLevel.Debug,LOG_TAG, "Dir: " + f.getAbsoluteFile());
            }
            else {
                logger.log(ILoggingLogLevel.Debug,LOG_TAG,  "File: " + f.getAbsoluteFile());
            }
            descriptors.add(Utils.toArp(f));
            if(f.listFiles().length > 0){
                descriptors.addAll(walk(f));
            }

        }
        return descriptors;
    }

    /**
     * Recursive list directory
     * @param root
     * @return FileDescriptor
     */
    private List<FileDescriptor> walk(File root, String regexp) {
        List<FileDescriptor> descriptors = new ArrayList<>();
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                logger.log(ILoggingLogLevel.Debug,LOG_TAG, "Dir: " + f.getAbsoluteFile());
            }
            else {
                logger.log(ILoggingLogLevel.Debug,LOG_TAG,  "File: " + f.getAbsoluteFile());
            }
            if(f.getName().matches(regexp))
                descriptors.add(Utils.toArp(f));
            if(f.listFiles().length > 0){
                descriptors.addAll(walk(f));
            }

        }
        return descriptors;
    }

    /**
     * List all the files matching the speficied regex filter within this file/path reference. If the reference
     * is a file, it will not yield any results.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param regex      Filter (eg. *.jpg, *.png, Fil*) name string.
     * @param callback   Result of operation.
     * @since ARP1.0
     */
    public void listFilesForRegex(FileDescriptor descriptor, String regex, IFileListResultCallback callback) {

        if(isDirectory(descriptor)){
            File file = new File(descriptor.getPathAbsolute());
            List<FileDescriptor> descriptors = walk(file, regex);
            callback.onResult(descriptors.toArray(new FileDescriptor[descriptors.size()]));

        }else callback.onError(IFileListResultCallbackError.InexistentFile);
    }

    /**
     * Creates the parent path (or paths, if recursive) to the given file/path if it doesn't already exist.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param recursive  Whether to create all parent path elements.
     * @return True if the path was created, false otherwise (or it exists already).
     * @since ARP1.0
     */
    public boolean mkDir(FileDescriptor descriptor, boolean recursive) {
        boolean response = true;
        try {
            File file = new File(descriptor.getPathAbsolute());
            if (file.isFile()) {
                file = file.getParentFile();
            }
            if (recursive)
                file.mkdirs();
            else file.mkdir();
        }catch(Exception e){
            logger.log(ILoggingLogLevel.Error,LOG_TAG,"mkDir Error: "+e.getLocalizedMessage());
            response = false;
        }
        return response;
    }

    /**
     * Moves the current file to the given file destination, optionally overwriting and creating the path to the
     * new destination file.
     *
     * @param source      File descriptor of file or folder used for operation as source.
     * @param destination File descriptor of file or folder used for operation as destination.
     * @param createPath  True to create the path if it does not already exist.
     * @param callback    Result of the operation.
     * @param overwrite   True to create the path if it does not already exist.
     * @since ARP1.0
     */
    public void move(FileDescriptor source, FileDescriptor destination, boolean createPath, boolean overwrite, IFileResultCallback callback) {
        try {
            File from = new File(source.getPathAbsolute());
            if(!from.exists()){
                callback.onError(IFileResultCallbackError.SourceInexistent);
                return;
            }
            File to = new File(destination.getPathAbsolute());
            if (overwrite) to.delete();
            else if(to.exists()){
                callback.onError(IFileResultCallbackError.DestionationExists);
                return;
            }
            if (createPath) to.mkdirs();
            from.renameTo(to);
            from.delete();
            callback.onResult(destination);
        }catch (Exception e){
            logger.log(ILoggingLogLevel.Error,LOG_TAG,"mode Error: "+e.getLocalizedMessage());
            callback.onError(IFileResultCallbackError.Unauthorized);
        }

    }

    /**
     * Sets the content of the file.
     *
     * @param descriptor File descriptor of file or folder used for operation.
     * @param content    Binary content to store in the file.
     * @param callback   Result of the operation.
     * @since ARP1.0
     */
    public void setContent(FileDescriptor descriptor, byte[] content, IFileDataStoreResultCallback callback) {

        FileOutputStream fos = null;
        try {
            File f = new File(descriptor.getPathAbsolute());
            fos = new FileOutputStream(f);
            fos.write(content);
            fos.flush();
            fos.close();
            callback.onResult(descriptor);
        } catch (FileNotFoundException e) {
            logger.log(ILoggingLogLevel.Error,LOG_TAG,e.getLocalizedMessage());
            callback.onError(IFileDataStoreResultCallbackError.InexistentFile);
        } catch (IOException e) {
            logger.log(ILoggingLogLevel.Error,LOG_TAG,e.getLocalizedMessage());
            callback.onError(IFileDataStoreResultCallbackError.Unauthorized);
        }catch (Exception e ) {
            logger.log(ILoggingLogLevel.Error,LOG_TAG,e.getLocalizedMessage());
            callback.onError(IFileDataStoreResultCallbackError.Unknown);
        }finally {
            try {
                fos.close();
            } catch (IOException e) {
                logger.log(ILoggingLogLevel.Error, LOG_TAG, e.getLocalizedMessage());
            }
        }
    }

}
/**
 ------------------------------------| Engineered with ♥ in Barcelona, Catalonia |--------------------------------------
 */
