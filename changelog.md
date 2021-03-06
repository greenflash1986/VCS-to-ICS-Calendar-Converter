# v3.0
* change `-email` argument to `--email`, remove `email` 
* added support for RRULE
* use Java 8 and new DateTime-Classes internally
* convert multiple vcs files to one calendar file
* convert AALARM to .ics 

# v2.0
* Added support for multi-todo and multi-event calendar files.
* Now the version of the application is shown at runtime startup.
* Added folder Doc/untested_vcs with 55 new vcs files

# v1.7.5
* Now BOM is removed when reading files encoded in the following UTF formats: UTF-8 (with and without BOM), UTF_16LE, UTF_16BE, UTF_32LE, UTF_32BE. As specified in Oracle bug 4508058, BOM is not automatically removed for compatibility reasons so it has to be done manually.
* Added Apache Commons IO 2.4 to remove BOM from UTF encoded files.
* Added some php example code to decode quoted printable strings and a multi-event Microsoft Works vcs file to Doc folder.

# v1.7
* Apache Commons Codec updated to version 1.7
* JavaSE updated to 1.7
* Now all field names are accepted also when not uppercase. This was done for compatibility with Microsoft Works.
* Added a test that is run automatically to check whether org.apache.commons.codec.net.QuotedPrintableCodec.decodeQuotedPrintable is working on current operating system. Java may contain a bug where some characters are damaged when using Windows
* Added file dair.vcs

# v1.6.3
* Email can now be specified from command line as an argument to the jar file, with the syntax -email, email or -e. In case no email is specified after -e will use a blank email.

# v1.6.2
* Email can now be specified from command line as an argument to the jar file, with the syntax -email youremail.

# v1.6.1
* Fixed bug that caused multiline entries starting with a single space to be ignored.
* Added Doc folder with some documentation.
* Added multiline_with_single_space.vcs to test multiline fields starting with a single space instead of two.

# v1.6.0
* Added encoding detection of vcs files (thanks to org.mozilla.universalchardet)
* Now all ics files are saved as UTF-8 instead of the same encoding of the vcs file (ics files must be UTF-8 to hold any character).
* Added vcs files 5, 6 and 7 with different file encodings.
* Now compatible with Mozilla Lightning/Sunbird.

# v1.5.6
* Fixed bug that did not detect correctly the end of the first event of a file.
* Fixed bug that caused SUMMARY, LOCATION and DESCRIPTION to be treated as quoted-printable encoded even if they weren't.

# v1.5.5
* Fixed bug that caused DESCRIPTION to be null if the corresponding field didn't exist in vcs file.
* Added support for fields that are not quoted-printable encoded but multiline.
* Added vcs files 1, 2, 3 and 4 to test not quoted-printable multiline.

# v1.5
* Added complete support for UTF-8 quoted-printable encoding (thanks to org.apache.commons, under Apache license).
* Fixed bug that caused ics files to miss END statements
* Fixed bug that caused ics files to have a blank line at the end (CRLF)
* Added 'Quoted-printable chars (The Cairo, daily alarm).vcs' file to test deeply utf-8 encoded quoted-printable chars.

# v1.0.1
* Added support for STATUS:COMPLETED statement in ToDo files.
* Added TodoThings.vcs and TodoThings.ics files to test STATUS statement.

# v1.0.0
* Initial release