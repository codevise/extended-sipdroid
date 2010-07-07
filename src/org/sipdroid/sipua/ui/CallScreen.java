package org.sipdroid.sipua.ui;

import org.sipdroid.media.RtpStreamReceiver;
import org.sipdroid.sipua.R;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.ui.InstantAutoCompleteTextView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/*
 * Copyright (C) 2009 The Sipdroid Open Source Project
 * 
 * This file is part of Sipdroid (http://www.sipdroid.org)
 * 
 * Sipdroid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This source code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this source code; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

public class CallScreen extends Activity implements DialogInterface.OnClickListener {
	public static final int FIRST_MENU_ID = Menu.FIRST;
	public static final int HANG_UP_MENU_ITEM = FIRST_MENU_ID + 1;
	public static final int HOLD_MENU_ITEM = FIRST_MENU_ID + 2;
	public static final int MUTE_MENU_ITEM = FIRST_MENU_ID + 3;
	public static final int VIDEO_MENU_ITEM = FIRST_MENU_ID + 5;
	public static final int SPEAKER_MENU_ITEM = FIRST_MENU_ID + 6;
	public static final int TRANSFER_MENU_ITEM = FIRST_MENU_ID + 7;
	public static final int ANSWER_MENU_ITEM = FIRST_MENU_ID + 8;
	public static final int BLUETOOTH_MENU_ITEM = FIRST_MENU_ID + 9;

	private static EditText transferText;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, HOLD_MENU_ITEM, 0, R.string.menu_hold);
		m.setIcon(android.R.drawable.stat_sys_phone_call_on_hold);
		m = menu.add(0, SPEAKER_MENU_ITEM, 0, R.string.menu_speaker);
		m.setIcon(android.R.drawable.stat_sys_speakerphone);
		m = menu.add(0, MUTE_MENU_ITEM, 0, R.string.menu_mute);
		m.setIcon(android.R.drawable.stat_notify_call_mute);
		m = menu.add(0, ANSWER_MENU_ITEM, 0, R.string.menu_answer);
		m.setIcon(android.R.drawable.ic_menu_call);
		m = menu.add(0, BLUETOOTH_MENU_ITEM, 0, R.string.menu_bluetooth);
		m.setIcon(R.drawable.stat_sys_phone_call_bluetooth);
		m = menu.add(0, VIDEO_MENU_ITEM, 0, R.string.menu_video);
		m.setIcon(android.R.drawable.ic_menu_camera);
		m = menu.add(0, TRANSFER_MENU_ITEM, 0, R.string.menu_transfer);
		m.setIcon(android.R.drawable.ic_menu_call);			
		m = menu.add(0, HANG_UP_MENU_ITEM, 0, R.string.menu_endCall);
		m.setIcon(R.drawable.ic_menu_end_call);
				
		return result;
	}

	public void onClick(DialogInterface dialog, int which)
	{
		if (which == DialogInterface.BUTTON_POSITIVE)
			Receiver.engine(this).transfer(transferText.getText().toString());
	}

	private void transfer() {
		transferText = new InstantAutoCompleteTextView(Receiver.mContext,null);
		transferText.setInputType(InputType.TYPE_CLASS_TEXT |
					  InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		new AlertDialog.Builder(this)
			.setTitle(Receiver.mContext.getString(R.string.transfer_title))
			.setView(transferText)
			.setPositiveButton(android.R.string.ok, this)
			.setNegativeButton(android.R.string.cancel, this)
			.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		Intent intent = null;

		switch (item.getItemId()) {
		case HANG_UP_MENU_ITEM:
			Receiver.stopRingtone();
			Receiver.engine(this).rejectcall();
			break;
			
		case ANSWER_MENU_ITEM:
			Receiver.stopRingtone();
			Receiver.engine(this).answercall();
			break;
			
		case HOLD_MENU_ITEM:
			Receiver.engine(this).togglehold();
			break;

		case TRANSFER_MENU_ITEM:
			transfer();
			break;
			
		case MUTE_MENU_ITEM:
			Receiver.engine(this).togglemute();
			break;
					
		case SPEAKER_MENU_ITEM:
			Receiver.engine(this).speaker(RtpStreamReceiver.speakermode == AudioManager.MODE_NORMAL?
					AudioManager.MODE_IN_CALL:AudioManager.MODE_NORMAL);
			break;
			
		case BLUETOOTH_MENU_ITEM:
			Receiver.engine(this).togglebluetooth();
			break;
					
		case VIDEO_MENU_ITEM:
			if (Receiver.call_state == UserAgent.UA_STATE_HOLD) Receiver.engine(this).togglehold();
			try {
				intent = new Intent(this, org.sipdroid.sipua.ui.VideoCamera.class);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
			}
			break;
		}

		return result;
	}
	
	long enabletime;
    KeyguardManager mKeyguardManager;
    KeyguardManager.KeyguardLock mKeyguardLock;
    boolean enabled;
    
	void disableKeyguard() {
    	if (mKeyguardManager == null) {
	        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
	        mKeyguardLock = mKeyguardManager.newKeyguardLock("Sipdroid");
	        enabled = true;
    	}
		if (enabled) {
			mKeyguardLock.disableKeyguard();
			enabled = false;
			enabletime = SystemClock.elapsedRealtime();
		}
	}
	
	void reenableKeyguard() {
		if (!enabled) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			mKeyguardLock.reenableKeyguard();
			enabled = true;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (Integer.parseInt(Build.VERSION.SDK) >= 5 && Integer.parseInt(Build.VERSION.SDK) <= 7)
			disableKeyguard();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (Integer.parseInt(Build.VERSION.SDK) >= 5 && Integer.parseInt(Build.VERSION.SDK) <= 7)
			reenableKeyguard();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (Integer.parseInt(Build.VERSION.SDK) < 5 || Integer.parseInt(Build.VERSION.SDK) > 7)
			disableKeyguard();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (Integer.parseInt(Build.VERSION.SDK) < 5 || Integer.parseInt(Build.VERSION.SDK) > 7)
			reenableKeyguard();
	}

}
