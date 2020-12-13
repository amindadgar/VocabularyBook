package com.amindadgar.mydictionary

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.amindadgar.mydictionary.activities.MainActivity
import org.hamcrest.Matchers

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.amindadgar.mydictionary", appContext.packageName)
    }


    @Test
    fun addWord(){
        val searchWord:String = " opacity "
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.addFabButton)).perform(click())
        onView(withTagValue(Matchers.`is`("DialogEditText"))).perform(typeText(searchWord))
        onView(withId(R.id.confirm_button)).perform(click())

        scenario.onActivity { activity ->
            val data = activity.wordsData
            val newData = data[data.size-1]
            assert(newData.words == searchWord) {"Everything is fine"}
        }
    }
}