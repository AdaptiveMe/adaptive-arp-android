package me.adaptive.arp.impl.Data;/*
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

import me.adaptive.arp.api.IFile;
import me.adaptive.arp.api.IFilePath;
import me.adaptive.arp.api.IFileSystem;

public class FilePathImpl implements IFilePath {
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
