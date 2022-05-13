package factorizer;

import java.util.List;
import java.util.Map;

/**
 * Thread class for running a factoring calculation on an integer, and adding that integer to either a Map of nonprimes to factors, or a List of primes. 
 * @author Ryan
 * @version 1.0
 */
public class FactorThread extends Thread{
	private Map<Integer, Integer[]> nonprimes;
	private List<Integer> primes;
	private int num;
	/**
	 * Constructor for FactorThread
	 * @param nonprimes Map of nonprimes to an array of its factors for n to be added to
	 * @param primes list of prime numbers for n to be added to
	 * @param n integer to be factored
	 */
	FactorThread(Map<Integer, Integer[]> nonprimes, List<Integer> primes, int n){
		this.nonprimes = nonprimes;
		this.primes = primes;
		num = n;
	}
	
	/**
	 * Runs the factorization and adds to corresponding collection
	 */
	public void run() {
		Integer[] factors = Factorizer.factors(num);
		if(factors.length == 2) {
			primes.add(num);
		} else {
			nonprimes.put(num, factors);
		}
	}
}
