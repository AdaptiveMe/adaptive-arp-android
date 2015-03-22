package me.adaptive.arp;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        setName("MainActivity");
    }

    public void testName() throws Exception {
        assertTrue("test",true);

    }
}