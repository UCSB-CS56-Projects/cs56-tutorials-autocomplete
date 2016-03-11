cs56-tutorials-autocomplete
===========================

A small project containing simple classes and main programs that demonstrate how to build "autocomplete" widgets in a Swing GUI.

project history
===============
```
 N/A
```

Only one AutoComplete class written:
	Uses a JTextField for the search bar
	Uses a JComboBox for the popup menu
	The options correspond to the buttons at the bottom of the window
		When a selection is made, the button is pressed for you
		This is tied to the feature needed in the cs56-misc-map-gui project

	Three Listeners necessary for this to work:
		JTextField - DocumentListener:
			Listens for any keystroke in the JTextField
			Fires the suggestion box to list relevant options
		JTextField - KeyListener:
			Listens for important keys such as ENTER, ESCAPE, and arrow keys
			Fires the appropriate option
				ENTER - search for the selection made
				ESCAPE - close the drop down menu
				ARROWS - make a selection
		JComboBox - ActionListener:
			Listens for a selection from the dropdown menu
			Inserts selection into the box

	Key feature to help overcome obstacles with the cs56-misc-map-gui project:
		Method to track whether the JComboBox was being adjusted
		Problems arose when the popup menu and the search bar were out of sync with each other
		Solution, make sure they aren't editing each other at the same time

		Mehtods "setAdjusting" and "isAdjusting" keep track of a defined property of JComboBox "is_adjusting"

	Source for style and fixes to this AutoComplete Class:
	http://twaver.blogspot.com/2012/07/add-function-autocompletein-jtextfield.html

	Key Contributions:
		Using a JComboBox under the JTextField by setting the height to 0
		is_adjusting property to avoid runtime erros
		Cleaner GUI programming practices
		

W16 Final Remark
===========================
	Here is what we have done for this project
	1.separate it into a couple of classes
	2.make it like a real auto-complete on websites
	3.add a text area that makes it like a text searcher
 	4.add navigation arrows
	5.can search multiple words
	6.auto-suggest the next word that is most likely to follow the previous word
	
	We added two files named FollowFreq.java and FreqSuggest.java to implement the frequency suggestion feature.
	There still are some problems about this feature such as it only suggest the most frequent word and you are
	going to fix it as an issue. Overall not a very fun project but you can learn a lot from it. ps: other
	projects seem worse.
