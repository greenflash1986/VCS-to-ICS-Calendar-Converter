# VCS to ICS Calendar Converter

This tool converts calender entries from the older VCS (vCalendar) format to the newer ICS format (iCalendar). Although iCalendar is mostly downwards compatible some special fields are not, for example "repeat rules". These tool will convert this fields.

It was originally written by dragomerlin and extended by me to convert my Nokia-Phone calendar files.
  
Author(s):

- [dragomerlin](http://dragomerlin.users.sourceforge.net)
- [greenflash1986](http://github.com/greenflash1986/)

Source code: Java only  
Java Version: >= 1.8 (higher versions should also work, but it's untested)
Licenses: GPL
Platform: multi (java runtime dependant)
Interface type: command line only, console
Runtime: userspace
Permissions: read / write on jar's present dir for vcs and ics subfolders
Internal behaviour:

* read, convert and write strings of characters into text files
* decode quoted printable strings into UTF-8
* Convert calendars to version 2.0 (remove quoted-printable)

__Warning: Java may contain a bug where some letters will result in a unknown symbol
when applying the org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable
routine on strings that are not totally coded in quoted printable. This shouldn't happen,
and the issue was only found on windows, on vcs files provided by Microsoft Works. See
dair.vcs for more details. In this case, it is recommended to use other operating system
until it's solved. The program makes a quick check that is output to command line every time
it is run.__

## Usage

### How it works

It looks for vcs folder and ics folder. Under vcs folder looks for .vcs files and for each one that matches the
calendar format creates the corresponding file in the ics dir. Already **existent files are overwritten**.
Email field may be required for third party software.

### Getting the vcs files

- You can send them to your computer via bluetooth from the phone, some symbian devices allow it.
- Use third party software running inside your phone.
- Connect the device to the computer via USB or Bluetooth or Wifi.
- Create a NBU, NBF, NFB, NFC and ARC type backups and extract calendar files inside with NbuExplorer.
- Get the contacts in the native database that the device uses and use third party software to parse.

### How to install

You have to have Java ( >= 1.8) installed on your system or at least have a running Java executable. Please install Java according to your operating system.

Download [release 3.0|https://github.com/greenflash1986/VCS-to-ICS-Calendar-Converter/releases/download/v3.0/VCS2ICS-3.0.zip] and unzip it to an extra folder.

### How to use

- Delete the ics folder  
- Put the vcs files in vcs folder  

#### Run it

with **launch-from-windows.bat** (will prompt for email) or **directly from console**

if you are running the application directly from console, email can be specified or not. To specify use `-e` or `--email` arguments.

`java -jar VCS2ICS.jar` will prompt for email
`java -jar VCS2ICS.jar <somethingstrange>` will prompt as well
`java -jar VCS2ICS.jar -e` OR `java -jar VCS2ICS.jar --email` will use blank email and don't ask
`java -jar VCS2ICS.jar -e <youremail>` OR `java -jar VCS2ICS.jar --email <youremail>` will use that string.

### Tips

- Use an application like Notepad++ or Kate to edit files and see / change the encoding of the file itself.

## Known issues: 		
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

## Disclaimer / Notes
I fixed the issues / made this project in my spare time and used this project as
playground to improve some of my skills. Because of this I won't promise to do
further work on this project. I provided it online on Github for you that you can
use the (partially) fixed version. Hopefully it's useful for some people and / or 
feel free to fork or made contributions. I will do my best to honor these efforts.
