package wordFormatter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;


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
	
	JPanel panelIn;
	JPanel panelOut;
	JTextArea text1;
	JTextArea text2;
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
		
		//implementing ActionListener to all the item in menu
		JMenuItem[] items = {newItem, save, open, saveasFile};
		for(JMenuItem item : items){
			item.addActionListener(this);
		}
			
		newItem.add(open);	
		file.add(newItem);
		file.addSeparator();
		file.add(save);
		file.add(saveasFile);
		bar.add(file);
		
		setJMenuBar(bar);
	
		//
		text1 = new JTextArea();
		text2 = new JTextArea();
	
		
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
		} else if (e.getActionCommand().equalsIgnoreCase("Save as")) {
			saveasFile();
		}
	
		
	}

	
	private void open() {//throws IOException {
		
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		File selectedFile = fileChooser.getSelectedFile();
		String filename = selectedFile.getAbsolutePath();
		try{	    
			BufferedReader br = new BufferedReader(new FileReader(filename));
			
			String currentLine; //current line that the buffered reader is looking at
		    String command = "";
		    String commandDetail = ""; //This variable is for commands that expand beyond 2 characters and require a specifier to specificy amount or whether to toggle off or on.
		    int specifiedNum = 0;
		    
		      while((currentLine = br.readLine()) != null) //continues reading text file until there is nothing left to be read
		      {
		        if(currentLine.indexOf("-") == 0)//checks if the line being read is a command
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
		        	   changeAlignment(0);
		             break;
		             
		           case("-c"): //aligns all text after the command to the center
		        	   changeAlignment(1);
		             break;
		             
		           case("-r"): //aligns all text after the command to the right side
		        	   changeAlignment(2);
		             break;
		             
		           case("-e"): //equally spaces all words in the line
		        	   toggleEqual();
		             break;
		             
		           case("-w"): //toggles wrap
		        	   toggleWrap(commandDetail);
		             break;
		             
		           case("-s"): //single spacing
		        	   changeSpace(0);
		             break;
		             
		           case("-d"): //double spacing
		        	   changeSpace(1);
		             break;
		             
		           case("-t"): //text after this command becomes a title by printing the text on one line and then printing hyphens underneath the text on a second line
		              toggleTitle();
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
		        else //The only thing that isn't a command is text, any text on this line should be printed on the display after being formatted
		        {
		          //Put code to format and display the text in here, use variables to dictate how it should be formatted
		        }
		      }
			br.close();
			text1.requestFocus();
			
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(null, e);
		}
		
	}
				
	private void save() {
		
	}
	private void saveasFile() {
	
	}	
	
	//Displays errors to the GUI for user to read
	public void recordErrors(String errorMsg) {
		text1.setText(null);
		text1.append(errorMsg);
		//There's no area for displaying errors right now, so I'm printing to 2nd area text box for now.
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
	
	public void changeAlignment(int newAlign)
	{
		alignment = newAlign;
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
		if(toggler == "+")
		{
			wrap = true;
		}
		else if(toggler == "-")
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