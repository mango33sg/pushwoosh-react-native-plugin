package com.pushwoosh.reactnativeplugin;

import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.pushwoosh.Pushwoosh;
import com.pushwoosh.badge.PushwooshBadge;
import com.pushwoosh.exception.GetTagsException;
import com.pushwoosh.exception.PushwooshException;
import com.pushwoosh.exception.RegisterForPushNotificationsException;
import com.pushwoosh.exception.UnregisterForPushNotificationException;
import com.pushwoosh.function.Result;
import com.pushwoosh.inapp.PushwooshInApp;
import com.pushwoosh.location.PushwooshLocation;
import com.pushwoosh.notification.PushwooshNotificationSettings;
import com.pushwoosh.notification.SoundType;
import com.pushwoosh.notification.VibrateType;
import com.pushwoosh.tags.TagsBundle;

import org.json.JSONObject;

public class PushwooshPlugin extends ReactContextBaseJavaModule implements LifecycleEventListener {

	static final String TAG = "RNPushwoosh";
	private static final String PUSH_OPEN_EVENT = "PwPushOpened";
	private static final String PUSH_OPEN_JS_EVENT = "pushOpened";

	private static final String PUSH_RECEIVED_EVENT = Pushwoosh.PUSH_RECEIVE_EVENT;
	private static final String PUSH_RECEIVED_JS_EVENT = "pushReceived";

	private static EventDispatcher mEventDispatcher = new EventDispatcher();

	private static String sReceivedPushData;
	private static boolean sReceivedPushCallbackRegistered = false;

	private static String sStartPushData;
	private static boolean sPushCallbackRegistered = false;

	private static boolean sInitialized = false;
	private static final Object sStartPushLock = new Object();

	private static PushwooshPlugin INSTANCE = null;

	public PushwooshPlugin(ReactApplicationContext reactContext) {
		super(reactContext);

		INSTANCE = this;

		reactContext.addLifecycleEventListener(this);
	}

	///
	/// Module API
	///

	@Override
	public String getName() {
		return "Pushwoosh";
	}

	@ReactMethod
	public void init(ReadableMap config, Callback success, Callback error) {
		String appId = config.getString("pw_appid");
		String projectId = config.getString("project_number");

		if (appId == null || projectId == null) {
			if (error != null) {
				error.invoke("Pushwoosh Application id and GCM project number not specified");
			}
			return;
		}

		Pushwoosh.getInstance().setAppId(appId);
		Pushwoosh.getInstance().setSenderId(projectId);

		sInitialized = true;

		if (success != null) {
			success.invoke();
		}
	}

	@ReactMethod
	public void register(final Callback success, final Callback error) {
		Pushwoosh.getInstance().registerForPushNotifications(new com.pushwoosh.function.Callback<String, RegisterForPushNotificationsException>() {
			@Override
			public void process(@NonNull Result<String, RegisterForPushNotificationsException> result) {
				if (result.isSuccess()) {
					success.invoke(result.getData());
				} else if (result.getException() != null) {
					error.invoke(result.getException().getLocalizedMessage());
				}
			}
		});
	}

	@ReactMethod
	public void unregister(final Callback success, final Callback error) {
		Pushwoosh.getInstance().unregisterForPushNotifications(new com.pushwoosh.function.Callback<String, UnregisterForPushNotificationException>() {
			@Override
			public void process(@NonNull Result<String, UnregisterForPushNotificationException> result) {
				if (result.isSuccess()) {
					if (success != null) {
						success.invoke(result.getData());
					}
				} else if (result.getException() != null) {
					if (error != null) {
						error.invoke(result.getException().getLocalizedMessage());
					}
				}
			}
		});
	}

	@ReactMethod
	public void onPushOpen(Callback callback) {
		synchronized (sStartPushLock) {
			if (!sPushCallbackRegistered && sStartPushData != null) {
				callback.invoke(ConversionUtil.toWritableMap(ConversionUtil.stringToJSONObject(sStartPushData)));
				sPushCallbackRegistered = true;
				return;
			}

			sPushCallbackRegistered = true;
			mEventDispatcher.subscribe(PUSH_OPEN_EVENT, callback);
		}
	}

	@ReactMethod
	public void onPushReceived(Callback callback) {
		synchronized (sStartPushLock) {
			if (!sReceivedPushCallbackRegistered && sReceivedPushData != null) {
				callback.invoke(ConversionUtil.toWritableMap(ConversionUtil.stringToJSONObject(sReceivedPushData)));
				sReceivedPushCallbackRegistered = true;
				return;
			}

			sReceivedPushCallbackRegistered = true;
			mEventDispatcher.subscribe(PUSH_RECEIVED_EVENT, callback);
		}
	}

	@ReactMethod
	public void setTags(ReadableMap tags, final Callback success, final Callback error) {
		Pushwoosh.getInstance().sendTags(ConversionUtil.convertToTagsBundle(tags), new com.pushwoosh.function.Callback<Void, PushwooshException>() {
			@Override
			public void process(@NonNull Result<Void, PushwooshException> result) {
				if (result.isSuccess()) {
					if (success != null) {
						success.invoke();
					}
				} else {
					if (error != null) {
						error.invoke(result.getException().getMessage());
					}
				}
			}
		});
	}

	@ReactMethod
	public void getTags(final Callback success, final Callback error) {
		Pushwoosh.getInstance().getTags(new com.pushwoosh.function.Callback<TagsBundle, GetTagsException>() {
			@Override
			public void process(@NonNull Result<TagsBundle, GetTagsException> result) {
				if(result.isSuccess()){
					if (success != null && result.getData()!=null) {
						success.invoke(ConversionUtil.toWritableMap(result.getData().toJson()));
					}
				} else {
					if (error != null && result.getException()!=null) {
						error.invoke(result.getException().getMessage());
					}
				}
			}
		});
	}

	@ReactMethod
	public void getPushToken(Callback callback) {
		callback.invoke(Pushwoosh.getInstance().getPushToken());
	}

	@ReactMethod
	public void getHwid(Callback callback) {
		callback.invoke(Pushwoosh.getInstance().getHwid());
	}

	@ReactMethod
	public void setUserId(String userId) {
		PushwooshInApp.getInstance().setUserId(userId);
	}

	@ReactMethod
	public void postEvent(String event, ReadableMap attributes) {
		PushwooshInApp.getInstance().postEvent(event, ConversionUtil.convertToTagsBundle(attributes));
	}

	@ReactMethod
	public void startLocationTracking() {
		PushwooshLocation.startLocationTracking();
	}

	@ReactMethod
	public void stopLocationTracking() {
		PushwooshLocation.stopLocationTracking();
	}

	@ReactMethod
	public void setApplicationIconBadgeNumber(int badgeNumber) {
		PushwooshBadge.setBadgeNumber(badgeNumber);
	}

	@ReactMethod
	public void getApplicationIconBadgeNumber(Callback callback) {
		callback.invoke(PushwooshBadge.getBadgeNumber());
	}

	@ReactMethod
	public void addToApplicationIconBadgeNumber(int badgeNumber) {
		PushwooshBadge.addBadgeNumber(badgeNumber);
	}

	@ReactMethod
	public void setMultiNotificationMode(boolean on) {
		PushwooshNotificationSettings.setMultiNotificationMode(on);
	}

	@ReactMethod
	public void setLightScreenOnNotification(boolean on) {
		PushwooshNotificationSettings.setLightScreenOnNotification(on);
	}

	@ReactMethod
	public void setEnableLED(boolean on) {
		PushwooshNotificationSettings.setEnableLED(on);
	}

	@ReactMethod
	public void setColorLED(int color) {
		PushwooshNotificationSettings.setColorLED( color);
	}

	@ReactMethod
	public void setSoundType(int type) {
		PushwooshNotificationSettings.setSoundNotificationType(SoundType.fromInt(type));
	}

	@ReactMethod
	public void setVibrateType(int type) {
		PushwooshNotificationSettings.setVibrateNotificationType(VibrateType.fromInt(type));
	}

	@ReactMethod
	public void pump(Callback success) {
		WritableMap map = Arguments.createMap();
		boolean hasData = false;

		synchronized (sStartPushLock) {
			if (sStartPushData != null) {
				Log.v(TAG, "Pump from push opened");
				if (success != null) {
					map.putMap("opened", ConversionUtil.toWritableMap(ConversionUtil.stringToJSONObject(sStartPushData)));
					hasData = true;
				} else {
					sendEvent(PUSH_OPEN_JS_EVENT, ConversionUtil.stringToJSONObject(sStartPushData));
				}
				sStartPushData = null;
			}

			if (sReceivedPushData != null) {
				Log.v(TAG, "Pump from push received");
				if (success != null) {
					map.putMap("received", ConversionUtil.toWritableMap(ConversionUtil.stringToJSONObject(sReceivedPushData)));
					hasData = true;
				} else {
					sendEvent(PUSH_RECEIVED_JS_EVENT, ConversionUtil.stringToJSONObject(sReceivedPushData));
				}
				sReceivedPushData = null;
			}

			if (hasData) {
				success.invoke(map);
			}
		}
	}

	///
	/// LifecycleEventListener callbacks
	///

	@Override
	public void onHostResume() {
		Log.v(TAG, "Host resumed");
	}

	@Override
	public void onHostPause() {
		Log.v(TAG, "Host paused");
	}

	@Override
	public void onHostDestroy() {
		Log.v(TAG, "Host destroyed");

		sPushCallbackRegistered = false;
		sStartPushData = null;

		sReceivedPushCallbackRegistered = false;
		sReceivedPushData = null;
	}

	///
	/// Private methods
	///

	static void openPush(String pushData) {
		Log.i(TAG, "Push open: " + pushData);

		try {
			synchronized (sStartPushLock) {
				sStartPushData = pushData;
				if (sPushCallbackRegistered) {
					mEventDispatcher.dispatchEvent(PUSH_OPEN_EVENT, ConversionUtil.toWritableMap(ConversionUtil.stringToJSONObject(pushData)));
				}
				if (sInitialized && INSTANCE != null) {
					INSTANCE.sendEvent(PUSH_OPEN_JS_EVENT, ConversionUtil.stringToJSONObject(pushData));
				} else {
					String message = "Push open lost to ReactNative, reasons";
					message += ": sInitialized = " + String.valueOf(sInitialized);
					message += ": INSTANCE = " + String.valueOf(INSTANCE);
					Log.e(TAG, message);
				}
			}
		} catch (Exception e) {
			// React Native is highly unstable
			Log.e(TAG, e.getMessage());
		}
	}

	private void sendEvent(String event, JSONObject params) {
		getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
			.emit(event, ConversionUtil.toWritableMap(params));
	}

	static void messageReceived(String pushData) {
		Log.i(TAG, "Push received: " + pushData);

		try {
			synchronized (sStartPushLock) {
				sReceivedPushData = pushData;
				if (sReceivedPushCallbackRegistered) {
					mEventDispatcher.dispatchEvent(PUSH_RECEIVED_EVENT, ConversionUtil.toWritableMap(ConversionUtil.stringToJSONObject(pushData)));
				}
				if (sInitialized && INSTANCE != null) {
					INSTANCE.sendEvent(PUSH_RECEIVED_JS_EVENT, ConversionUtil.stringToJSONObject(pushData));
				}
			}
		} catch (Exception e) {
			// React Native is highly unstable
			Log.e(TAG, e.getMessage());
		}
	}
}
