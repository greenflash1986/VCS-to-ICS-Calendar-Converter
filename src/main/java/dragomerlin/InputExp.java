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

package dragomerlin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class InputExp {
  public static String readd() {
 
	System.out.println("Enter here the email where you will store the calendar: ");
	String s = null;
 
	try{
	    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	    s = bufferRead.readLine();
	    // Always close streams
	    bufferRead.close();
 
	    System.out.println("Your saved email is: " + s + "\n");
	}
	catch(IOException e)
	{
		e.printStackTrace();
	}
	return s;
 
  }
  

		
}
