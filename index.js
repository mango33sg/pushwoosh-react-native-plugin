'use strict';

import { NativeModules } from 'react-native';

const PushwooshModule = NativeModules.Pushwoosh;

//Class: PushNotification
//Use `PushNotification` to register device for push notifications on Pushwoosh and customize notification appearance.
//
//Example:
//(start code)
//DeviceEventEmitter.addListener('pushOpened', (e: Event) => {
//  console.warn("pushOpened: " + JSON.stringify(e));
//  alert(JSON.stringify(e));
//});
//
//const Pushwoosh = require('pushwoosh-react-native-plugin');
//
//Pushwoosh.init({ "pw_appid" : "XXXX-XXXX", "project_number" : "XXXXXXXXXXXXX" });
//
//Pushwoosh.register(
//  (token) => {
//    console.warn("Registered for pushes: " + token);
//  },
//  (error) => {
//    console.warn("Failed to register: " + error);
//  }
//);
//(end)
class PushNotification {

	//Function: init
	//Call this first thing with your Pushwoosh App ID (pw_appid parameter) and Google Project ID for Android (projectid parameter)
	//
	//Example:
	//(start code)
	//	//initialize Pushwoosh with projectid: "GOOGLE_PROJECT_ID", appid : "PUSHWOOSH_APP_ID". This will trigger all pending push notifications on start.
	//	Pushwoosh.init({ projectid: "XXXXXXXXXXXXXXX", pw_appid : "XXXXX-XXXXX" });
	//(end)
	init(config: Object, success: ?Function, fail: ?Function) {
		if (!success) {
			success = function() {};
		}
		if (!fail) {
			fail = function(error) {};
		}
		PushwooshModule.init(config, success, fail);
	}

	//Function: register
	//Call this to register for push notifications and retreive a push Token
	//
	//Example:
	//(start code)
	//	Pushwoosh.registerDevice(
	//		function(token)
	//		{
	//			alert(token);
	//		},
	//		function(status)
	//		{
	//			alert("failed to register: " +  status);
	//		}
	//	);
	//(end)
	register(success: ?Function, fail: ?Function) {
		if (!success) {
			success = function(token) {};
		}
		if (!fail) {
			fail = function(error) {};
		}
		PushwooshModule.register(success, fail);
	}

	//Function: unregister
	//Unregisters device from push notifications
	unregister(success: ?Function, fail: ?Function) {
		if (!success) {
			success = function(token) {};
		}
		if (!fail) {
			fail = function(error) {};
		}
		PushwooshModule.unregister(success, fail);
	}

	//Function: onPushOpen
	//Deprecated - use DeviceEventEmitter.addListener('pushOpened', callback) instead
	onPushOpen(callback: Function) {
		PushwooshModule.onPushOpen(callback);
	}

	//Function: setTags
	//Call this to set tags for the device
	//
	//Example:
	//sets the following tags: "deviceName" with value "hello" and "deviceId" with value 10
	//(start code)
	//	Pushwoosh.setTags({deviceName:"hello", deviceId:10},
	//		function(status) {
	//			console.warn('setTags success');
	//		},
	//		function(status) {
	//			console.warn('setTags failed');
	//		}
	//	);
	//
	//	//setings list tags "MyTag" with values (array) "hello", "world"
	//	pushNotification.setTags({"MyTag":["hello", "world"]});
	//(end)
	setTags(tags: Object, success: ?Function, fail: ?Function) {
		if (!success) {
			success = function() {};
		}
		if (!fail) {
			fail = function(error) {};
		}
		PushwooshModule.setTags(tags, success, fail);
	}

	//Function: getTags
	//Call this to get tags for the device
	//
	//Example:
	//(start code)
	//	Pushwoosh.getTags(
	//		function(tags)
	//		{
	//			console.warn('tags for the device: ' + JSON.stringify(tags));
	//		},
	//		function(error)
	//		{
	//			console.warn('get tags error: ' + JSON.stringify(error));
	//		}
	//	);
	//(end)
	getTags(success: Function, fail: ?Function) {
		if (!fail) {
			fail = function(error) {};
		}
		PushwooshModule.getTags(success, fail);
	}

    //Function: setShowPushnotificationAlert
    //Set push notifications alert when push notification is received while the app is running, default is `true`
    //
    //Example:
    //(start code)
    //    Pushwoosh.setShowPushnotificationAlert(false);
    //(end)
    setShowPushnotificationAlert(showPushnotificationAlert: boolean) {
        PushwooshModule.setShowPushnotificationAlert(showPushnotificationAlert);
    }
    
    //Function: getShowPushnotificationAlert
    //Show push notifications alert when push notification is received while the app is running, default is `true`
    //
    //Example:
    //(start code)
    //    Pushwoosh.getShowPushnotificationAlert((showPushnotificationAlert) => {
    //                                           console.warn("showPushnotificationAlert = " + showPushnotificationAlert);
    //                                           });
    //(end)
    getShowPushnotificationAlert(callback: Function) {
        PushwooshModule.getShowPushnotificationAlert(callback);
    }
    
	//Function: getPushToken
	//Call this to get push token if it is available. Note the token also comes in registerDevice function callback.
	//
	//Example:
	//(start code)
	//	Pushwoosh.getPushToken(
	//		function(token)
	//		{
	//			console.warn('push token: ' + token);
	//		}
	//	);
	//(end)
	getPushToken(success: Function) {
		PushwooshModule.getPushToken(success);
	}

	//Function: getHwid
	//Call this to get Pushwoosh HWID used for communications with Pushwoosh API
	//
	//Example:
	//(start code)
	//	Pushwoosh.getHwid(
	//		function(token) {
	//			console.warn('Pushwoosh HWID: ' + token);
	//		}
	//	);
	//(end)
	getHwid(success: Function) {
		PushwooshModule.getHwid(success);
	}

	//Function: setUserId
	//[android, ios] Set User indentifier. This could be Facebook ID, username or email, or any other user ID.
	//This allows data and events to be matched across multiple user devices.
	//
	//Parameters:
	// "userId" - user string identifier
	//
	setUserId(userId: string) {
		PushwooshModule.setUserId(userId);
	}

	//Function: postEvent
	//[android, ios] Post events for In-App Messages. This can trigger In-App message display as specified in Pushwoosh Control Panel.
	//
	//Parameters:
	// "event" - event to trigger
	// "attributes" - object with additional event attributes
	// 
	// Example:
	//(start code)
	// Pushwoosh.setUserId("XXXXXX");
	// Pushwoosh.postEvent("buttonPressed", { "buttonNumber" : 4, "buttonLabel" : "banner" });
	//(end)
	postEvent(event: string, attributes: ?Object) {
		if (!attributes) {
			attributes = {};
		}
		PushwooshModule.postEvent(event, attributes);
	}

	//Function: startLocationTracking
	//[android, ios, wp8, windows] Starts geolocation based push notifications. You need to configure Geozones in Pushwoosh Control panel.
	//
	//Parameters:
	// "success" - success callback
	// "fail" - error callback
	//
	startLocationTracking() {
		PushwooshModule.startLocationTracking();
	}

	//Function: stopLocationTracking
	//[android, ios, wp8, windows] Stops geolocation based push notifications
	//
	//Parameters:
	// "success" - success callback
	// "fail" - error callback
	//
	stopLocationTracking() {
		PushwooshModule.stopLocationTracking();
	}

	//Function: setApplicationIconBadgeNumber
	//[android, ios, wp8, windows] Set the application icon badge number
	//
	//Parameters:
	// "badgeNumber" - icon badge number
	//
	setApplicationIconBadgeNumber(badgeNumber: number) {
		PushwooshModule.setApplicationIconBadgeNumber(badgeNumber);
	}

	//Function: getApplicationIconBadgeNumber
	//[android, ios] Returns the application icon badge number
	//
	//Parameters:
	// "callback" - success callback
	//
	//Example:
	//(start code)
	//	Pushwoosh.getApplicationIconBadgeNumber(function(badge){ alert(badge);} );
	//(end)
	getApplicationIconBadgeNumber(callback: Function) {
		PushwooshModule.getApplicationIconBadgeNumber(callback);
	}
	
	//Function: addToApplicationIconBadgeNumber
	//[android, ios] Adds value to the application icon badge
	//
	//Parameters:
	// "badgeNumber" - incremental icon badge number
	//
	//Example:
	//(start code)
	//	Pushwoosh.addToApplicationIconBadgeNumber(5);
	//	Pushwoosh.addToApplicationIconBadgeNumber(-5);
	//(end)
	addToApplicationIconBadgeNumber(badgeNumber: number) {
		PushwooshModule.addToApplicationIconBadgeNumber(badgeNumber);
	}

	//Function: setMultiNotificationMode
	//[android] Allows multiple notifications to be displayed in the Android Notification Center
	setMultiNotificationMode(on: boolean) {
		PushwooshModule.setMultiNotificationMode(on);
	}

	//Function: setLightScreenOnNotification
	//[android] Turns the screen on if notification arrives
	//
	//Parameters:
	// "on" - enable/disable screen unlock (is disabled by default)
	//
	setLightScreenOnNotification(on: boolean) {
		PushwooshModule.setLightScreenOnNotification(on);
	}

	//Function: setEnableLED
	//[android] Enables led blinking when notification arrives and display is off
	//
	//Parameters:
	// "on" - enable/disable led blink (is disabled by default)
	//
	setEnableLED(on: boolean) {
		PushwooshModule.setEnableLED(on);
	}

	//Function: setEnableLED
	//[android] Set led color. Use with <setEnableLED>
	//
	//Parameters:
	// "color" - led color in ARGB integer format
	//
	setColorLED(color: number) {
		PushwooshModule.setColorLED(color);
	}

	//Function: setSoundType
	//[android] Sets default sound to play when push notification arrive.
	//
	//Parameters:
	// "type" - Sound type (0 - default, 1 - no sound, 2 - always)
	//
	setSoundType(type: number) {
		PushwooshModule.setSoundType(type);
	}

	//Function: setVibrateType
	//[android] Sets default vibration mode when push notification arrive.
	//
	//Parameters:
	// "type" - Vibration type (0 - default, 1 - no vibration, 2 - always)
	//
	setVibrateType(type: number) {
		PushwooshModule.setVibrateType(type);
	}

	//Function: pump
	pump(success: Function) {
		PushwooshModule.pump(success);
	}
}

module.exports = new PushNotification();
