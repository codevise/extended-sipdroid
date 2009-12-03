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
package org.sipdroid.pjlib;

import java.lang.String;

import org.sipdroid.sipua.ui.Sipdroid;

public class Codec {
    public static native int open(String codec_id);
    public static native int decode(byte alaw[], short lin[], int frames);
    public static native int encode(short lin[], int offset, byte alaw[], int frames);
    public static native int close();

    public static void init() {
    }
    
	static {
		try {
	        System.loadLibrary("pjlib_linker_jni");
	        open("gsm");
		} catch (Throwable e) {
			if (!Sipdroid.release) e.printStackTrace();
		}
	}
}
