package wordFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.*;
public class TextSamplerDemo extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
	//These variables are set to the default values that the text formatter will have
	//They will be changed by other methods and will be used later in the code
	//to format the text
	public int lineLength = 80;
	public int alignment = 0; //0 = left, 1 = center, 2 = right
	public boolean equalSpace = false;
	public boolean wrap = false;
	public int space = 0;//0 = single-spaced, 1 = double-spaced
	public boolean title = false;
	public int paragraphIndent = 0;
	public int blankLines = 0;
	public int columnNumber = 1;//this value can only be 1 or 2
	File selectedFile = null;
	
	JPanel panelIn;
	JPanel panelOut;
	JTextPane text1;
	JTextPane text2;
	JSplitPane splitPane;
	
	public static void main(String[] args){
		
		new TextSamplerDemo().setVisible(true);
	}
	
	public TextSamplerDemo() {
		super("Text Format Demo");
		setSize(800,800);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		createGUI();
	}
	
	
	public void createGUI() {

		JMenuBar bar = new JMenuBar();
		JMenu file = new JMenu("File"); 
		JMenuItem newItem = new JMenu("New");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem open = new JMenuItem("Open File");
		JMenuItem saveasFile = new JMenuItem("Save As...");
		JMenuItem recordErrors = new JMenuItem("Record Errors");
		
		//implementing ActionListener to all the item in menu
		JMenuItem[] items = {newItem, save, open, saveasFile, recordErrors};
		for(JMenuItem item : items){
			item.addActionListener(this);
		}
			
		newItem.add(open);	
		file.add(newItem);
		file.addSeparator();
		file.add(save);
		file.add(saveasFile);
		file.add(recordErrors);
		bar.add(file);
		
		setJMenuBar(bar);
		
		StyleContext context = new StyleContext();
	    StyledDocument document = new DefaultStyledDocument(context);
		
        //
		text1 = new JTextPane();
		text1.setEditable(false);
		text1.setFont(new Font("monospaced", Font.PLAIN, 12));
		text1.setBackground(Color.pink);
		
		text2 = new JTextPane(document);
		text2.setEditable(false);
		text2.setFont(new Font("monospaced", Font.PLAIN, 12));
		//text2.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		
		panelIn = new JPanel();
		panelIn.setBackground(Color.pink);
		panelIn.add(text1);
		panelOut = new JPanel();
		panelOut.setBackground(Color.WHITE);
		panelOut.add(text2);
	 
		//JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(panelIn), new JScrollPane(panelOut));

		JScrollPane inputScrollPane = new JScrollPane(panelIn);
		inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		inputScrollPane.setPreferredSize(new Dimension(250,100));
		inputScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Input Text"), 
				BorderFactory.createEmptyBorder(5,5,5,5)), inputScrollPane.getBorder()));
		inputScrollPane.setMinimumSize(new Dimension(10,10));
		
		//
		JScrollPane outputScrollPane = new JScrollPane(panelOut);
		outputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		outputScrollPane.setPreferredSize(new Dimension(250,250));
		outputScrollPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Output Text"), 
				BorderFactory.createEmptyBorder(5,5,5,5)), outputScrollPane.getBorder()));
		outputScrollPane.setMinimumSize(new Dimension(10,10));
		
		//
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScrollPane, outputScrollPane);
		splitPane.setResizeWeight(0.5);
		splitPane.setTopComponent(inputScrollPane);
		splitPane.setBottomComponent(outputScrollPane);
		
		add(splitPane);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equalsIgnoreCase("Open File")) {
			//try {
				open();
			//} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			//} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
						
		} else if (e.getActionCommand().equalsIgnoreCase("Save")) {
			save();
		} else if (e.getActionCommand().equalsIgnoreCase("Save as...")) {
			saveasFile();
		} else if (e.getActionCommand().equalsIgnoreCase("Record Errors")) {
			recordErrors();
		}
	
		
	}

	
	private void open() {//throws IOException {
		// resets text so previous file and errors aren't displayed 
		text1.setText(null);
		text2.setText(null);
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		
		int result = fileChooser.showOpenDialog(null);
		
		//If cancel action is detected, this will return and stop open file
		if(result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		File selectedFile = fileChooser.getSelectedFile();
		String filename = selectedFile.getAbsolutePath();
		try{	    
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String currentLine; //current line that the buffered reader is looking at
		    String command = "";
		    String commandDetail = ""; //This variable is for commands that expand beyond 2 characters and require a specifier to specify amount or whether to toggle off or on.
		    int counter = 0; //used to go to newline after every 80 characters.
		    int specifiedNum = 0;
		    
		    //sets the parameters need to change alignment in the read lines
		    StyledDocument doc = text2.getStyledDocument();
		    SimpleAttributeSet left = new SimpleAttributeSet();
	        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);

	        SimpleAttributeSet center = new SimpleAttributeSet();
	        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
	        
	        SimpleAttributeSet right = new SimpleAttributeSet();
	        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
	        
	        //defaults alignment to left
	        SimpleAttributeSet tAlignment = left;
		    
		      while((currentLine = br.readLine()) != null) //continues reading text file until there is nothing left to be read
		      {
		        if(currentLine.indexOf("-") == 0 && currentLine.length() > 1)//checks if the line being read is a command
		        {
		          command = currentLine.substring(0,2);
		          commandDetail = currentLine.substring(2,currentLine.length());
		          
		          switch(command) //Dictates based on command read which variables to change by calling functions to change variables
		          {
		          //Parses user input for numerical value to pass as a new line length, includes
		          //error handling and will return -1 if the input is invalid.
		           case("-n"): 
		        	   try{
		        		   specifiedNum = Integer.parseInt(commandDetail);
		        	   }
		           		catch(NumberFormatException e)
		           		{
		           			specifiedNum = -1;
		           		};
		           		changeLineLength(specifiedNum);
		             break;
		             
		           case("-l"): //aligns all text after the command to the left side
		        	   
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   tAlignment = left;
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-l' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-c"): //aligns all text after the command to the center
		        	   
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   tAlignment = center;
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-c' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-r"): //aligns all text after the command to the right side
		        	   
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   tAlignment = right;
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-r' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-e"): //equally spaces all words in the line
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   toggleEqual();
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-e' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-w"): //toggles wrap
		        	   
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 3)
		        	   {
		        		   toggleWrap(commandDetail);
		        	   }
		        	   else if(currentLine.length() == 2)
		        	   {
		        		   recordErrors("Error: Expected input after '-w+' or '-w-' command, additional input is necessary");
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-w+' or '-w-' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-s"): //single spacing
		        	   
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   changeSpace(0);
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-s' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-d"): //double spacing
		        	   
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   changeSpace(1);
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-d' command, additional input is not necessary");
		        	   }
		             break;
		             
		           case("-t"): //text after this command becomes a title by printing the text on one line and then printing hyphens underneath the text on a second line
		        	   //Error handling for when additional input values are entered after commands that don't need it
		        	   if(currentLine.length() == 2)
		        	   {
		        		   toggleTitle();
		        	   }
		        	   else
		        	   {
		        		   recordErrors("Error: Unexpected input after '-t' command, additional input is not necessary");
		        	   }
		        	   break;
		        	   
		           //Parses user input for numerical value to pass a number of spaces to indent, includes
				   //error handling and will return -1 if the input is invalid.
		           case("-p"): //adds indentation
		        	   try{
		        		   specifiedNum = Integer.parseInt(commandDetail);
		        	   }
		           		catch(NumberFormatException e)
		           		{
		           			specifiedNum = -1;
		           		};
		           		changeParagraph(specifiedNum);
		             break;
		             
		           //Parses user input for numerical value to pass a number of blank lines to add, includes
				   //error handling and will return -1 if the input is invalid.
		           case("-b"): //adds new blank line
		        	   try{
		        		   specifiedNum = Integer.parseInt(commandDetail);
		        	   }
		           		catch(NumberFormatException e)
		           		{
		           			specifiedNum = -1;
		           		};
		           		changeBlankLines(specifiedNum);
		             break;
		             
		           //Parses user input for numerical value to pass a number of columns, includes
				   //error handling and will return -1 if the input is invalid.
		           case("-a"): //changes amount of columns of text (similar to how a textbook may have their text formatted)
		        	   try{
		        		   specifiedNum = Integer.parseInt(commandDetail);
		        	   }
		           		catch(NumberFormatException e)
		           		{
		           			specifiedNum = -1;
		           		};
		           		changeColumns(specifiedNum);
		             break;
		             
		           default:
		        	   recordErrors("Error: Input after '-' is not a valid command.");
		          }
		        }
		        
		        //This else if statement is error handling for when a hyphen is given as an input alone.
		        else if(currentLine.indexOf("-") == 0 && currentLine.length() == 1)
		        {
		        	recordErrors("Error: Invalid command, expected additional input after '-'.");
		        }
		        
		        //The only thing that isn't a command is text, any text on currentLine should be printed on the display after being formatted
		        else
		        {
		        	//starts a new line if the current one goes over 75 characters (so that it can finish writing whatever word its on) 
		        	for(int i = 0; i < currentLine.length(); i++) {
		        		counter++;
		        		if(counter >= 75 && i+1 != currentLine.length() && currentLine.substring(i+1, i+2).equals(" ")) {
		        			currentLine = currentLine.substring(0, i+1) + "\n" + currentLine.substring(i+2,currentLine.length());
		        			counter = 0;
		        		}
		        	}
		        	
		        	//starts a new line at the end of each sentence.
		        	currentLine = currentLine + "\n";
		        	counter = 0;
		    
		        	doc.setParagraphAttributes(doc.getLength(), 1, tAlignment, false);
		        	doc.insertString(doc.getLength(), currentLine, tAlignment);
		        	
		        	//adds a space if there is an empty line read in the text file
		        	if(currentLine.equals("")) {
		        		doc.insertString(doc.getLength(), "\n", null);
		        	}
		        }
		      }
		    br.close();
			text2.requestFocus();
			
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e);
		}
	}
				
	private void save() {
		//Returns
		if(selectedFile == null)
		{
			recordErrors("Error: Unable to save, pre-existing save file must be created.");
			return;
		}
		
		//Adds .txt to file if it doesn't already have it
		if(!selectedFile.getName().endsWith(".txt"))
		{
			selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
		}
		PrintWriter sWriter = null;
		
		//Saves all text from text2 into the file desired.
		try {
			sWriter = new PrintWriter(new FileWriter(selectedFile));
			sWriter.write(text2.getText());	//******This will definitely need to be changed later, this doesn't print with formatting******
			sWriter.close();
		}	catch (IOException error) {
			recordErrors("Error: Unable to save file.");
			error.printStackTrace();	//Prints where the error occurred and what type of error in console
		}
	}	

	private void saveasFile() {
		//Only allows user to select existing text files or to make a new text file by inputting a name
		FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Text File", "txt");
		
		//Upon pressing save as, user is directed to the file path of the program
		JFileChooser fc = new JFileChooser("./");
		fc.setApproveButtonText("Save");
		fc.setFileFilter(extensionFilter);
		
		//Detects whether or not the user pressed cancel and ensures program doesn't 
		//continue to attempt to save by returning when a cancel action is detected
		int action = fc.showOpenDialog(this);
		
		if (action != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		//Grabs all the necessary information to save and ensures that the output
		//file will always be a text file.
		selectedFile = fc.getSelectedFile();
		if(!selectedFile.getName().endsWith(".txt"))
		{
			selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
		}
		PrintWriter saWriter = null;
		
		//Saves all text from text2 into the file desired.
		try {
			saWriter = new PrintWriter(new FileWriter(selectedFile));
			saWriter.write(text2.getText());	//******This will definitely need to be changed later, this doesn't print with formatting******
			saWriter.close();
		}	catch (IOException error) {
			recordErrors("Error: Unable to save file.");
			error.printStackTrace();	//Prints where the error occurred and what type of error in console
		}
	}	
	
	private void recordErrors() {
		//Only allows user to select existing text files or to make a new text file by inputting a name
		FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("Text File", "txt");
		
		//Upon pressing save as, user is directed to the file path of the program
		JFileChooser fc = new JFileChooser("./");
		fc.setApproveButtonText("Save");
		fc.setFileFilter(extensionFilter);
		
		//Detects whether or not the user pressed cancel and ensures program doesn't 
		//continue to attempt to save by returning when a cancel action is detected
		int action = fc.showOpenDialog(this);
		
		if (action != JFileChooser.APPROVE_OPTION)
		{
			return;
		}
		
		//Grabs all the necessary information to save and ensures that the output
		//file will always be a text file.
		File errorFile = fc.getSelectedFile();
		if(!errorFile.getName().endsWith(".txt"))
		{
			errorFile = new File(errorFile.getAbsolutePath() + ".txt");
		}
		PrintWriter errWriter = null;
		
		//Saves all text from text2 into the file desired.
		try {
			errWriter = new PrintWriter(new FileWriter(errorFile));
			errWriter.write(text1.getText());
			errWriter.close();
		}	catch (IOException error) {
			recordErrors("Error: Unable to record errors to file.");
			error.printStackTrace();	//Prints where the error occurred and what type of error in console
		}
	}	
	
	//Displays errors to the GUI for user to read
	public void recordErrors(String errorMsg) {
		text1.setText(text1.getText() + errorMsg + "\n");
	}
	
	//All functions below here change the variables that are declared above
	//These functions also feature with error handling
	public void changeLineLength(int newLength)
	{
		if(newLength == -1)
		{
			recordErrors("Error: Input after '-n' command is not an integer.");
		}
		else
		{
			lineLength = newLength;
		}
	}
	
	public void toggleEqual()
	{
		if(equalSpace == false)
		{
			equalSpace = true;
		}
		else
		{
			equalSpace = false;
		}
	}
	
	public void toggleWrap(String toggler)
	{
		if(toggler.equalsIgnoreCase("+"))
		{
			wrap = true;
		}
		else if(toggler.equalsIgnoreCase("-"))
		{
			wrap = false;
		}
		else
		{
			recordErrors("Error: Input after '-w' is neither a '+' or '-' symbol.");
		}
	}
	
	public void changeSpace(int spacing)
	{
		space = spacing;
	}
	
	public void toggleTitle()
	{
		if(title == false)
		{
			title = true;
		}
		else
		{
			title = false;
		}
	}
	
	public void changeParagraph(int indent)
	{
		if(indent == -1)
		{
			recordErrors("Error: Input after '-p' is not an integer.");
		}
		else
		{
			paragraphIndent = indent;
		}
	}
	
	public void changeBlankLines(int numberOfBlanks)
	{
		if(numberOfBlanks == -1)
		{
			recordErrors("Error: Input after '-b' is not an integer.");
		}
		else
		{
			blankLines = numberOfBlanks;
		}
	}
	
	public void changeColumns(int newColumnNumber)
	{
		if(newColumnNumber == -1)
		{
			recordErrors("Error: Input after '-a' is not an integer.");
		}
		else if(newColumnNumber < 1 || newColumnNumber > 2)
		{
			recordErrors("Error: Input after '-a' is not a valid input, you are only allowed to have 1 or 2 columns.");
		}
		else
		{
			columnNumber = newColumnNumber;
		}
	}
}