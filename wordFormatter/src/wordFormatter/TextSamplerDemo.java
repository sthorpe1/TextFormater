package wordFormatter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.JComponent; 
import javax.swing.*;


public class TextSamplerDemo extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	
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
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputScrollPane, outputScrollPane);
		splitPane.setResizeWeight(0.5);
		splitPane.setTopComponent(inputScrollPane);
		splitPane.setBottomComponent(outputScrollPane);
		
		add(splitPane);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getActionCommand().equalsIgnoreCase("Open File")) {
			try {
				open();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} else if (e.getActionCommand().equalsIgnoreCase("Save")) {
			save();
		} else if (e.getActionCommand().equalsIgnoreCase("Save as")) {
			saveasFile();
		}
	
		
	}
	
	private void open() throws FileNotFoundException {
		
		JFileChooser fileChooser = new JFileChooser();
		StringBuilder sb = new StringBuilder();
		
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(this);
		
		if (result == JFileChooser.APPROVE_OPTION) {
		    File selectedFile = fileChooser.getSelectedFile();
		    
				Scanner input = new Scanner(selectedFile);
				text1.setText(selectedFile.getAbsolutePath());
				while(input.hasNext()){
					
					sb.append(input.nextLine());
					sb.append("\n");
				}
				input.close();
			
					
		} else
		{
			sb.append("No file was selected");
		}
		
		
	}
				
	private void save() {
		
	}
	private void saveasFile() {
	
	}	
}