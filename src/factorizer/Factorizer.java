package factorizer;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
/**
 * Class with various methods regarding factoring integers. 
 * @author Ryan Schwegel
 * @version 1.2
 */
public class Factorizer {
	
	/**
	 * Returns an Integer[] of the factors of param n.
	 * Precondition: 0 < n < Max integer value
	 * @param n number to get factors of
	 * @return Integer[] containing all factors of number n
	 */
	public static Integer[] factors(int n) {
		ArrayList<Integer> factors = new ArrayList<Integer>(); //local ArrayList
		
		//Factoring computation
		for(int i = 1; i <= Math.sqrt(n); i++){
			if(n%i == 0) {
				factors.add(i);
				if(n/i != i) //Check for opposing factor
					factors.add(n/i);
			}
		}
		
		Collections.sort(factors); //sort list
		return factors.toArray(new Integer[0]); //output with Integer[]
	}
	
	/**
	 * Precondition: 0 < n < Max integer value
	 * @param n number to see if is prime
	 * @return true if n is prime, false otherwise
	 */
	public static boolean isPrime(int n) {
		for(int i = 2; i <= Math.sqrt(n); i++) {
			if(n%i == 0) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Determines whether integers are prime or nonprime in range 1 < x <= num, and either adds x to a list of primes or x and its factors to a map of nonprimes to their factors. 
	 * Utilizes an unthreaded implementation, running through each integer on a single thread. 
	 * @param nonprimes Map of nonprimes to an array of its factors for n to be added to
	 * @param primes list of prime numbers for n to be added to
	 * @param n positive integer to be factored up to
	 */
	public static void unthreadedPrimes(Map<Integer, Integer[]> nonprimes, List<Integer> primes, int num) {
		Integer[] factors;
		for(int i = 2; i<=num; i++) {
			factors = Factorizer.factors(i);
			if(factors.length == 2)
				primes.add(i);
			else
				nonprimes.put(i, factors);
		}
	}
	
	/**
	 * Determines whether integers are prime or nonprime in range 1 < x <= num, and either adds x to a list of primes or x and its factors to a map of nonprimes to their factors. 
	 * Utilizes a naive threaded implementation where a thread is created for each integer to factorize, meaning this method is incredibly inefficient. 
	 * @param nonprimes Map of nonprimes to an array of its factors for n to be added to, must be thread safe.
	 * @param primes list of prime numbers for n to be added to, must be thread safe.
	 * @param n positive integer to be factored up to
	 */
	public static void unboundedThreadedPrimes(Map<Integer, Integer[]> nonprimes, List<Integer> primes, int num) {
		for (int i = 2; i<=num; i++) {
			new FactorThread(nonprimes, primes, i).start();
		}
	}
	
	/**
	 * Determines whether integers are prime or nonprime in range 1 < x <= num, and either adds x to a list of primes or x and its factors to a map of nonprimes to their factors.
	 * Utilizes a threaded implementation using a bounded thread pool.
	 * @param nonprimes Map of nonprimes to an array of its factors for n to be added to, must be thread safe.
	 * @param primes list of prime numbers for n to be added to, must be thread safe.
	 * @param num positive integer to be factored up to
	 * @param threads Capacity of thread pool. Value must be greater than 0.
	 */
	public static void threadPoolPrimes(Map<Integer, Integer[]> nonprimes, List<Integer> primes, int num, int threads) {
		//Local variables
		ExecutorService executorService = Executors.newFixedThreadPool(threads);
		CompletionService<Map.Entry<Integer, Integer[]>> completionService = new ExecutorCompletionService<Map.Entry<Integer, Integer[]>>(executorService);
		
		//Create list of all numbers, needed to pass nearly final integer into completion service
		ArrayList<Integer> nums = new ArrayList<Integer>(num-1);
		for(int i = 2; i<=num; i++) {
			nums.add(i);
		}
		
		//Submit work
		for(int n : nums) {
			completionService.submit(()->new AbstractMap.SimpleEntry<Integer,Integer[]>(n, Factorizer.factors(n)));
		}
		
		//take and get exception handling
		try {
			for(int n = 0; n < nums.size(); n++) {
				//acquire entry
				Future<Map.Entry<Integer, Integer[]>> f = completionService.take();
				Map.Entry<Integer, Integer[]> e = f.get();
				
				//add to appropriate collection
				if (e.getValue().length == 2) {
					primes.add(e.getKey());
				} else {
					nonprimes.put(e.getKey(), e.getValue());
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			System.err.println(e.getCause());
		}
		//shutdown thread service
		executorService.shutdown();
	}
	
	/**
	 * Determines whether integers are prime or nonprime in range 1 < x <= num, and either adds x to a list of primes or x and its factors to a map of nonprimes to their factors.
	 * Utilizes a threaded implementation using a bounded thread pool. Default implementation using number of cores + 1 for thread count. 
	 * @param nonprimes Map of nonprimes to an array of its factors for n to be added to, must be thread safe.
	 * @param primes list of prime numbers for n to be added to, must be thread safe.
	 * @param num positive integer to be factored up to 
	 */
	public static void threadPoolPrimes(Map<Integer, Integer[]> nonprimes, List<Integer> primes, int num) {
		threadPoolPrimes(nonprimes, primes, num, Runtime.getRuntime().availableProcessors() + 1);
	}
	
	/**
	 * Determines whether integers are prime or nonprime in range 1 < x <= num, and either adds x to a list of primes or x and its factors to a map of nonprimes to their factors.
	 * Utilizes a threaded implementation using streams. 
	 * @param nonprimes Map of nonprimes to an array of its factors for n to be added to, must be thread safe.
	 * @param primes list of prime numbers for n to be added to, must be thread safe.
	 * @param num positive integer to be factored up to
	 */
	public static void streamPrimes(Map<Integer, Integer[]> nonprimes, List<Integer> primes, int num) {
		IntStream.rangeClosed(2, num).parallel().forEach((int i)->{
			//perform factoring
			Integer[] facts = Factorizer.factors(i);
			if(facts.length == 2) {
				primes.add(i);
			} else
				nonprimes.put(i, facts);
		});
	}
}
