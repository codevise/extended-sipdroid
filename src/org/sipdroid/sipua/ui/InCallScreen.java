package org.sipdroid.sipua.ui;

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

import org.sipdroid.media.G711;
import org.sipdroid.sipua.R;
import org.sipdroid.sipua.UserAgent;
import org.sipdroid.sipua.phone.CallCard;
import org.sipdroid.sipua.phone.Phone;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class InCallScreen extends Activity {
	public static final int FIRST_MENU_ID = Menu.FIRST;
	public static final int HANG_UP_MENU_ITEM = FIRST_MENU_ID + 1;
	public static final int HOLD_MENU_ITEM = FIRST_MENU_ID + 2;
	public static final int MUTE_MENU_ITEM = FIRST_MENU_ID + 3;
	public static final int DTMF_MENU_ITEM = FIRST_MENU_ID + 4;

	private static Receiver m_receiver;
	CallCard mCallCard;
	Phone ccPhone;
	
    KeyguardManager mKeyguardManager;
    KeyguardManager.KeyguardLock mKeyguardLock;
    boolean enabled;
    
    public void onDestroy() {
		super.onDestroy();
		if (m_receiver != null) {
			unregisterReceiver(m_receiver);
			m_receiver = null;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		Receiver.isTop = false;
    	if (!Sipdroid.release) Log.i("SipUA:","on pause");
		if (Receiver.keepTop) Receiver.moveTop();
		reenableKeyguard();
	}
	
	void moveBack() {
		if (!Receiver.ccConn.isIncoming() || Receiver.ccCall.base != 0) {
	        Intent intent = new Intent(Intent.ACTION_VIEW, null);
	        intent.setType("vnd.android.cursor.dir/calls");
	        startActivity(intent);
		}
		moveTaskToBack(true);
	}
	
	void disableKeyguard() {
		if (enabled) {
			mKeyguardLock.disableKeyguard();
			enabled = false;
		}
	}
	
	void reenableKeyguard() {
		if (!enabled) {
			mKeyguardLock.reenableKeyguard();
			enabled = true;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Receiver.isTop = true;
    	if (!Sipdroid.release) Log.i("SipUA:","on resume");
		switch (Receiver.call_state) {
		case UserAgent.UA_STATE_INCOMING_CALL:
			callCardMenuButtonHint.setText(R.string.menuButtonHint2);
			callCardMenuButtonHint.setVisibility(View.VISIBLE);
			break;
		case UserAgent.UA_STATE_INCALL:
		case UserAgent.UA_STATE_HOLD:
            callCardMenuButtonHint.setText(R.string.menuButtonHint);
			callCardMenuButtonHint.setVisibility(View.VISIBLE);
			break;
		case UserAgent.UA_STATE_IDLE:
			callCardMenuButtonHint.setVisibility(View.INVISIBLE);
			if (Receiver.ccConn != null && Receiver.ccConn.date != 0) {
		        (new Thread() {
					public void run() {
						try {
							sleep(2000);
						} catch (InterruptedException e) {
						}
						moveBack();
					}
				}).start();   
			} else
				moveBack();
			break;
		case UserAgent.UA_STATE_OUTGOING_CALL:
			callCardMenuButtonHint.setVisibility(View.INVISIBLE);
			break;
		}
		if (Receiver.ccCall != null) mCallCard.displayMainCallStatus(ccPhone,Receiver.ccCall);
		disableKeyguard();
	}
	
	ViewGroup mInCallPanel;
	TextView callCardMenuButtonHint;
	
    public void initInCallScreen() {
        mInCallPanel = (ViewGroup) findViewById(R.id.inCallPanel);
        View callCardLayout = getLayoutInflater().inflate(
                    R.layout.call_card_popup,
                    mInCallPanel);
        mCallCard = (CallCard) callCardLayout.findViewById(R.id.callCard);
        mCallCard.reset();
        callCardMenuButtonHint = mCallCard.getMenuButtonHint();
        mCallCard.displayOnHoldCallStatus(ccPhone,null);
        mCallCard.displayOngoingCallStatus(ccPhone,null);
        
        // Have the WindowManager filter out touch events that are "too fat".
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES);
    }
    
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		G711.init();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.incall);
		
		initInCallScreen();

		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentfilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(m_receiver = new Receiver(), intentfilter);           

        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mKeyguardLock = mKeyguardManager.newKeyguardLock("Sipdroid");
        enabled = true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);

		MenuItem m = menu.add(0, HOLD_MENU_ITEM, 0, R.string.menu_hold);
		m.setIcon(R.drawable.sym_call_hold_on);
		m = menu.add(0, HANG_UP_MENU_ITEM, 0, R.string.menu_endCall);
		m.setIcon(R.drawable.sym_call_end);
		m = menu.add(0, MUTE_MENU_ITEM, 0, R.string.menu_mute);
		m.setIcon(R.drawable.mute);
		m = menu.add(0, DTMF_MENU_ITEM, 0, R.string.menu_dtmf);
		m.setIcon(R.drawable.sym_incoming_call_answer_options);
				
		return result;
	}
	
	@Override
	public void onOptionsMenuClosed(Menu menu) {
		super.onOptionsMenuClosed(menu);
		if (Receiver.call_state == UserAgent.UA_STATE_INCALL || Receiver.call_state == UserAgent.UA_STATE_HOLD) callCardMenuButtonHint.setVisibility(View.VISIBLE);	
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean result = super.onPrepareOptionsMenu(menu);

		callCardMenuButtonHint.setVisibility(View.INVISIBLE);
		if (Receiver.call_state == UserAgent.UA_STATE_INCALL || Receiver.call_state == UserAgent.UA_STATE_HOLD) {
			menu.findItem(HOLD_MENU_ITEM).setVisible(true);
			menu.findItem(MUTE_MENU_ITEM).setVisible(true);
			menu.findItem(DTMF_MENU_ITEM).setVisible(true);
		} else {
			menu.findItem(HOLD_MENU_ITEM).setVisible(false);
			menu.findItem(MUTE_MENU_ITEM).setVisible(false);
			menu.findItem(DTMF_MENU_ITEM).setVisible(false);
		}
		
		return result;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_MENU:
        	if (Receiver.call_state == UserAgent.UA_STATE_INCOMING_CALL) {
        		Receiver.engine(this).answercall();
        		return true;
        	}
        	break;
        	
        case KeyEvent.KEYCODE_CALL:
        	switch (Receiver.call_state) {
        	case UserAgent.UA_STATE_INCOMING_CALL: // does not come thru any more
        		Receiver.engine(this).answercall();
        		return true;
        	case UserAgent.UA_STATE_INCALL:
        	case UserAgent.UA_STATE_HOLD:
       			Receiver.engine(this).togglehold();
       			return true;
        	default:
	            // consume KEYCODE_CALL so PhoneWindow doesn't do anything with it
	            return true;
        	}

        // Note there's no KeyEvent.KEYCODE_ENDCALL case here.
        // The standard system-wide handling of the ENDCALL key
        // (see PhoneWindowManager's handling of KEYCODE_ENDCALL)
        // already implements exactly what the UI spec wants,
        // namely (1) "hang up" if there's a current active call,
        // or (2) "don't answer" if there's a current ringing call.

        case KeyEvent.KEYCODE_BACK:
        	if (!Sipdroid.release) Log.i("SipUA:","keycode back "+(SystemClock.uptimeMillis()-event.getEventTime()));
    		Receiver.engine(this).rejectcall();      
            return true;

        case KeyEvent.KEYCODE_CAMERA:
            // Disable the CAMERA button while in-call since it's too
            // easy to press accidentally.
        	return true;
        }
        return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = super.onOptionsItemSelected(item);
		Intent intent = null;

		switch (item.getItemId()) {
		case HANG_UP_MENU_ITEM:
			Receiver.engine(this).rejectcall();
			break;
			
		case HOLD_MENU_ITEM:
			Receiver.engine(this).togglehold();
			break;
			
		case MUTE_MENU_ITEM:
			Receiver.engine(this).togglemute();
			break;
					
		case DTMF_MENU_ITEM: {
			try {
				intent = new Intent(this, org.sipdroid.sipua.ui.DTMF.class);
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
			}
		}
			break;
		}

		return result;
	}

}
