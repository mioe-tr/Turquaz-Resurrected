package com.turquaz.engine.config;

import com.turquaz.common.Compression;
import com.turquaz.engine.EngConfiguration;

public class EngCompression
{
	private static int COMPRESSION_METHOD=-1;
	
	public static void setCompressionMethod(int compression)
	{
		if (compression == Compression.LZMA || compression== Compression.ZIP)
		{
			COMPRESSION_METHOD=compression;
			EngConfiguration.setString("compression", String.valueOf(compression));
		}
	}
	
	public static int getCompressionMethod()
	{
		if (COMPRESSION_METHOD==-1)
		{
			String compress=EngConfiguration.getString("compression");
			if (compress != null)
			{
				int compression;
				try
				{
					compression=Integer.parseInt(compress);
				}
				catch(Exception ex)
				{
					compression=Compression.LZMA; //default
				}
				if (compression != Compression.LZMA && compression != Compression.ZIP)
					compression=Compression.LZMA;
				COMPRESSION_METHOD=compression;
			}
			else
			{
				COMPRESSION_METHOD=Compression.LZMA;
			}
				
		}
		return COMPRESSION_METHOD;	
	}
	
	
}
