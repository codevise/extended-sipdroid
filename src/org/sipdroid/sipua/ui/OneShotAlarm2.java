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

import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.SipStack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OneShotAlarm2 extends BroadcastReceiver {

    @Override
	public void onReceive(Context context, Intent intent) {
    	if (!Sipdroid.release) Log.i("SipUA:","alarm2");
        if (SipStack.default_transport_protocols[0].equals(SipProvider.PROTO_TCP) &&
        		 Receiver.engine(context).isRegistered())
           	context.startService(new Intent(context,RegisterService.class));
        else
        	context.stopService(new Intent(context,RegisterService.class));
    }
}
