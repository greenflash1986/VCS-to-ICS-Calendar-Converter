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

/**
   Encodings that can be detected

    Chinese
        ISO-2022-CN
        BIG5
        EUC-TW
        GB18030
        HZ-GB-2312*

    Cyrillic
        ISO-8859-5
        KOI8-R
        WINDOWS-1251
        MACCYRILLIC
        IBM866
        IBM855 

    Greek
        ISO-8859-7
        WINDOWS-1253 

    Hebrew
        ISO-8859-8
        WINDOWS-1255 

    Japanese
        ISO-2022-JP
        SHIFT_JIS
        EUC-JP 

    Korean
        ISO-2022-KR
        EUC-KR 

    Unicode
        UTF-8
        UTF-16BE / UTF-16LE
        UTF-32BE / UTF-32LE / X-ISO-10646-UCS-4-3412* / X-ISO-10646-UCS-4-2143* 

    Others
        WINDOWS-1252 

 *Currently not supported by Java 
 */

package dragomerlin;

import org.mozilla.universalchardet.UniversalDetector;

public class TestDetector {
  public static String main(String string) throws java.io.IOException {
    byte[] buf = new byte[4096];
    String fileName = string;
    java.io.FileInputStream fis = new java.io.FileInputStream(fileName);

    // (1)
    UniversalDetector detector = new UniversalDetector(null);

    // (2)
    int nread;
    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
      detector.handleData(buf, 0, nread);
    }
    // (3)
    detector.dataEnd();

    // (4)
    String encoding = detector.getDetectedCharset();
    if (encoding != null) {
      System.out.println("Detected encoding = " + encoding);
    } else {
      System.out.println("No encoding detected.");
    }
    String encoding2 = encoding;
    // (5)
    detector.reset();
    return encoding2;
  }
}