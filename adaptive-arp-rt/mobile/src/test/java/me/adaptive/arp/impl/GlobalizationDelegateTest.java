package me.adaptive.arp.impl;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import me.adaptive.arp.api.AppRegistryBridge;
import me.adaptive.arp.api.IGlobalization;
import me.adaptive.arp.api.ILoggingLogLevel;
import me.adaptive.arp.api.Locale;
import me.adaptive.arp.impl.util.AbstractTest;
import me.adaptive.arp.impl.util.Utils;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 21)
public class GlobalizationDelegateTest extends AbstractTest<IGlobalization> {

    @Test
    public void testDeviceInfo() throws Exception {

        Locale locale = bridge.getDefaultLocale();
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, locale.getCountry());
        Utils.log(ILoggingLogLevel.Debug, LOG_TAG, locale.getLanguage());

        Assert.assertNotNull(bridge.getResourceLiteral("hello-world", locale));
    }

    @Override
    public IGlobalization getBridge() {
        return AppRegistryBridge.getInstance().getGlobalizationBridge();
    }
}
