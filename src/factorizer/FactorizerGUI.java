package factorizer;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.*;

/**
 * Class providing a responsive GUI for calculating prime numbers up to a certain number sequentially,
 * utilizing the Factorizer class. Users provide a starting and ending number for a prime calculation and
 * receive a count of the primes in the selected range. 
 * @author Ryan Schwegel
 */
public class FactorizerGUI {
	public static void main(String[] args) {
		//Local variables
		List<Integer> primes = new Vector<Integer>();
		Map<Integer, Integer[]> nonprimes = new ConcurrentHashMap<Integer, Integer[]>();
		//GUI Elements
		JFrame fr = new JFrame();
		fr.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		fr.setTitle("Prime Counter"); //primary swing frame
		
		JLabel primesLabel = new JLabel("Primes: 0"); //primes label
		
		JButton cancelButton = new JButton("Cancel");
		JButton submitButton = new JButton("Submit"); //buttons
		
		JLabel startLabel = new JLabel("Start #");
		JLabel endLabel = new JLabel("End #");
		JTextField startTextField = new JTextField("", 10);
		JTextField endTextField = new JTextField("", 10); //start and end input fields and labels
		
		//Submit button action
		submitButton.addActionListener(e -> {
			primesLabel.setForeground(new Color(51,51,51)); //Reset label color if error was displayed
			//Error checking and collection of input
			if(startTextField.getText().equals("") || endTextField.getText().equals("")) {
				primesLabel.setText("Enter values");
				return;
			}
			
			int start = Integer.parseInt(startTextField.getText()); 
			int end = Integer.parseInt(endTextField.getText()); //collect user input
			
			if(start >= end || start < 0 || end < 0) { //Check for invalid range
				primesLabel.setText("Invalid range");
				primesLabel.setForeground(Color.red);
				return;
			}
			//Input confirmed, begin factoring
			submitButton.setEnabled(false); //disable button, re-enabled inside worker done() method
			//clear collections from previous run
			primes.clear();
			nonprimes.clear();
			FactorWorker worker = new FactorWorker(start, end, nonprimes, primes, primesLabel, submitButton);
			
			//Add cancel listener for new worker on cancel button
			cancelButton.addActionListener(ev -> {
				worker.cancel(true);
			});

			worker.execute(); //run worker
		});
		
		submitButton.setBounds(130, 100, 100, 40);
		cancelButton.setBounds(180, 100, 100, 40); //define button widths and heights
		//add elements
		fr.add(startLabel);
		fr.add(startTextField);
		fr.add(endLabel);
		fr.add(endTextField);
		fr.add(submitButton);
		fr.add(primesLabel);
		fr.add(cancelButton);
		
		fr.setLayout(new FlowLayout());
		fr.setSize(350,120);
		fr.setVisible(true); //frame configuration
	}
}
