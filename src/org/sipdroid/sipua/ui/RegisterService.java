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

package org.sipdroid.sipua.ui;

import org.sipdroid.sipua.UserAgent;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RegisterService extends Service {
    @Override
    public void onCreate() {
    	super.onCreate();
        Receiver.engine(this).isRegistered();
    }
    
    @Override
    public void onStart(Intent intent, int id) {
         super.onStart(intent,id);
         if (SipStack.default_transport_protocols[0].equals(SipProvider.PROTO_TCP)
        		 || Receiver.call_state != UserAgent.UA_STATE_IDLE)
        	 Receiver.alarm(10*60, OneShotAlarm2.class);
         else
        	 Receiver.alarm(45, OneShotAlarm2.class);
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
}
