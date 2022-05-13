package factorizer;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
/**
 * Driver for Factorizer class. Takes user input of a positive integer, calculates primes up to that number utilizing method based off user input, and logs runtime.  
 * @author Ryan Schwegel
 * @version 1.1
 */
public class FactorizerDriver {
	
	public static void main(String[] args) {
		//Local variables
		Scanner scan = new Scanner(System.in);
		List<Integer> primes = new Vector<Integer>();
		Map<Integer, Integer[]> nonprimes = new ConcurrentHashMap<Integer, Integer[]>();
		int input = 1, num = 0, threads = 0;
		
		//main loop
		while(true) {
			//Reset collections
			primes.clear();
			nonprimes.clear();
			
			//Collect user input
			System.out.print("Please enter number to calculate primes up to (-1 to exit): ");
			num = scan.nextInt();
			//check for exit
			if(num == -1) {
				//close scanner
				scan.close();
				return;
			}
			System.out.print("Enter execution mode (1 - unthreaded, 2 - threaded, unbounded, 3 - Bounded Threadpool, 4 - Streams): ");
			input = scan.nextInt();
			
			long start = System.currentTimeMillis(); //log time
			//perform user action
			if(input == 1) {
				Factorizer.unthreadedPrimes(nonprimes, primes, num);
			} else if (input == 2) {
				Factorizer.unboundedThreadedPrimes(nonprimes, primes, num);
			} else if (input == 3) {
				System.out.print("Enter thread pool capacity (0 for number of cores + 1): ");
				threads = scan.nextInt();
				if(threads==0)
					Factorizer.threadPoolPrimes(nonprimes, primes, num);
				else
					Factorizer.threadPoolPrimes(nonprimes, primes, num, threads);
			} else if (input == 4) {
				Factorizer.streamPrimes(nonprimes, primes, num);
			}
			long end = System.currentTimeMillis(); //log time
			System.out.println("Runtime: " + (end-start) + "ms"); //print runtime
			System.out.println("Prime list size: " + primes.size());
			System.out.println("Nonprime list size: " + nonprimes.keySet().size()); 
		}
	}
}
