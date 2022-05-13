# ConcurrentFactorizer
Factorizing Java GUI, driver, and methods

Developed over the course of Spring 2022 as part of Concurrent Programming taught by Patrick McKee

The GUI is used to calculate the number of primes in a range between start and end integers selected by the user. Access it by running the main method inside FactorizerGUI.java

The driver can be used to calculate a range of primes 2-n and provide stats on the runtime, number of primes, and number of nonprimes. Users are prompted to select a mode of operation, which either utilizes a sequential solution, an unbounded threaded solution, a bounded threadpool, or streams to complete the factoring operations. This allows the user to see which solutions perform the fastest under different conditions (larger numbers/ranges vs. smaller ones).
