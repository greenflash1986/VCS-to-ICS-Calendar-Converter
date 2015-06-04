/** 
	Copyright 2011 dragomerlin
	
 	This file is part of VCS to ICS Calendar Converter.
  
    VCS to ICS Calendar Converter is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    VCS to ICS Calendar Converter is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with VCS to ICS Calendar Converter.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sourceforge.users.dragomerlin;

import java.io.ByteArrayInputStream;

class OddCharsException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OddCharsException(String msg){
	super(msg);
	}
}

public class HexCodec {
	  private static final char[] kDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
	      'b', 'c', 'd', 'e', 'f' };

	  public static byte[] hexToBytes(char[] hex) {
		  if ((hex.length & 0x01) != 0)
			try {
				throw new OddCharsException("Odd number of characters.");
			} catch (OddCharsException e) {
				e.printStackTrace();
			}
	    int length = hex.length / 2;
	    byte[] raw = new byte[length];
	    for (int i = 0; i < length; i++) {
	      int high = Character.digit(hex[i * 2], 16);
	      int low = Character.digit(hex[i * 2 + 1], 16);
	      int value = (high << 4) | low;
	      if (value > 127)
	        value -= 256;
	      raw[i] = (byte) value;
	    }
	    return raw;
	  }

	  public static byte[] hexToBytes(String hex) {
	    return hexToBytes(hex.toCharArray());
	  }
	  
	  public static char[] bytesToHex(byte[] raw) { 
		    int length = raw.length;
		    char[] hex = new char[length * 2];
		    for (int i = 0; i < length; i++) { 
		      int value = (raw[i] + 256) % 256;
		      int highIndex = value >> 4;
		      int lowIndex = value & 0x0f;
		      hex[i * 2 + 0] = kDigits[highIndex];
		      hex[i * 2 + 1] = kDigits[lowIndex];
		    } 
		    return hex;
		  } 
	  
	  public static String toHexadecimal(byte[] data) 
      { 
              String result=""; 
              ByteArrayInputStream input = new ByteArrayInputStream(data); 
              String cadAux; 
              int alreadyRead = input.read(); 
              while(alreadyRead != -1) 
              { 
                      cadAux = Integer.toHexString(alreadyRead); 
                      if(cadAux.length() < 2) //Add a 0 
                      	result += "0"; 
                      result += cadAux; 
                      alreadyRead = input.read(); 
              } 
              return result; 
      }
	}
