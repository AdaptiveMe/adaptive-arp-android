package me.adaptive.arp.impl.util;

import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;

import me.adaptive.arp.MainActivity;
import me.adaptive.arp.api.IAdaptiveRP;

public abstract class AbstractTest<T extends IAdaptiveRP> {

    protected MainActivity activity;
    protected String LOG_TAG = this.getClass().getSimpleName() + "Test";
    protected T bridge;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
        this.bridge = getBridge();
    }

    @After
    public void tearDown() throws Exception {

    }

    public abstract T getBridge();


}
