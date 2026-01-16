# VCS to ICS Calendar Converter 
  
Author(s):

- [dragomerlin](http://dragomerlin.users.sourceforge.net)
- [greenflash1986](http://github.com/greenflash1986/)  

Source code: Java only  
Java Version: 1.8    
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

## Installation

This uses `bash`, so...
- UNIX-based OSes (MacOS/Linux): switch to `bash` shell with `bash` command
- Windows: run it inside [WSL](https://learn.microsoft.com/en-us/windows/wsl/install)

```
# install SDKMAN!
curl -s "https://get.sdkman.io" | bash

# update shell environment to include `sdk` utility
. ~/.bashrc

# install version of Java this project was made with (other versions may work, just haven't tested them)
sdk install java 8.0.472-sem

# use the Java version you just installed
sdk use java 8.0.472-sem

# check you're actually invoking that version of Java when running `java` (if not, you can just run the binary with the full path, which should be `~/.sdkman/candidates/java/8.0.472-sem/bin/java`)
java -version

# (try to) automatically install dependencies for your OS to run next commands

# thank you [@Mark](https://unix.stackexchange.com/users/236089/mark) for this script :)
# https://unix.stackexchange.com/a/571192
# https://creativecommons.org/licenses/by-sa/4.0/

packagesNeeded=(curl jq wget unzip tr date)
if [ -x "$(command -v apk)" ];
then
    sudo apk add -y --no-cache "${packagesNeeded[@]}"
elif [ -x "$(command -v apt-get)" ];
then
    sudo apt-get install "${packagesNeeded[@]}"
elif [ -x "$(command -v dnf)" ];
then
    sudo dnf install "${packagesNeeded[@]}"
elif [ -x "$(command -v zypper)" ];
then
    sudo zypper install "${packagesNeeded[@]}"
elif [ -x "$(command -v brew)" ];
then
    brew install "${packagesNeeded[@]}"
else
    echo "FAILED TO INSTALL PACKAGE: Package manager not found. You must manually install: "${packagesNeeded[@]}"">&2;
fi

# create and navigate to a temporary working directory (I do this to make sure the `unzip` and `java` commands run on the right file!)

mkdir $(date -Is | tr -d '+:\n') && cd $_

# download the latest release from the repo using GitHub CLI
curl https://api.github.com/repos/greenflash1986/VCS-to-ICS-Calendar-Converter/releases/1419374 | jq .assets[].browser_download_url | xargs wget

# unzip the downloaded archive
unzip *.zip

# run the program
java -jar *.jar

```

## How to use:
Delete the ics folder  
Put the vcs files in vcs folder  
Run it with launch-from-windows.bat 

 or

if you are running the application direct from console, email can be specified or not. To specify use `-e` or `--email` arguments.

`java -jar VCS2ICS.jar` will prompt for email  
`java -jar VCS2ICS.jar somethingstrange` will prompt as well  
`java -jar VCS2ICS.jar -e` OR `java -jar VCS2ICS.jar --email`will use blank email and don't ask  
`java -jar VCS2ICS.jar -e youremail` OR `java -jar VCS2ICS.jar --email youremail` will use that string.  


## How it works:
It looks for vcs folder and ics folder. Under vcs folder looks for .vcs files and for each one that matches the
calendar format creates the corresponding into the ics dir. Already existent are overwritten.
Email field may be required for third party software.   

## Getting the vcs files:
* You can send them to your computer via bluetooth from the phone, some symbian devices allow it.
* Use third party software running inside your phone.
* Connect the device to the computer via USB or Bluetooth or Wifi.
* Create a NBU, NBF, NFB, NFC and ARC type backups and extract calendar files inside with NbuExplorer.
* Get the contacts in the native database that the device uses and use third party software to parse.

## Tips:
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
