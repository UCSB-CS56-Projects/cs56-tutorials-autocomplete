package edu.ucsb.cs56.projects.tutorial.autocomplete;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Dimension;
import java.util.ArrayList;


/**
 * Class to test an auto complete function
 * @author Noah Malik and Jonathan Moody
 */

public class AutoComplete {
    
    JFrame frame = new JFrame("AutoComplete Test");
    JPanel leftPanel = new JPanel();
    JPanel rightPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JPanel southPanel = new JPanel();
    JPanel midPanel = new JPanel();
    JTextArea options = new JTextArea(10, 15);
    JLabel optionsLabel = new JLabel("Options:");
    JTextField searchBar = new JTextField(20);
    JLabel searchBarLabel = new JLabel("Search:");
    final DefaultComboBoxModel suggestBoxModel = new DefaultComboBoxModel();
    final JComboBox suggestBox = new JComboBox(suggestBoxModel);
    static final ArrayList<String> optionsList = new ArrayList<String>();
    JTextArea text_area = new JTextArea(10, 30);
    JScrollPane sp = new JScrollPane(text_area);

	static {
	optionsList.add("Abalone");
	optionsList.add("Apple Pie");
	optionsList.add("apples");
	optionsList.add("Bilge");
	optionsList.add("Cartoons");
	optionsList.add("crusty");
	optionsList.add("Czechoslovakia");

    }
    /**
       Checks to see if a JComboBox is adjusting, meaning there is a change being made to it.
       @param cb A JComboBox used as the suggestions list
       @return True if cb has the property "is_adjusting" set to true.
    */

    private static boolean isAdjusting(JComboBox cb) {
	if (cb.getClientProperty("is_adjusting") instanceof Boolean) {
	    return ((Boolean) cb.getClientProperty("is_adjusting"));
	}
	return false;
    }
    /**
       Gives a JComboBox the property "is_adjusting" if not already created and sets it to true or false.
       @param cb A JComboBox used as the suggestions list
       @param tof Used to set the "is_adjusting" property to true or false.
    */
		
    private static void setAdjusting(JComboBox cb, boolean tof) {
	cb.putClientProperty("is_adjusting", tof);
    }
		
    /**
       Prepares all the widgets, panels, buttons, and listeners for the AutoComplete window.
    */
    public void go() {

	// Add options to select from search bar
	options.setText("Abalone \n"
			+ "Apple Pie \n"
			+ "apples \n"
			+ "Bilge \n"
			+ "Cartoons \n"
			+ "crusty \n"
			+ "Czechoslovakia");

	// Prepare widgets
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	options.setEditable(false);
	searchBar.setLayout(new BorderLayout());
	searchBar.add(suggestBox, BorderLayout.SOUTH);
	suggestBox.setPreferredSize(new Dimension(searchBar.getPreferredSize().width, 0));

	// Prepare panels
	midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
	leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
	rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
	southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

	// Prepare buttons
	// for(JButton b : buttonList) {
	// 	b.setPreferredSize(new Dimension((30 + 10*b.getText().length()), 20));
	// 	bottomPanel.add(b);
	// }
	// Add Listeners
	searchBar.addKeyListener(new SearchBarKeyListener());
	text_area.getDocument().addDocumentListener(new MyTextListener());
	searchBar.getDocument().addDocumentListener(new SearchBarDocumentListener());
	setAdjusting(suggestBox, false);
	suggestBox.setSelectedItem(null);
	suggestBox.addActionListener(new AutoCompleteListener());

	midPanel.setBackground(Color.WHITE);
	midPanel.setSize(500, 150);
	leftPanel.setBackground(Color.WHITE);
	leftPanel.add(optionsLabel);
	leftPanel.setSize(250, 150);
	leftPanel.add(options);
	options.setMaximumSize(options.getPreferredSize());
	//leftPanel.add(Box.createRigidArea(new Dimension(250,200)));
	rightPanel.setBackground(Color.WHITE);
	rightPanel.setSize(250, 150);
	rightPanel.add(searchBarLabel);
	rightPanel.add(searchBar);
	bottomPanel.setBackground(Color.WHITE);
	bottomPanel.setSize(800, 20);
	bottomPanel.add(sp);
	searchBar.setMaximumSize(searchBar.getPreferredSize());
	//rightPanel.add(Box.createRigidArea(new Dimension(250,300)));

	//midPanel.add(leftPanel);
	midPanel.add(Box.createRigidArea(new Dimension(350, 100)));
	midPanel.add(rightPanel);
	southPanel.add(bottomPanel);
	southPanel.add(Box.createRigidArea(new Dimension(800, 20)));
	frame.getContentPane().add(BorderLayout.CENTER, midPanel);
	frame.getContentPane().add(BorderLayout.SOUTH, southPanel);
	frame.setBackground(Color.WHITE);
	frame.setSize(820, 400);
	frame.setVisible(true);

    }// end go()
		
    /**
       Sets the text inside the searchBar JTextField to the selected item in the suggestBox JComboBox.
    */
    public void autoComplete() {
	searchBar.setText(suggestBox.getSelectedItem().toString());
	suggestBox.setPopupVisible(false);			
    }
    /**
       Checks to see if the text inside the searchBar JTextField is equal to any of the buttons.
       If this text matches a button, that button is clicked.
    */
    public void search() {
	String query = searchBar.getText();

	// for(JButton button : buttonList) {
	// 	if(query.equals(button.getText())) {
	// 		button.doClick();
	// 	}
	// }

    }// end search()
		
    /**
       Adds a button name to the suggestBox JComboBox 
       if the button name starts with the text inside the searchBar JTextField.
    */
    public void showOptions() {
	setAdjusting(suggestBox, true);
	suggestBoxModel.removeAllElements();
	String query = searchBar.getText();

	if(!query.isEmpty()) {
	    for(String item : optionsList) {
		if(item.toLowerCase().startsWith(query.toLowerCase())) {
		    suggestBoxModel.addElement(item);
		}
	    }
	}

	suggestBox.setPopupVisible(suggestBoxModel.getSize() > 0);
	setAdjusting(suggestBox, false);
    }// end showOptions()
		
    /**
       Listens for the Enter, Up Arrow, Down Arrow, and Escape keystrokes.
       If Enter is pressed, a search runs for the selection made.
       If Escape is pressed, the suggestBox JComboBox is closed.
       If the Up Arrow or Down Arrow are pressed, the selected item in the suggestBox JComboBox
       is moved up or down one.
    */
    class SearchBarKeyListener extends KeyAdapter {

	public void keyPressed(KeyEvent event) {
	    setAdjusting(suggestBox, true);
	    int keyCode = event.getKeyCode();

	    if(keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN) {
		//These following two lines forward the appropriate action to the JComboBox based on the key pressed?
		event.setSource(suggestBox);
		suggestBox.dispatchEvent(event);

		if(keyCode == KeyEvent.VK_ENTER) {
		    autoComplete();
		    search();
		}
	    }

	    if(keyCode == KeyEvent.VK_ESCAPE) {
		suggestBox.setPopupVisible(false);
	    }
	    setAdjusting(suggestBox, false);
	}

    }// end SearchBarListener
		
    /**
       Listens for keystrokes in the searchBar JTextField.
       Fires the suggestBox JComboBox to list relevent options.
    */
    class SearchBarDocumentListener implements DocumentListener {

	public void changedUpdate(DocumentEvent e) {
	    showOptions();
	}

	public void insertUpdate(DocumentEvent e) {
	    showOptions();
	}

	public void removeUpdate(DocumentEvent e) {
	    showOptions();
	}

    }// end SearchBarDocumentListener
		
    /**
       Listens for a selection from the suggestBox JComboBox.
       Inserts this selection into the searchBar JTextField.
    */
    class AutoCompleteListener implements ActionListener {

	public void actionPerformed(ActionEvent event) {
	    if(!isAdjusting(suggestBox) && suggestBox.getSelectedItem() != null) {
		autoComplete();
	    }
	}

    }// End SearchBarListener

    class MyTextListener implements DocumentListener{

	public void changedUpdate(DocumentEvent event){
	    optionsList.clear();
	    String s = text_area.getText();
	    int size = s.length();
	    String temp = "";
	    for(int i = 0; i < size; i++){
		if (s.charAt(i) != ' ' && s.charAt(i) != '\n'){
		    temp += s.charAt(i);
		}
		else{
		    if(temp != ""){
			optionsList.add(temp);
			temp = "";
		    }
		}
	    }
	    if(temp != ""){
		optionsList.add(temp);
	    }
	}
	public void insertUpdate(DocumentEvent event){
	    optionsList.clear();
	    String s = text_area.getText();
	    int size = s.length();
	    String temp = "";
	    for(int i = 0; i < size; i++){
		if (s.charAt(i) != ' ' && s.charAt(i) != '\n'){
		    temp += s.charAt(i);
		}
		else{
		    if(temp != ""){
			optionsList.add(temp);
			temp = "";
		    }
		}
	    }
	    if(temp != ""){
		optionsList.add(temp);
	    }
	}

	public void removeUpdate(DocumentEvent event){
	    optionsList.clear();
	    String s = text_area.getText();
	    int size = s.length();
	    String temp = "";
	    for(int i = 0; i < size; i++){
		if (s.charAt(i) != ' ' && s.charAt(i) != '\n'){
		    temp += s.charAt(i);
		}
		else{
		    if(temp != ""){
			optionsList.add(temp);
			temp = "";
		    }
		}
	    }
	    if(temp != ""){
		optionsList.add(temp);
	    }
	}

    }
    
    /**
       Runs the go method to create a new AutoComplete window.
    */
    public static void main(String[] args) {
	AutoComplete a = new AutoComplete();
	a.go();
    }
}
