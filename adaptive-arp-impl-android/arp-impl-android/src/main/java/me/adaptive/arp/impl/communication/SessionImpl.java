package me.adaptive.arp.impl.communication;/*
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

import me.adaptive.arp.api.Cookie;
import me.adaptive.arp.api.ISession;

public class SessionImpl implements ISession {
    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public void setCookies(Cookie[] cookie) {

    }

    @Override
    public void setCookie(Cookie cookie) {

    }

    @Override
    public void removeCookies(Cookie[] cookie) {

    }

    @Override
    public void removeCookie(Cookie cookie) {

    }

    @Override
    public Object[] getAttributes() {
        return new Object[0];
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public void setAttribute(String name, Object value) {

    }

    @Override
    public String[] listAttributeNames() {
        return new String[0];
    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public void removeAttributes() {

    }
}
