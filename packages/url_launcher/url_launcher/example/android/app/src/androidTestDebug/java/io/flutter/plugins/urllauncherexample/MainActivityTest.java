package io.flutter.plugins.urllauncherexample;

import android.support.test.rule.ActivityTestRule;

import dev.flutter.plugins.e2e.FlutterRunner;
import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(FlutterRunner.class)
public class MainActivityTest {
  @Rule public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
}
