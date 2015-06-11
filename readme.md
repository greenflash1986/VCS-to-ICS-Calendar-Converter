# General

Project name: VCS to ICS Calendar Converter  
Latest version: @version@ 
Author(s): @authors@  
Source code: Java only  
JDK: 1.8.0_45  
Licenses: GPL, LGPL, Apache, MPL  
Platform: multi (java runtime dependant)  
Interface type: command line only, console  
Runtime: userspace  
Permissions: read / write on jar's present dir for vcs and ics subfolders  
Internal behaviour:

* read, convert and write strings of characters into text files
* decode quoted printable strings into UTF-8
* Convert calendars to version 2.0 (remove quoted-printable)
					
**Warning: Java may contain a bug where some letters will result in a unknown symbol when applying the org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable routine on strings that are not totally coded in quoted printable. This shouldn't happen, and the issue was only found on windows, on vcs files provided by Microsoft Works. See dair.vcs for more details. In this case, it is recommended to use other operating system until it's solved. The program makes a quick check that is output to command line every time it is run.**

# How it works:
It looks for vcs folder and ics folder. Under vcs folder looks for .vcs files and for each one that matches the
calendar format creates the corresponding into the ics dir. Already existent are overwritten.
Email field may be required for third party software.   
When running the application email can be specified or not. If specified use '-e', 'email' or '-email' arguments.

'java -jar calconv.jar' will prompt for email  
'java -jar calconv.jar somethingstrange' will prompt as well  
'java -jar calconv.jar -e' will use blank email and don't ask  
'java -jar calconv.jar -e youremail' will use that string.  

# Getting the vcs files:
* You can send them to your computer via bluetooth from the phone, some symbian devices allow it.
* Use third party software running inside your phone.
* Connect the device to the computer via USB or Bluetooth or Wifi.
* Create a NBU, NBF, NFB, NFC and ARC type backups and extract calendar files inside with NbuExplorer.
* Get the contacts in the native database that the device uses and use third party software to parse.

# Tips:
- Use an application like Notepad++ or Kate to edit files and see / change the encoding of the file itself.
				
# Known issues: 		
* VCS files, when using quoted printable format, it is always expected to be ASCII and UTF-8 charset even if no
 specified (ENCODING=QUOTED-PRINTABLE;CHARSET=UTF-8). Other charsets different than UTF-8 for printable are
 unsupported and non standard.
* Some applications may limit the number of characters per field when importing ics files, truncating the lines
 and thus resulting in a loss of data.
* Alarms are not converted since each Nokia phone may use different labels for fields or structure for specifying
 it. The same way, each software may automatically overwrite or skip alarm settings or automatically overwrite
 with a new one. Some alarm settings are not exportable because are too much specific for the device, for example
 using an internal ringtone on Nokia that does not exist on iCal; system pop-up notifications or vibration
 reminders. There are no complete specifications as well.
* Other issues may be present on third parties software due to closed specifications.