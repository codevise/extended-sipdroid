package org.sipdroid.sipua.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.zoolu.tools.Random;

import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;

public class Checkin {
	
	static long hold;
	
	static void url(final String opt,final boolean in_call) {
        (new Thread() {
			public void run() {
				try {
			        URL url = new URL(opt);
			        String line;
			        String[] lines;
			        BufferedReader in;
			        
					in = new BufferedReader(new InputStreamReader(url.openStream()));
					for (;;) {
						line = in.readLine();
						if (line == null) break;
						lines = line.split(" ");
						if (lines.length == 2) {
							if (PreferenceManager.getDefaultSharedPreferences(Receiver.mContext).getString("dns","").equals(lines[0])) {
								if (in_call) {
									hold = SystemClock.elapsedRealtime();
									Receiver.engine(Receiver.mContext).rejectcall();
								}
								Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(lines[1]));
								intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								Receiver.mContext.startActivity(intent);
							}
						}
					}
			        in.close();
				} catch (IOException e) {
					if (!Sipdroid.release) e.printStackTrace();
				}

			}
		}).start();   
	}

	public static void checkin(boolean in_call) {
		if (!in_call || SystemClock.elapsedRealtime() < hold + 5*60*1000 ||
				Random.nextInt(5) == 1)
			url("http://sipdroid.googlecode.com/svn/images/checkin",in_call);
	}
}
