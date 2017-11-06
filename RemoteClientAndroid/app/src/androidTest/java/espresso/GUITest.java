package espresso;

import android.support.test.espresso.IdlingPolicies;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import remote.aut.bme.hu.remote.R;
import ui.MainActivity;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class GUITest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test() throws InterruptedException {
        connectToServer();

        openAccessTab();

        loginAccount();
        logoutAccount();

        generateAnonymousAccess();
        connectAnonymous();
        disconnectAnonymous();

        openConnectionTab();
        disconnectFromServer();
    }

    private void disconnectFromServer() throws InterruptedException {
        onView(withId(R.id.buttonConnect)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buttonConnect)).check(matches(withText("Connect")));
    }

    private void openConnectionTab() {
        onView(withText("Connection")).perform(click());
    }

    private void disconnectAnonymous() {
        onView(withId(R.id.buttonAnonymousConnect)).perform(click());
    }

    private void connectAnonymous() throws InterruptedException {
        onView(withId(R.id.buttonAnonymousConnect)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buttonAnonymousConnect)).check(matches(withText("Disconnect")));
    }

    private void generateAnonymousAccess() {
        onView(withId(R.id.buttonGenerateId)).perform(click());
        onView(withId(R.id.buttonGeneratePassword)).perform(click());
    }

    private void logoutAccount() {
        onView(withId(R.id.buttonAccountConnect)).perform(click());
    }

    private void loginAccount() throws InterruptedException {
        onView(withId(R.id.inputAccountUsername)).perform(typeText("user"));
        onView(withId(R.id.inputAccountPassword)).perform(typeText("user"));
        onView(withId(R.id.buttonAccountConnect)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buttonAccountConnect)).check(matches(withText("Logout")));
    }

    private void openAccessTab() {
        onView(withText("Access")).perform(click());
    }

    private void connectToServer() throws InterruptedException {
        onView(withId(R.id.inputAddress)).perform(typeText("192.168.0.8")).perform(closeSoftKeyboard());
        onView(withId(R.id.buttonConnect)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.buttonConnect)).check(matches(withText("Disconnect")));
    }

}
