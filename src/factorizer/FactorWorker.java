package factorizer;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.swing.*;
/**
 * SwingWorker for performing factoring operations.
 * @author Ryan Schwegel
 */
public class FactorWorker extends SwingWorker<Void, String> {
	
	private int start, end;
	private Map<Integer, Integer[]> nonprimes;
	private List<Integer> primes;
	private JLabel l;
	private JButton b;
	/**
	 * Constructor for FactorWorker
	 * @param start start of range of primes to be calculated, inclusive
	 * @param end end of range of primes to be calculated, inclusive
	 * @param nonprimes map where nonprimes will be mapped to their factors, must be thread safe
	 * @param primes list of integers where primes will be added, must be thread safe
	 * @param l label to be updated with prime count
	 * @param b button to be re-enabled once worker is done.
	 */
	FactorWorker(int start, int end, Map<Integer, Integer[]> nonprimes, List<Integer> primes, JLabel l, JButton b){
		super();
		this.start = start;
		this.end = end;
		this.nonprimes = nonprimes;
		this.primes = primes;
		this.l = l;
		this.b = b;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		l.setText("Primes: 0"); //set primes to 0, in case there are no primes in requested range.
		IntStream.rangeClosed(start, end).parallel().forEach((int i)->{
			//check for cancellation
			if(isCancelled())
				return;
			//perform factoring
			Integer[] facts = Factorizer.factors(i);
			//check for cancellation again, preventing label from updating after user has pressed cancel
			if(isCancelled())
				return;
			if(facts.length == 2) {
				primes.add(i);
				l.setText("Primes: " + primes.size()); //update primes label
			} else
				nonprimes.put(i, facts);
		});
		return null;
	}
	
	protected void done() {
		b.setEnabled(true); //re-enables submit button
	}
}
