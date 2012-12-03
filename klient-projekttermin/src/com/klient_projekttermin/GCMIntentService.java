package com.klient_projekttermin;

/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import static com.klient_projekttermin.CommonUtilities.SENDER_ID;
import static com.klient_projekttermin.CommonUtilities.displayMessage;
import login.LogInActivity;
import login.User;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		displayMessage(context, getString(R.string.gcm_registered));
		ServerUtilities.register(context, registrationId);
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		System.out.println("message");
		if (intent.getExtras().containsKey("logout")&&intent.getExtras().get("logout").equals("true")) {
			System.out.println("logga ut");
			User user=User.getInstance();
			user.setLoggedIn(false);
			Intent i = new Intent(this, LogInActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}else{
			Log.i(TAG, "Received message");
			String message = getString(R.string.gcm_message);
			displayMessage(context, message);
		}
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		displayMessage(context, message);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		displayMessage(context,
				getString(R.string.gcm_recoverable_error, errorId));
		return super.onRecoverableError(context, errorId);
	}
}
