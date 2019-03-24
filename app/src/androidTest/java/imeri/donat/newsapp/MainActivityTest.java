package imeri.donat.newsapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);


    private MainActivity mActivityTest;

    @Before
    public void setUp(){
        mActivityTest=mActivityTestRule.getActivity();

    }


    //Testing lunch of main activity
    @Test
    public void testLunch(){
        onView(withId(R.id.txt_filter)).check(matches((isDisplayed())));
        Log.d("Activity lunched ","successfuly");
    }


    //Testing parsing function
    @Test
    public void testParsing() throws XmlPullParserException, IOException {
        List<RssFeedModel> l;
        InputStream targetStream=getClass().getClassLoader().getResourceAsStream("testdata.txt");
        l=mActivityTest.parseFeed(targetStream);

        assertEquals("RSS Solutions for Restaurants", l.get(0).title);
        assertEquals("This is a description for solutions about restaurants", l.get(0).description);
        assertEquals("http://www.feedforall.com/restaurant.htm", l.get(0).link);

    }

    @After
    public void tearDown() throws Exception {
        mActivityTest=null;
    }
}