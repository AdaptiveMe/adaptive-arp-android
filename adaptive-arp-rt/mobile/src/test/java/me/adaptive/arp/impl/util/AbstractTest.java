package me.adaptive.arp.impl.util;

import org.junit.After;
import org.junit.Before;
import org.robolectric.Robolectric;

import me.adaptive.arp.MainActivity;
import me.adaptive.arp.api.IAdaptiveRP;

/**
 * Abstract Class that implements and defines the common operation for every test case in the
 * Adaptive ARP Platform
 *
 * @param <T> IAdaptiveRP Interface
 */
public abstract class AbstractTest<T extends IAdaptiveRP> {

    // Main activity of the application
    protected MainActivity activity;

    // Log tag
    protected String LOG_TAG = this.getClass().getSimpleName() + "Test";

    // Specific bridge for every test
    protected T bridge;

    /**
     * Start method for every Test case
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(MainActivity.class);
        this.bridge = getBridge();
    }

    /**
     * End method for every test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {

    }

    /**
     * Abstract method for setting the individual bridge on every child
     *
     * @return Returns the specific bridge
     */
    public abstract T getBridge();


}
