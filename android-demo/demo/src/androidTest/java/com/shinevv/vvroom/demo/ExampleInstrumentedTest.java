package com.shinevv.vvroom.demo;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
<<<<<<< HEAD
    public void useAppContext() {
=======
    public void useAppContext() throws Exception {
>>>>>>> e0470175ed8cec92f667c81fe8784ef5fc466378
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.shinevv.vvroom.demo", appContext.getPackageName());
    }
}
