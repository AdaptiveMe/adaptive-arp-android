package me.adaptive.arp.impl.data;/*
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

import me.adaptive.arp.api.*;

public class FileImpl implements IFile {
    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean delete() {
        return false;
    }

    @Override
    public boolean delete(boolean cascade) {
        return false;
    }

    @Override
    public void create(String name, IFileResultCallback callback) {

    }

    @Override
    public void create(String path, String name, IFileResultCallback callback) {

    }

    @Override
    public boolean mkDir() {
        return false;
    }

    @Override
    public boolean mkDir(boolean recursive) {
        return false;
    }

    @Override
    public void listFiles(IFileListResultCallback callback) {

    }

    @Override
    public void listFiles(String regex, IFileListResultCallback callback) {

    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public long getDateCreated() {
        return 0;
    }

    @Override
    public long getDateModified() {
        return 0;
    }

    @Override
    public void getContent(IFileDataResultCallback callback) {

    }

    @Override
    public void setContent(byte[] content, IFileDataResultCallback callback) {

    }

    @Override
    public boolean canWrite() {
        return false;
    }

    @Override
    public boolean canRead() {
        return false;
    }

    @Override
    public void move(IFile newFile, IFileResultCallback callback) {

    }

    @Override
    public void move(IFile newFile, IFileResultCallback callback, boolean overwrite) {

    }

    @Override
    public void move(IFile newFile, boolean createPath, IFileResultCallback callback) {

    }

    @Override
    public void move(IFile newFile, boolean createPath, IFileResultCallback callback, boolean overwrite) {

    }

    @Override
    public IFilePath toPath() {
        return null;
    }

    @Override
    public IFileSystem getFileSystem() {
        return null;
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    @Override
    public IFilePath getRoot() {
        return null;
    }

    @Override
    public IFilePath getFileName() {
        return null;
    }

    @Override
    public IFilePath getParent() {
        return null;
    }

    @Override
    public int getNameCount() {
        return 0;
    }

    @Override
    public IFilePath getName(int index) throws Exception {
        return null;
    }

    @Override
    public boolean startsWith(IFilePath other) {
        return false;
    }

    @Override
    public boolean startsWith(String other) {
        return false;
    }

    @Override
    public boolean endsWith(IFilePath other) {
        return false;
    }

    @Override
    public boolean endsWith(String other) {
        return false;
    }

    @Override
    public boolean equals(String other) {
        return false;
    }

    @Override
    public IFilePath normalize() {
        return null;
    }

    @Override
    public IFilePath resolve(IFilePath other) {
        return null;
    }

    @Override
    public IFilePath resolve(String other) {
        return null;
    }

    @Override
    public IFilePath resolveSibling(IFilePath other) {
        return null;
    }

    @Override
    public IFilePath resolveSibling(String other) {
        return null;
    }

    @Override
    public IFilePath relativize(IFilePath other) {
        return null;
    }

    @Override
    public IFilePath toAbsolutePath() {
        return null;
    }

    @Override
    public IFile toFile() {
        return null;
    }

    @Override
    public boolean equalPath(IFilePath other) {
        return false;
    }
}
