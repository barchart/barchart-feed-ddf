package com.barchart.feed.ddf.client.provider;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HistoricDataParsing {

	public static void main(final String[] args) throws Exception {
		
		final ZipFile zip = new ZipFile("/home/gavin/logs/Feed 20121203.zip");
		Enumeration<? extends ZipEntry> e = zip.entries();
		
		ZipEntry entry = (ZipEntry) e.nextElement();
		
		InputStreamReader inStreamReader = new InputStreamReader(zip.getInputStream(entry));
		BufferedReader buffReader = new BufferedReader(inStreamReader);
		
//		Socket output = new Socket(InetAddress.getLocalHost(), 7001);
//		output.setReuseAddress(true);
//		OutputStream outStream = new BufferedOutputStream(output.getOutputStream());
		
		String line = buffReader.readLine();
		
		while(line != null) {
			
			System.out.println(line);
			
			byte[] ba = line.getBytes();
			if(ba[ba.length-1] != 3) { // Ignore timestamp heartbeats
			
				byte[] outBytes = encodeTimestamp(ba);
				
				if((char)outBytes[1] == 'S') {
					outBytes = encodeSpreadSymbol(outBytes);
				} 
				
				//try {
					//outStream.write(outBytes);
				//} catch (final IOException io) {
					// Ignore broken pipe exceptions
				//}
				
			}
			
			line = buffReader.readLine();
		}
		
		buffReader.close();
		//outStream.close();
		
	}
	
	public static byte[] encodeTimestamp(final byte[] ba) {
			
		ByteBuffer buffer = ByteBuffer.allocate(ba.length - 17 + 9);
		buffer.put(ba, 0, ba.length - 17);
		buffer.put((byte) (((ba[ba.length - 17] - 48) * 10) + (ba[ba.length - 16] - 48)));
		buffer.put((byte) (((ba[ba.length - 15] - 48) * 10) + (ba[ba.length - 14] - 48)));
		buffer.put((byte) (((ba[ba.length - 13] - 48) * 10) + (ba[ba.length - 12] - 48)));
		buffer.put((byte) (((ba[ba.length - 11] - 48) * 10) + (ba[ba.length - 10] - 48)));
		buffer.put((byte) (((ba[ba.length - 9] - 48) * 10) + (ba[ba.length - 8] - 48)));
		buffer.put((byte) (((ba[ba.length - 7] - 48) * 10) + (ba[ba.length - 6] - 48)));
		buffer.put((byte) (((ba[ba.length - 5] - 48) * 10) + (ba[ba.length - 4] - 48)));
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putShort((short) (((ba[ba.length - 3] - 48) * 100) + ((ba[ba.length - 2] - 48) * 10) + (ba[ba.length - 1] - 48)));

		return buffer.array();
		
	}
	
	public static byte[] encodeSpreadSymbol(final byte[] ba) {
		
		int pos = getIndexOf(ba, ',', 0);

        String symbol = parseStringValue(ba, 2, pos - 2);
        char subrecord = (char) ba[pos + 1];

        int spStart = pos + 7;
        String spreadType = parseStringValue(ba, pos + 7, 2);
        int numberOfLegs = parseIntValue(ba, pos + 9, 1);
        String[] legs = new String[numberOfLegs];
        legs[0] = symbol;

        pos += 10;
        for (int i = 1; i < numberOfLegs; i++) {
            int pos2 = getIndexOf(ba, ',', pos);
            legs[i] = parseStringValue(ba, pos, pos2 - pos);
            pos = pos2 + 1;
        }

        int start2 = pos;
        switch (subrecord) {
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
                // N.B. Keep the comma
                start2--;
                break;
        }

        int length = spStart + (ba.length - start2);
        byte[] ba2 = new byte[length];

        System.arraycopy(ba, 0, ba2, 0, spStart);
        System.arraycopy(ba, start2, ba2, spStart, ba.length - start2);

        StringBuilder sb = new StringBuilder("_S_" + spreadType);
        for (String s : legs) {
            sb.append("_" + s);
        }


        byte[] ba3 = new byte[ba2.length - symbol.length() + sb.length()];
        ba3[0] = 1;
        ba3[1] = 50;

        for (int i = 0; i < sb.length(); i++) {
            ba3[2 + i] = (byte)sb.charAt(i);
        }

        System.arraycopy(ba2, 2 + symbol.length(), ba3, 2 + sb.length(), ba3.length - (2 + sb.length()));

        //return new Object[] { subrecord, spreadType, numberOfLegs, legs, ba2, ba3 };
        return ba3;
		
	}
	
    public static int getIndexOf(final byte[] ba, final char c, final int pos) {
    	
    	byte target = String.valueOf(c).getBytes()[0];
    	int tempPos = pos;
    	
    	while(tempPos < ba.length) {
    		
    		if(ba[tempPos] == target) {
    			return tempPos;
    		}
    		
    		tempPos++;
    	}
    	
    	return -1;
    }
    
    public static String parseStringValue(final byte[] ba, final int start, final int len) {
    	
    	final char[] result = new char[len];
    	
    	for(int i = 0; i < len; i++) {
    		result[i] = (char)ba[start+i];
    	}
    	
    	return new String(result);
    }
    
    public static int parseIntValue(final byte[] ba, final int start, final int len) {
    	return Integer.parseInt(parseStringValue(ba, start, len));
    }
    
}
