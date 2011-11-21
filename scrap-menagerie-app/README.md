Scrap Menagerie
---------------

A simple system clipboard utility.

#### Artifact

A pure Java library, with main class 
`org.hawkinssoftware.ui.util.scraps.ScrapMenagerieMain`

#### Installation

* To run the Scrap Menagerie as a standalone application
    * Get the latest [scrap-menagerie-app.zip]
    * Unzip it anywhere
    * Launch the **scrap-menagerie.bat** file
        + double-click from Windows Explorer, or
        + **cd** to the directory in a DOS prompt and enter 
          **scrap-menagerie.bat**
* To run the Scrap Menagerie from Eclipse
    * Import these projects into Eclipse using 
      **File > Import... > Maven Projects**
        + [scrap-menagerie]
        + [scrap-menagerie-app]
    * Create an Eclipse run profile for class 
      **ScrapMenagerieMain** with VM args:
        + -javaagent:target/rns-agent.jar 
        + -Djava.library.path=target/native 
        + -Dsun.awt.noerasebackground=true 
        + -Dsun.java2d.noddraw=true 
        + -Dsun.java2d.d3d=false

[scrap-menagerie]: https://github.com/byron-hawkins/org.hawkinssoftware.scrap-menagerie/blob/master/scrap-menagerie/README.md
[scrap-menagerie-app]: https://github.com/byron-hawkins/org.hawkinssoftware.scrap-menagerie-app/blob/master/scrap-menagerie-app/README.md
[scrap-menagerie-app.zip]: http://www.hawkinssoftware.net/oss/bin/scrap-menagerie-app.zip

#### Features

1. **System Clipboard monitor**
    * The contents of the system clipboard are displayed
        + The clipboard is continuously monitored for changes
1. **Clipboard History**
    * Each change to the clipboard causes a new entry to be added
      to the history list. 
        + Duplicate entries will be automatically removed
    * To re-copy a clip from the history list:
        + Select the clip in the history list
        + Press the **Re-Copy** button 
1. **Typing Fragments**
    * Capture keyboard input into a fragment for later copying
        + Press the "+" button above the list to create a fragment
        + Click the toggle button on the fragment list item to 
          start keyboard input capture
        + Start typing in any application
        + Click the toggle button again to end capture
    * To copy a fragment into the clipboard:
        + Select the fragment in the list
        + Press the **Re-Copy** button
    * Multiple fragments may capture input simultaneously
    * To remove a fragment:
        + Select the fragment 
        + Press the "-" button above the list 
1. **Clipboard Text Transforms**
    1. Change the capitalization of the first letter of the 
       current clipboard contents. 
        * When writing code, this automatically changes a copied 
          classname into a relatable variable name
    1. Simplify the current clipboard contents to plain text
        * Removes the formatting from text copied out of a Word 
          document or an HTML web page
        * Reduces a set of copied files to a comma-separated list
          of filenames (path excluded)
1. **Pin**
    * The console is "always on top" when pinned
        + Click the pin and click away to hide it
    * Raise the console by pressing the keyboard trigger
        + The default is **ctrl+D**
            - See the **Shortcuts** section to customize
        + The mouse buttons works while keys are pressed on the 
          keyboard (thanks to [azia-native-input]), so you can pin 
          the console while it is raised 
        
[azia-native-input]: https://github.com/byron-hawkins/org.hawkinssoftware.azia-native-input/blob/master/azia-native-input/README.md
        
#### Shortcuts

The Scrap Menagerie traps and consumes all keystrokes that occur 
while its special keyboard trigger is active. 

* The default trigger is **ctrl+D**
* Each of the following key shortcuts implies **Trigger+Key**.

<table border="1" cellpadding="3" style="margin-left: 10px">
	<tr>
		<th>Key</th>
		<th>Feature</th>
	</tr>
    <tr>
    	<td>&lt;up-arrow&gt;</td>
    	<td>Move the focused list selection up</td>
    </tr>
    <tr>
    	<td>&lt;down-arrow&gt;</td>
    	<td>Move the focused list selection down</td>
    </tr>
    <tr>
    	<td>&lt;page-up&gt;</td>
    	<td>Move the focused list selection up a page</td>
    </tr>
    <tr>
    	<td>&lt;page-down&gt;</td>
    	<td>Move the focused list selection down a page</td>
    </tr>
    <tr>
    	<td>&lt;enter&gt;</td>
    	<td>Re-copy the selected item from the focused list</td>
    </tr>
    <tr>
    	<td>6</td>
    	<td>Move the focused list selection to the top</td>
    </tr>
    <tr>
    	<td>7</td>
    	<td>Toggle focus between the lists</td>
    </tr>
    <tr>
    	<td>8</td>
    	<td>Change the capitalization of the first letter of the
	    	current clipboard contents</td>
    </tr>
    <tr>
    	<td>9</td>
    	<td>Simplify the current clipboard contents to plain 
    		text</td>
    </tr>
    <tr>
    	<td>0</td>
    	<td>Toggle capture on the selected fragment (if any)</td>
    </tr>
    <tr>
    	<td>+</td>
    	<td>Add a new fragment</td>
    </tr>
    <tr>
    	<td>-</td>
    	<td>Remove the selected fragment (if any)</td>
    </tr>
    <tr>
    	<td>P</td>
    	<td>Toggle the Scrap Menagerie console pin</td>
    </tr>
</table>      

Customize your shortcut keys by placing a file named 
**shortcuts.properties** anywhere on the classpath (most easily
the installation root, next to the **scrap-menagerie.bat** file).
The file format is the standard [Java properties][properties] 
format. Find an example of the property names inside the 
**/lib/scrap-menagerie*.jar** file at 
**/config/default-shortcuts.properties**.

[properties]: http://en.wikipedia.org/wiki/.properties    
        
        