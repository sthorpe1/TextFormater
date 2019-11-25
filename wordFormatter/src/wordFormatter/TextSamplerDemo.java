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
		inputScrollPane.setPreferredSize(new Dimension(250,250));
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
			
			text1.read(br, null);////////////////////////////////////////add switch case here
			br.close();
			text1.requestFocus();///////////////////////////////////////add how to use variables to change text formatting
			
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
	//All functions below here change the variables that are declared above
	public void changeLineLength(int newLength) //Error handling may still need to be added to these functions
	{											//For example, column function will need to revert back to 1 if
		lineLength = newLength;					//a column number that isn't 1 or 2 is inputed by user
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
		else
		{
			wrap = false;
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
			title= true;
		}
		else
		{
			title = false;
		}
	}
	public void changeParagraph(int indent)
	{
		paragraphIndent = indent;
	}
	public void changeBlankLines(int numberOfBlanks)
	{
		blankLines = numberOfBlanks;
	}
	public void changeColumns(int newColumnNumber)
	{
		columnNumber = newColumnNumber;
	}
}