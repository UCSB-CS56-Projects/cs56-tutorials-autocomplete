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
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.event.*;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;


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
    JButton nextButton = new JButton("next");
    JButton preButton = new JButton("prev");
    final DefaultComboBoxModel suggestBoxModel = new DefaultComboBoxModel();
    final JComboBox suggestBox = new JComboBox(suggestBoxModel);
    static final ArrayList<String> optionsList = new ArrayList<String>();
    JTextArea text_area = new JTextArea(10, 30);
    JScrollPane sp = new JScrollPane(text_area);
    int start =0;
    int end =0;
    //all of the start index of s's full text
    ArrayList<Integer> starts = new ArrayList<Integer>();
    int currentIndex = 0;
    int wordSize = 0;
    //first word of s
    String first = "";
    //record the index of the first word of the s
    ArrayList<Integer> firstWordIndex = new ArrayList<Integer>();

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
	//add ActionListener for NextButton and PrevButton
	nextButton.addActionListener(new NextListener());
	preButton.addActionListener(new PreListener());

	// Prepare widgets
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	options.setEditable(false);
	searchBar.setLayout(new BorderLayout());
	searchBar.add(suggestBox, BorderLayout.SOUTH);
	suggestBox.setPreferredSize(new Dimension(searchBar.getPreferredSize().width, 0));

	// Prepare panels
	midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.X_AXIS));
	leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
	rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
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
	rightPanel.add(preButton);
	rightPanel.add(nextButton);
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
	frame.setSize(1120, 400);
	frame.setVisible(true);

    }// end go()
		
    /**
       Sets the text inside the searchBar JTextField to the selected item in the suggestBox JComboBox.
    */
    public void autoComplete() {
	first = "";
	starts.clear();
	start =0;
	end =0;
	String s = suggestBox.getSelectedItem().toString();
	wordSize = s.length();

	//get the first word
	for(int i =0; i<wordSize; i++){
	    char tmp = s.charAt(i);
	    if(tmp!=' '&& tmp!='\n' && tmp!='\t')
		first+=tmp;
	    else
		break;
	}
	
	searchBar.setText(s);
	suggestBox.setPopupVisible(false);
	String all_text = text_area.getText();
	int size = all_text.length();
	String temp = "";
	 for(int i = 0; i < size; i++){
	     if (all_text.charAt(i) != ' ' && all_text.charAt(i) != '\n'){
	 	temp += all_text.charAt(i);
	     }
	     else{
	 	if(temp != ""){
	 	    if(temp.equals(first)){
			firstWordIndex.add(start);
	 		end = i;
		    }
	 	    temp = "";		    
	 	}
		start = i+1;
	     }
      
	 }
	  if(temp.equals(first)){
	      firstWordIndex.add(start);
	  }

	  //size of the firstWordIndex
	  int firstSize = firstWordIndex.size();
	  boolean sameText = true;
	  for(int i =0; i<firstSize; i++){
	      sameText = true;
	      int startPoint = firstWordIndex.get(i);
	      for(int j=0; j<wordSize; j++){
		  if((j+startPoint)>=size)
		      break;
		  if(all_text.charAt(j+startPoint) !=s.charAt(j)){
		      sameText = false;
		      break;
		  }
	      }
	      if(sameText)
		  starts.add(firstWordIndex.get(i));
	  }
	  
	  if(starts.size()==0)
	      return;
	try {
	    text_area.getHighlighter().removeAllHighlights();
	    Highlighter highlighter = text_area.getHighlighter();
	    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
	    highlighter.addHighlight(starts.get(0), starts.get(0)+wordSize, painter);
	    Rectangle viewRect = text_area.modelToView(starts.get(0));
	    text_area.scrollRectToVisible(viewRect);
	    text_area.setCaretPosition(starts.get(0)+wordSize);
	    text_area.moveCaretPosition(starts.get(0));
	}
	catch(BadLocationException ex){
	}
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
	//optionsList.clear();
	String query = searchBar.getText();
	String text = text_area.getText();
	FreqSuggest all_suggests = new FreqSuggest(text);
	Hashtable<String, ArrayList<FollowFreq>> table = all_suggests.GetTable();
	boolean flag = false;
	String current = "";
	String input = "";
	int max = 0;
	if(!query.isEmpty()) {
	    for(String item : optionsList) {
		if(item.toLowerCase().startsWith(query.toLowerCase())) 
		    suggestBoxModel.addElement(item);
	    }
	    
	    char last = query.charAt(query.length()-1);
	    if (last == ' '){
       	for(int i = query.length() - 2; i >= 0 && query.charAt(i) != ' '; i--)
		    current = String.valueOf(query.charAt(i)).concat(current);
		if(!current.equals("")){
		    if(table.containsKey(current)){
			for(FollowFreq f: table.get(current)){
			    if(input.equals("") || f.freq > max){
				input = f.word;
				max = f.freq;
			    }
			}
			optionsList.add(query + input);
			suggestBoxModel.addElement(query + input);
		    }
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

    //listener for next
    class NextListener implements ActionListener{
	public void actionPerformed(ActionEvent event){
	    if(currentIndex<(starts.size()-1)){
		currentIndex+=1;
		try {
		    text_area.getHighlighter().removeAllHighlights();
		    Highlighter highlighter = text_area.getHighlighter();
		    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
		    highlighter.addHighlight(starts.get(currentIndex), starts.get(currentIndex)+wordSize, painter);
		    Rectangle viewRect = text_area.modelToView(starts.get(currentIndex));
		    text_area.scrollRectToVisible(viewRect);
		    text_area.setCaretPosition(starts.get(currentIndex)+wordSize);
		    text_area.moveCaretPosition(starts.get(currentIndex));
		}
		catch(BadLocationException ex){
		    
		}
	    }
	}

    }

    //listener for pre
    class PreListener implements ActionListener{
	public void actionPerformed(ActionEvent event){
	    if(currentIndex>0){
		currentIndex-=1;
		try {
		    text_area.getHighlighter().removeAllHighlights();
		    Highlighter highlighter = text_area.getHighlighter();
		    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
		    highlighter.addHighlight(starts.get(currentIndex), starts.get(currentIndex)+wordSize, painter);
		    Rectangle viewRect = text_area.modelToView(starts.get(currentIndex));
		    text_area.scrollRectToVisible(viewRect);
		    text_area.setCaretPosition(starts.get(currentIndex)+wordSize);
		    text_area.moveCaretPosition(starts.get(currentIndex));
		}
		catch(BadLocationException ex){
		    
		}
	    }
	}

    }
    

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
		    if(temp != ""&&optionsList.contains(temp)!=true){
			optionsList.add(temp);
		    }
		    temp = "";
		}
	    }
	    if(temp != ""&&optionsList.contains(temp)!=true){
		optionsList.add(temp);
	    }
	    temp = "";
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
		    if(temp != ""&&optionsList.contains(temp)!=true){
			optionsList.add(temp);
		    }
		    temp = "";
		}
	    }
	    if(temp != ""&&optionsList.contains(temp)!=true){
		optionsList.add(temp);
	    }
	    temp = "";
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
		    if(temp != ""&&optionsList.contains(temp)!=true){
			optionsList.add(temp);
		    }
		    temp = "";
		}
	    }
	    if(temp != ""&&optionsList.contains(temp)!=true){
		optionsList.add(temp);
	    }
	    temp = "";
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

// class FollowFreq{
//     public String word;
//     public int freq;
    
//     public FollowFreq(){word = ""; freq = 0;}
//     public FollowFreq(String word, int freq){ this.word = word; this.freq = freq;}
    
// };

// class FreqSuggest{
//     private Hashtable<String, ArrayList<FollowFreq>> freq_suggest;
    
//     public FreqSuggest(String input){
// 	freq_suggest = new Hashtable<String, ArrayList<FollowFreq>>();
// 	String front = "";
// 	String back = "";
	
// 	for(int i = 0; i < input.length(); i++){
// 	    if(input.charAt(i) != ' ' && input.charAt(i) != '\n')
// 		back += input.charAt(i);
// 	    else{
// 		//System.out.print(back);
// 		if(!front.equals("")){
// 		    if(freq_suggest.containsKey(front)){
// 			boolean flag = false;
// 			ArrayList<FollowFreq> temp = freq_suggest.get(front);
// 			for(FollowFreq f: temp){
// 			    if(f.word.equals(back)){
// 				flag = true;
// 				f.freq ++;
// 				break;
// 			    }
// 			}
// 			if(!flag)
// 			    temp.add(new FollowFreq(back, 1));
			
// 			freq_suggest.put(front, temp);
			
// 		    }
// 		    else{
// 			ArrayList<FollowFreq> temp = new ArrayList<FollowFreq>();
// 			temp.add( new FollowFreq(back, 1));
// 			//System.out.println(temp);
// 			freq_suggest.put(front, temp);
// 			//System.out.println(freq_suggest.get(front));
// 		    }
// 		}
// 		//System.out.println(temp);
// 		front = back;
// 		back = "";	
// 	    }
// 	}
// 	//	    for(String s: freq_suggest.keys()){
// 	//		ArrayList<FollowFreq> temp = java.Collectoins.sort(freq_suggest.get(s));
// 	//	freq_suggest.put(s, temp);
	
//     }
//     public Hashtable<String, ArrayList<FollowFreq>> GetTable(){ return this.freq_suggest;}
// };
