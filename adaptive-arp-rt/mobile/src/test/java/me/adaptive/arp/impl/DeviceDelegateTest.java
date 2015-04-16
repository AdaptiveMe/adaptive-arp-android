package me.adaptive.arp.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.DeviceInfo;
import me.adaptive.arp.api.IDevice;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.impl.util.AbstractTest;
import me.adaptive.arp.impl.util.Utils;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21)
public class DeviceDelegateTest extends AbstractTest<IDevice> {

    @Test
    public void testDeviceInfo() throws Exception {

        DeviceInfo deviceInfo = bridge.getDeviceInfo();
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, deviceInfo.getModel());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, deviceInfo.getName());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, deviceInfo.getVendor());


        Assert.assertNotNull(deviceInfo);
    }

    @Override
    public IDevice getBridge() {
        return AppRegistryBridge.getInstance().getDeviceBridge();
    }
}
