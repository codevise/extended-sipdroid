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
package org.sipdroid.codecs;

import org.sipdroid.sipua.ui.Receiver;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

class CodecBase implements Preference.OnPreferenceChangeListener {
	protected String CODEC_NAME;
	protected String CODEC_USER_NAME;
	protected int CODEC_NUMBER;
	protected String CODEC_DESCRIPTION;
	protected String CODEC_DEFAULT_SETTING = "never";

	private boolean loaded = false;
	private boolean enabled = false;
	private boolean edgeOnly = false,edgeOr3GOnly = false;
	private String value;

	void update() {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(Receiver.mContext);
		value = sp.getString(CODEC_NAME, CODEC_DEFAULT_SETTING);
		updateFlags(value);		
	}
	
	void load() {
		update();
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}
    
	public void enable(boolean e) {
		enabled = e;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean edgeOnly() {
		return enabled && edgeOnly;
	}
	
	public boolean edgeOr3GOnly() {
		return enabled && edgeOr3GOnly;
	}

	public String name() {
		return CODEC_NAME;
	}

	public String userName() {
		return CODEC_USER_NAME;
	}

	public String getTitle() {
		return CODEC_USER_NAME + " (" + CODEC_DESCRIPTION + ")";
	}

	public int number() {
		return CODEC_NUMBER;
	}

	public void setListPreference(ListPreference l) {
		l.setOnPreferenceChangeListener(this);
		l.setValue(value);
	}

	public boolean onPreferenceChange(Preference p, Object newValue) {
		ListPreference l = (ListPreference)p;
		value = (String)newValue;

		updateFlags(value);

		l.setValue(value);
		l.setSummary(l.getEntry());

		return true;
	}

	private void updateFlags(String v) {

		if (v.equals("never")) {
			enabled = false;
		} else {
			enabled = true;
			if (v.equals("edge"))
				edgeOnly = true;
			else
				edgeOnly = false;
			if (v.equals("edgeor3g"))
				edgeOr3GOnly = true;
			else
				edgeOr3GOnly = false;
		}
	}

	public String toString() {
		return "CODEC{ " + CODEC_NUMBER + ": " + getTitle() + "}";
	}
}
