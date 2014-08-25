package me.adaptive.arp.impl.System;/*
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

import me.adaptive.arp.api.DeviceInfo;
import me.adaptive.arp.api.IButtonListener;
import me.adaptive.arp.api.IDevice;
import me.adaptive.arp.api.Locale;

public class DeviceImpl implements IDevice {
    @Override
    public DeviceInfo getDeviceInfo() {
        return null;
    }

    @Override
    public Locale getLocaleCurrent() {
        return null;
    }

    @Override
    public void addButtonListener(IButtonListener listener) {

    }

    @Override
    public void removeButtonListener(IButtonListener listener) {

    }

    @Override
    public void removeButtonListeners() {

    }
}
