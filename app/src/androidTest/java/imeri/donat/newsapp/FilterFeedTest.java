package imeri.donat.newsapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Random;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class FilterFeedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule=new ActivityTestRule<MainActivity>(MainActivity.class);


    private MainActivity mActivityTest;

    @Before
    public void setUp() {
        mActivityTest=mActivityTestRule.getActivity();

    }

    @Test
    public void testFilter(){
        boolean changed=false;
        int afterFilterSize=0;
        int beforeFilterSize=0;
        int counter=0;
        onView(withId(R.id.miCompose)).perform(click());
        onView(withId(R.id.text_url)).perform(click());
        onView(withId(R.id.text_url)).perform(clearText(),typeText("https://nasa.gov/rss/dyn/breaking_news.rss"));
        closeSoftKeyboard();
        onView(withId(R.id.button_submit)).perform(click());
        while(!changed) {
            counter++;
            onView(withId(R.id.txt_filter)).perform(typeText(randomChar()));
            closeSoftKeyboard();
            try {
                afterFilterSize = mActivityTest.filterNews.newList.size();
                beforeFilterSize=mActivityTest.mFeedModelList.size();
            }
            catch (NullPointerException e){
                Log.d("First time starting app","nothing to filter");
            }
            if (afterFilterSize!=beforeFilterSize) {
                changed = true;
                Log.d("Number of elements removed after filtering ",(beforeFilterSize
                        -afterFilterSize)+"");
            }
            else if (counter>=10){
                changed=true;
                Log.d("Number of elements removed after filtering ","0");
            }

        }

    }

    @After
    public void tearDown() throws Exception {
        mActivityTest=null;
    }

    private String randomChar(){
        Random rnd = new Random();
        char c = (char) (rnd.nextInt(26) + 'a');

        return String.valueOf(c);
    }
}