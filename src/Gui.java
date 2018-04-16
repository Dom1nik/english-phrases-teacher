import javax.swing.*;

import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTabbedPaneUI.TabSelectionHandler;

import org.omg.CORBA.Current;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture.AsynchronousCompletionTask;

public class Gui {
	ArrayList<Phrase> phraseList = new ArrayList<Phrase>();
	JTextField questionPhrase;
	JTextField answerPhrase;
	JLabel playerScore;
	Phrase currentPhrase;
	String[] currentPhraseWords;
	short correctAnswers;
	short wrongAnswers;
	
	public static void main(String[] args) {
		new Gui().buildMainGui();
	}
	
	public void savePhraseObject(Object obj) {
		try {
			FileOutputStream fileStream = new FileOutputStream("phrases_objects.ser");
			ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
			objectStream.writeObject(obj);
			objectStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buildMainGui() {
		JFrame frame = new JFrame("Phrase Teacher");
		JPanel panel = new JPanel();		

		Box phraseDisplay = new Box(BoxLayout.Y_AXIS);
		Box taskControl = new Box(BoxLayout.Y_AXIS);
		
		JButton options = new JButton("Options".toUpperCase());
		options.addActionListener(new optionsButtonListener());

		questionPhrase = new JTextField();
		answerPhrase = new JTextField();
		phraseDisplay.add(questionPhrase);
		phraseDisplay.add(answerPhrase);
		answerPhrase.addActionListener(new answerPhraseTextFieldListener());
		
		JButton randomPhraseGetter = new JButton("Give me phrase!".toUpperCase());
		randomPhraseGetter.addActionListener(new randomPhraseGetterButtonListener());
		taskControl.add(randomPhraseGetter);
		JButton hintPhraseWord= new JButton("Hint".toUpperCase());
		hintPhraseWord.addActionListener(new hintPhraseWordButtonListener());
		taskControl.add(hintPhraseWord);
		playerScore = new JLabel("Wrong 0 :: Correct 0".toUpperCase());
		taskControl.add(playerScore);
		
		panel.setLayout(new BorderLayout());
		//panel.add(BorderLayout.WEST, phraseSetup);
		panel.add(BorderLayout.WEST, options);
		panel.add(BorderLayout.SOUTH, taskControl);
		panel.add(BorderLayout.CENTER, phraseDisplay);
		
		frame.getContentPane().add(panel);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}

	public class optionsButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			//new OptionsFrame().buildGui();
			new Options().addGui();
		}
	}
	
	public class  answerPhraseTextFieldListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (answerPhrase.getText().equals(currentPhrase.getEnglishTranslation())) {
				System.out.println("Correct, well done ;)");
				correctAnswers++;
			} else {
				System.out.println("Oops, something's not right :(");
				wrongAnswers++;
			}
			playerScore.setText(("Wrong " + wrongAnswers + " :: " + "Correct " + correctAnswers).toUpperCase());
		}
	}
	
	public class randomPhraseGetterButtonListener implements ActionListener {		
		public void actionPerformed(ActionEvent e) {
			if (phraseList.isEmpty()) {
				System.out.println("There are no saved phrases yet!");
			}
			else {
				currentPhrase = phraseList.get((int)(Math.random() * phraseList.size()));
				questionPhrase.setText(currentPhrase.getCroatianTranslation());	
				currentPhraseWords = currentPhrase.getEnglishTranslation().split(" ");
			}
		}
	}
	
	public class hintPhraseWordButtonListener implements ActionListener {		
		public void actionPerformed(ActionEvent e) {
			System.out.println("hint button pressed!");
			String currentPhraseQuestion = currentPhrase.getEnglishTranslation();			
			String answerPhraseTextFieldContent = answerPhrase.getText();
			String[] answerPhraseTextFieldWords = answerPhraseTextFieldContent.split(" ");
			String[] answerPhraseFullAnswerWords = currentPhrase.getEnglishTranslation().split(" ");
			
			if (answerPhraseTextFieldContent.isEmpty()) {
				answerPhrase.setText(answerPhraseFullAnswerWords[0]);
			} else if (answerPhraseTextFieldWords.length == answerPhraseFullAnswerWords.length) {
				System.out.println("No more hints, you are done!");
			} else {
				String lastWordInCurrentPhrase = answerPhraseTextFieldWords[answerPhraseTextFieldWords.length-1];
				answerPhrase.setText(answerPhraseTextFieldContent + " " + currentPhraseWords[Arrays.asList(currentPhraseWords).indexOf(lastWordInCurrentPhrase)+1]);
			}
		}
	}
	
	class Options {
		Map phraseTranslations = new HashMap();
		JTextField newPhraseCroatianTextField;
		JTextField newPhraseEnglishTextField;
		JTextField editPhraseCroatianTextField;
		JTextField editPhraseEnglishTextField;
		JComboBox removePhraseNameDropDown;
		JComboBox editPhraseNameDropDown;
		DefaultComboBoxModel model;
		ArrayList<String> phraseNamesList = new ArrayList<String>(); 
		
		public ArrayList<String> iteratePhraseNames() {
			ArrayList<String> phraseNamesList = new ArrayList<String>(); 
			for (Phrase obj : phraseList) {
	        	phraseNamesList.add(obj.getEnglishTranslation());
	        	phraseTranslations.put(obj.getEnglishTranslation(), obj.getCroatianTranslation());
			}
			
			return phraseNamesList;
		}
		
		public void addGui() {
			JTabbedPane pane = new JTabbedPane();
			JFrame frame = new JFrame("Options");
			JPanel panela = new JPanel();
			JPanel panela1 = new JPanel();
			JPanel panela2 = new JPanel();
			panela.setLayout(new BoxLayout(panela, BoxLayout.Y_AXIS));
			panela1.setLayout(new BoxLayout(panela1, BoxLayout.Y_AXIS));
			panela2.setLayout(new BoxLayout(panela2, BoxLayout.Y_AXIS));
			newPhraseCroatianTextField = new JTextField();
			newPhraseCroatianTextField.addKeyListener(new addPhraseCroatianTextFieldListener());
			//newPhraseCroatianTextField.addActionListener(new addPhraseCroatianTextFieldListener());
			panela.add(new JLabel("Enter Croatian phrase"));
			panela.add(newPhraseCroatianTextField);			
			
			newPhraseEnglishTextField = new JTextField();
			//newPhraseEnglishTextField.addActionListener(new addPhraseEnglishTextFieldListener());
			panela.add(new JLabel("Enter English phrase"));			
			panela.add(newPhraseEnglishTextField);			
			
			JButton addPhraseButton = new JButton("Add phrase".toUpperCase());
			addPhraseButton.addActionListener(new addPhraseButtonListener());
			panela.add(addPhraseButton);
			pane.addTab("Add new phrase", panela);
				        			
			editPhraseNameDropDown = new JComboBox();
		    removePhraseNameDropDown = new JComboBox();
	        editPhraseNameDropDown.addItemListener(new EditPhraseDropDownListener());
	        
			editPhraseCroatianTextField = new JTextField();
			editPhraseEnglishTextField = new JTextField();

		    if (phraseList.isEmpty()) {
		        	System.out.println("No phrases added yet!");
			        model = new DefaultComboBoxModel<>(new String[] {"- None -"});

		        } else {
			        phraseNamesList = iteratePhraseNames();
			        model = new DefaultComboBoxModel<>(phraseNamesList.toArray());
			        //editPhraseNameDropDown.addActionListener(new editPhraseJComboBoxListener());
			        //removePhraseNameDropDown.addActionListener(new removePhraseJComboBoxListener());
			        editPhraseCroatianTextField.setText(phraseNamesList.get(0));
			        editPhraseEnglishTextField.setText(phraseNamesList.get(0));
		        }
		    
		    editPhraseNameDropDown.setModel(model);
		    removePhraseNameDropDown.setModel(model);
			
		    panela1.add(new JLabel("Choose phrase to remove:"));
	        panela1.add(removePhraseNameDropDown);
			JButton removePhraseButton = new JButton("Remove phrase".toUpperCase());
			removePhraseButton.addActionListener(new removePhraseButtonListener());
			panela1.add(removePhraseButton);

			panela2.add(new JLabel("Choose phrase to edit:"));
			panela2.add(editPhraseNameDropDown);
			
			JButton editPhraseButton = new JButton("Edit phrase".toUpperCase());
			editPhraseButton.addActionListener(new editPhraseButtonListener());
			panela2.add(editPhraseEnglishTextField);
			panela2.add(editPhraseCroatianTextField);
			panela2.add(editPhraseButton);
			pane.addTab("Edit phrase", panela2);
			
			pane.addTab("Remove phrase", panela1);
			frame.getContentPane().add(pane);
			frame.setSize(300, 300);
			frame.setVisible(true);
		}

		public class addPhraseButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.out.println(newPhraseCroatianTextField.getText());
				System.out.println(newPhraseEnglishTextField.getText());
				phraseList.add(new Phrase(newPhraseCroatianTextField.getText(), newPhraseEnglishTextField.getText()));
				phraseNamesList = iteratePhraseNames();
				model = new DefaultComboBoxModel(phraseNamesList.toArray());
				removePhraseNameDropDown.setModel(model);
				editPhraseNameDropDown.setModel(model);
				newPhraseCroatianTextField.setText("");
				newPhraseEnglishTextField.setText("");
				editPhraseCroatianTextField.setText(phraseTranslations.get(editPhraseNameDropDown.getSelectedItem().toString()).toString());
				editPhraseEnglishTextField.setText(editPhraseNameDropDown.getSelectedItem().toString());
				savePhraseObject(phraseList);
			}
		}
		
		public class addPhraseEnglishTextFieldListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				
			}
		}
		
		public class addPhraseCroatianTextFieldListener implements KeyListener{

			public void keyTyped(KeyEvent evt) {  
				 //if(newPhraseCroatianTextField.getText().length()>=2){  
				 //	 evt.consume();
				 //}	
			}
			
			public void keyPressed(KeyEvent evt) {                       
			
			}
			
			public void keyReleased(KeyEvent evt) {                       
			
			}
		}
				
		public class removePhraseJComboBoxListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				
			}
		}
		
		public class editPhraseJComboBoxListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				
			}
		}
		
		public class removePhraseButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				if (!phraseList.isEmpty()) {
					System.out.println(removePhraseNameDropDown.getSelectedIndex());
					phraseList.remove(removePhraseNameDropDown.getSelectedIndex());
					savePhraseObject(phraseList);
					iteratePhraseNames();
					model = new DefaultComboBoxModel<>(phraseNamesList.toArray());
					removePhraseNameDropDown.setModel(model);
					editPhraseNameDropDown.setModel(model);
				}
				
				if (phraseList.isEmpty()){
			        model = new DefaultComboBoxModel<>(new String[] {"- None -"});
			        editPhraseNameDropDown.setModel(model);
			        removePhraseNameDropDown.setModel(model);
			        editPhraseCroatianTextField.setText("");
			        editPhraseEnglishTextField.setText("");
				}
			}
		}
		
		public class EditPhraseDropDownListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == 2) {
					editPhraseCroatianTextField.setText(phraseTranslations.get(editPhraseNameDropDown.getSelectedItem().toString()).toString());
					editPhraseEnglishTextField.setText(editPhraseNameDropDown.getSelectedItem().toString());
				}
			}
		}
		
		public class editPhraseButtonListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.out.println("edit button pressed!");
				Phrase phraseToEdit = phraseList.get(editPhraseNameDropDown.getSelectedIndex());
				System.out.println(phraseToEdit.getEnglishTranslation());
				phraseToEdit.setCroatianTranslation(editPhraseCroatianTextField.getText());
				phraseToEdit.setEnglishTranslation(editPhraseEnglishTextField.getText());
				phraseList.set(editPhraseNameDropDown.getSelectedIndex(), phraseToEdit);
				savePhraseObject(phraseList);
				model = new DefaultComboBoxModel<>(iteratePhraseNames().toArray());
				editPhraseNameDropDown.setModel(model);
				removePhraseNameDropDown.setModel(model);
			}
		}
	}
}

