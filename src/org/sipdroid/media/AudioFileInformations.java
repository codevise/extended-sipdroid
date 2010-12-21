package org.sipdroid.media;

class AudioFileInformations {
	public boolean success;
	public String error;
	public long rate;
	public int channels;
	public int encoding;
	public int bitratemode;
	public int bitrate;
	public long length;
	// TODO: Add meta data like IDv1 and IDv2 tags
	
	public enum mpg123_vbr 
	{
		MPG123_CBR,   
		MPG123_VBR,             
		MPG123_ABR              
	};

	public enum mpg123_mode 
	{
		MPG123_M_STEREO,      
		MPG123_M_JOINT,         
		MPG123_M_DUAL,          
		MPG123_M_MONO           
	};
}
