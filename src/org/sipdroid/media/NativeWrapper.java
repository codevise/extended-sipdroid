package org.sipdroid.media;


/**
 * Abstract class for native implementations
 * 
 * All native functions will be called through this class.
 * 
 * Controlling the MPG123 library need to be done through the service 
 * AIDL interface!
 * 
 *
 */
public class NativeWrapper {
	public static boolean initLib() {
		return ninitLib();
	}
	
	public static void cleanupLib() {
		ncleanupLib();
	}
	
	public static String getError() {
		return ngetError();
	}
	
	public static int initMP3(String filename) {
		return ninitMP3(filename);
	}
	
	public static void cleanupMP3() {
		ncleanupMP3();
	}
	
	public static boolean setEQ(int channel, double vol) {
		return nsetEQ(channel, vol);
	}
	
	public static void resetEQ() {
		nresetEQ();
	}
	
	public static AudioFileInformations getAudioInformations() {
		return ngetAudioInformations();
	}
	
	public static int decodeMP3(int bufferLen, short[] buffer) {
		return ndecodeMP3(bufferLen, buffer);
	}
	
	public static void seekTo(int frames) {
		nseekTo(frames);
	}
	
	
	
	/**
	 * 
	 * @return
	 */
	private static native boolean ninitLib();
	
	/**
	 * 
	 */
	private static native void ncleanupLib();
	
	/**
	 * 
	 * @return String explaining what went wrong
	 */
	private static native String ngetError();
	
	/**
	 * Initialize one MP3 file
	 * @param filename
	 * @return MPG123_OK
	 */
	private static native int ninitMP3(String filename);
	
	/**
	 * Cleanup all native needed resources for one MP3 file
	 */
	private static native void ncleanupMP3();
	
	/**
	 * 
	 * @param channel
	 * @param vol
	 * @return
	 */
	private static native boolean nsetEQ(int channel, double vol);
	
	/**
	 * 
	 */
	private static native void nresetEQ();
	
	/**
	 * 
	 * @return
	 */
	private static native AudioFileInformations ngetAudioInformations();
	
    /**
	 * Read, decode and write PCM data to our java application
	 * 
	 * @param bufferLen
	 * @param buffer
	 * @return 
	 */
    private static native int ndecodeMP3(int bufferLen, short[] buffer);
    
    /**
     * 
     * @param frames
     */
    private static native void nseekTo(int frames);
    
    /**
     * Our native MPEG (1,2 and 3) decoder library
     */
    static { System.loadLibrary("mp3"); }
}
