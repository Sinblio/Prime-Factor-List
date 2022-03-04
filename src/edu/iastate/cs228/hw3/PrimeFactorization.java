package edu.iastate.cs228.hw3;

/**
 *  
 * @author Ben Schroeder
 *
 */

import java.util.ListIterator;

import edu.iastate.cs228.hw3.PrimeFactorization.PrimeFactorizationIterator;

public class PrimeFactorization implements Iterable<PrimeFactor>
{
	private static final long OVERFLOW = -1;
	private long value; 	// the factored integer 
							// it is set to OVERFLOW when the number is greater than 2^63-1, the
						    // largest number representable by the type long. 
	
	/**
	 * Reference to dummy node at the head.
	 */
	private Node head;
	  
	/**
	 * Reference to dummy node at the tail.
	 */
	private Node tail;
	
	private int size;     	// number of distinct prime factors


	// ------------
	// Constructors 
	// ------------
	
    /**
	 *  Default constructor constructs an empty list to represent the number 1.
	 *  
	 *  Combined with the add() method, it can be used to create a prime factorization.  
	 */
	public PrimeFactorization() 
	{	 
		value = 1;
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		size = 1;
	}

	
	/** 
	 * Obtains the prime factorization of n and creates a doubly linked list to store the result.   
	 * Follows the direct search factorization algorithm in Section 1.2 of the project description. 
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization(long n) throws IllegalArgumentException 
	{
		if (n < 1)
			throw new IllegalArgumentException("N is less than 1");
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		
		Node last = head;
		int prime = 2;
		int lastNum = 0;
		long left = n;
		
		while(!isPrime(left))
		{
			if (Math.floorMod(left, prime) == 0)
			{
				if (prime != lastNum)
				{
					link(last, new Node(prime, 1));
					size++;
					lastNum = prime;
					last = last.next;
				}
				else
				{
					last.pFactor.multiplicity++;
				}
			}
			else
			{
				if(prime == 2)
					prime ++;
				else
					prime += 2;
			}
		}
		if (lastNum != left)
		{
			link(last, new Node(prime, 1));
			size++;
		}
		else
		{
			last.pFactor.multiplicity++;
		}
		updateValue();
	}
	
	
	/**
	 * Copy constructor. It is unnecessary to verify the primality of the numbers in the list.
	 * 
	 * @param pf
	 */
	public PrimeFactorization(PrimeFactorization pf)
	{
		
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		
		PrimeFactorizationIterator pfit = pf.iterator();
		
		Node current = head;
		
		while(pfit.hasNext())
		{
			link(current, new Node(pfit.next()));
			current = current.next;
			size++;
		}
		updateValue();
	}
	
	/**
	 * Constructs a factorization from an array of prime factors.  Useful when the number is 
	 * too large to be represented even as a long integer. 
	 * 
	 * @param pflist
	 */
	public PrimeFactorization (PrimeFactor[] pfList)
	{
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		
		Node last = head;
		
		for(int j = 0; j < pfList.length; j++)
		{
			Node add = new Node(pfList[j]);
			this.link(last, add);
			last = add;
			size++;
		}
		updateValue();
	}
	
	

	// --------------
	// Primality Test
	// --------------
	
    /**
	 * Test if a number is a prime or not.  Check iteratively from 2 to the largest 
	 * integer not exceeding the square root of n to see if it divides n. 
	 * 
	 *@param n
	 *@return true if n is a prime 
	 * 		  false otherwise 
	 */
    public static boolean isPrime(long n) 
	{
	    int sqrt = (int)Math.sqrt(n);
	    
	    if (Math.floorMod(n, 2) == 0)
	    	return false;
	    
	    for(int j = 3; j <= sqrt; j += 2)
	    {
	    	if (Math.floorMod(n, j) == 0)
	    		return false;
	    }
		return true; 
	}   

   
	// ---------------------------
	// Multiplication and Division 
	// ---------------------------
	
	/**
	 * Multiplies the integer v represented by this object with another number n.  Note that v may 
	 * be too large (in which case this.value == OVERFLOW). You can do this in one loop: Factor n and 
	 * traverse the doubly linked list simultaneously. For details refer to Section 3.1 in the 
	 * project description. Store the prime factorization of the product. Update value and size. 
	 * 
	 * @param n
	 * @throws IllegalArgumentException if n < 1
	 */
	public void multiply(long n) throws IllegalArgumentException 
	{
		if (n < 1)
			throw new IllegalArgumentException("N is less than 1");
		
		Node last = head;
		int prime = 2;
		int lastNum = 0;
		long left = n;
		
		while(!isPrime(left))
		{
			if (Math.floorMod(left, prime) == 0)
			{
				if (prime != lastNum)
				{
					link(last, new Node(prime, 1));
					lastNum = prime;
					last = last.next;
				}
				else
				{
					last.pFactor.multiplicity++;
				}
			}
			else
			{
				if(prime == 2)
					prime ++;
				else
					prime += 2;
			}
		}
		if (lastNum != left)
		{
			link(last, new Node(prime, 1));
		}
		else
		{
			last.pFactor.multiplicity++;
		}
		updateValue();
	}
	
	/**
	 * Multiplies the represented integer v with another number in the factorization form.  Traverse both 
	 * linked lists and store the result in this list object.  See Section 3.1 in the project description 
	 * for details of algorithm. 
	 * 
	 * @param pf 
	 */
	public void multiply(PrimeFactorization pf)
	{
		for(PrimeFactor x: pf) {
			add(x.prime, x.multiplicity);
		}
		updateValue();
	}
	
	
	/**
	 * Multiplies the integers represented by two PrimeFactorization objects.  
	 * 
	 * @param pf1
	 * @param pf2
	 * @return object of PrimeFactorization to represent the product 
	 */
	public static PrimeFactorization multiply(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorizationIterator pfit1 = pf1.iterator();
		PrimeFactorizationIterator pfit2 = pf2.iterator();
		
		long total = 1;
		
		while(pfit1.hasNext())
		{
			PrimeFactor temp = pfit1.next();
			for(int j = 0; j < temp.multiplicity; j ++)
				total = total * temp.prime;
		}
		
		while(pfit2.hasNext())
		{
			PrimeFactor temp = pfit2.next();
			for(int j = 0; j < temp.multiplicity; j ++)
				total = total * temp.prime;
		}
		
		return new PrimeFactorization(total); 
	}

	
	/**
	 * Divides the represented integer v by n.  Make updates to the list, value, size if divisible.  
	 * No update otherwise. Refer to Section 3.2 in the project description for details. 
	 *  
	 * @param n
	 * @return  true if divisible 
	 *          false if not divisible 
	 * @throws IllegalArgumentException if n <= 0
	 */
	public boolean dividedBy(long n) throws IllegalArgumentException
	{
		if(n<=0) 
			throw new IllegalArgumentException();
		
		if(value < n) 
			return false;
		
		return dividedBy(new PrimeFactorization(n));
	}

	
	/**
	 * Division where the divisor is represented in the factorization form.  Update the linked 
	 * list of this object accordingly by removing those nodes housing prime factors that disappear  
	 * after the division.  No update if this number is not divisible by pf. Algorithm details are 
	 * given in Section 3.2. 
	 * 
	 * @param pf
	 * @return	true if divisible by pf
	 * 			false otherwise
	 */
	public boolean dividedBy(PrimeFactorization pf)
	{
		if(value < pf.value) 
			return false;
		if(value!=-1 && pf.value==-1) 
			return false;
		
		if(value == pf.value) 
		{
			clearList();
			return true;
		}
		else
		{
			PrimeFactorization dupe = new PrimeFactorization(this);
			PrimeFactorizationIterator pfit = pf.iterator();
			PrimeFactor target;
				
			while(pfit.hasNext())
			{
				target = pfit.next();
				if(!dupe.remove(target.prime, target.multiplicity)) 
					return false;		
			}
			
			this.head = dupe.head;
			this.tail = dupe.tail;
			this.size = dupe.size;
			
			updateValue();
		}
		return true;
	}

	
	/**
	 * Divide the integer represented by the object pf1 by that represented by the object pf2. 
	 * Return a new object representing the quotient if divisible. Do not make changes to pf1 and 
	 * pf2. No update if the first number is not divisible by the second one. 
	 *  
	 * @param pf1
	 * @param pf2
	 * @return quotient as a new PrimeFactorization object if divisible
	 *         null otherwise 
	 */
	public static PrimeFactorization dividedBy(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorization out = null;
		
		if (pf1.value == PrimeFactorization.gcd(pf1, pf2).value);
		{
			PrimeFactorizationIterator pfit1 = pf1.iterator();
			PrimeFactorizationIterator pfit2 = pf2.iterator();
			
			PrimeFactor temp1 = pfit1.next();
			PrimeFactor temp2 = pfit2.next();
			int count = 0;
			
			while(pfit2.hasNext())
			{
				if (temp1.prime == temp2.prime)
				{
					if(temp1.multiplicity > temp2.multiplicity)
						count++;
					temp2 = pfit2.next();
					temp1 = pfit1.next();
				}
				else
				{
					count++;
					temp1 = pfit1.next();
				}
			}
			if (temp1.prime == temp2.prime)
			{
				if(temp1.multiplicity > temp2.multiplicity)
					count++;
				temp1 = pfit1.next();
			}
			else
			{
				count++;
				temp1 = pfit1.next();
			}
			while(pfit1.hasNext())
			{
				pfit1.next();
				count++;
			}
			
			PrimeFactor arry[] = new PrimeFactor[count];
			
			pfit1 = pf1.iterator();
			pfit2 = pf2.iterator();
			
			temp1 = pfit1.next();
			temp2 = pfit2.next();
			count = 0;
			
			while(pfit2.hasNext())
			{
				if (temp1.prime == temp2.prime)
				{
					if(temp1.multiplicity > temp2.multiplicity)
					{
						arry[count] = new PrimeFactor(temp1.prime, temp1.multiplicity - temp2.multiplicity);
						count++;
					}
					temp2 = pfit2.next();
					temp1 = pfit1.next();
				}
				else
				{
					arry[count] = new PrimeFactor(temp1.prime, temp1.multiplicity);
					count++;
					temp1 = pfit1.next();
				}
			}
			if (temp1.prime == temp2.prime)
			{
				if(temp1.multiplicity > temp2.multiplicity)
				{
					arry[count] = new PrimeFactor(temp1.prime, temp1.multiplicity - temp2.multiplicity);
					count++;
				}
				temp1 = pfit1.next();
			}
			else
			{
				arry[count] = new PrimeFactor(temp1.prime, temp1.multiplicity);
				count++;
				temp1 = pfit1.next();
			}
			while(pfit1.hasNext())
			{
				temp1 = pfit1.next();
				arry[count] = new PrimeFactor(temp1.prime, temp1.multiplicity);
				count++;
			}
			arry[count] = new PrimeFactor(temp1.prime, temp1.multiplicity);
			count++;
			
		}
		return out; 
	}

	
	// -------------------------------------------------
	// Greatest Common Divisor and Least Common Multiple 
	// -------------------------------------------------

	/**
	 * Computes the greatest common divisor (gcd) of the represented integer v and an input integer n.
	 * Returns the result as a PrimeFactor object.  Calls the method Euclidean() if 
	 * this.value != OVERFLOW.
	 *     
	 * It is more efficient to factorize the gcd than n, which can be much greater. 
	 *     
	 * @param n
	 * @return prime factorization of gcd
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization gcd(long n) throws IllegalArgumentException
	{
		PrimeFactorization temp = new PrimeFactorization(n);
		return PrimeFactorization.gcd(this, temp); 
	}
	

	/**
	  * Implements the Euclidean algorithm to compute the gcd of two natural numbers m and n. 
	  * The algorithm is described in Section 4.1 of the project description. 
	  * 
	  * @param m
	  * @param n
	  * @return gcd of m and n. 
	  * @throws IllegalArgumentException if m < 1 or n < 1
	  */
 	public static long Euclidean(long m, long n) throws IllegalArgumentException
	{
 		if (m < 1 || n < 1)
 			throw new IllegalArgumentException("m or n is less than 1");
 		
 		while (m != 0) 
 		{
            long temp = m;
            m = n % m;
            n = temp;
        }
        return n;
	}

 	
	/**
	 * Computes the gcd of the values represented by this object and pf by traversing the two lists.  No 
	 * direct computation involving value and pf.value. Refer to Section 4.2 in the project description 
	 * on how to proceed.  
	 * 
	 * @param  pf
	 * @return prime factorization of the gcd
	 */
	public PrimeFactorization gcd(PrimeFactorization pf)
	{
		return PrimeFactorization.gcd(this, pf); 
	}
	
	
	/**
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the gcd of two numbers represented by pf1 and pf2
	 */
	public static PrimeFactorization gcd(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorizationIterator pfit1 = pf1.iterator();
		PrimeFactorizationIterator pfit2 = pf2.iterator();
		
		Boolean cont = true;
		int count = 0;
		PrimeFactor next1 = pfit1.next();
		PrimeFactor next2 = pfit2.next();
		
		while(cont)
		{
			if (next1.prime == next2.prime)
				count++;
			else
			{
				if (next1.prime > next2.prime)
				{
					if(pfit2.hasNext())
						next2 = pfit2.next();
					else
						cont = false;
				}
				else
				{
					if(pfit1.hasNext())
						next1 = pfit1.next();
					else
						cont = false;
				}
			}
		}
		
		if (count == 0)
			return new PrimeFactorization();
		
		PrimeFactor arry[] = new PrimeFactor[count];
		
		pfit1 = pf1.iterator();
		pfit2 = pf2.iterator();
		cont = true;
		next1 = pfit1.next();
		next2 = pfit2.next();
		count = 0;
		
		while(cont)
		{
			if (next1.prime == next2.prime)
			{
				if (next1.multiplicity >= next2.multiplicity)
				{
					arry[count] = new PrimeFactor(next1.prime, next2.multiplicity);
					count++;
				}
				else
				{
					arry[count] = new PrimeFactor(next1.prime, next1.multiplicity);
					count++;
				}
			}
			else
			{
				if (next1.prime > next2.prime)
				{
					if(pfit2.hasNext())
						next2 = pfit2.next();
					else
						cont = false;
				}
				else
				{
					if(pfit1.hasNext())
						next1 = pfit1.next();
					else
						cont = false;
				}
			}
		}
		
		PrimeFactorization out = new PrimeFactorization(arry);
		return out; 
	}

	
	/**
	 * Computes the least common multiple (lcm) of the two integers represented by this object 
	 * and pf.  The list-based algorithm is given in Section 4.3 in the project description. 
	 * 
	 * @param pf  
	 * @return factored least common multiple  
	 */
	public PrimeFactorization lcm(PrimeFactorization pf)
	{
		return PrimeFactorization.lcm(this, pf); 
	}

	
	/**
	 * Computes the least common multiple of the represented integer v and an integer n. Construct a 
	 * PrimeFactors object using n and then call the lcm() method above.  Calls the first lcm() method. 
	 * 
	 * @param n
	 * @return factored least common multiple 
	 * @throws IllegalArgumentException if n < 1
	 */
	public PrimeFactorization lcm(long n) throws IllegalArgumentException 
	{
		if (n < 1)
			throw new IllegalArgumentException("n is less than 1");
		
		PrimeFactorization in = new PrimeFactorization(n);
		
		return PrimeFactorization.lcm(this, in); 
	}

	/**
	 * Computes the least common multiple of the integers represented by pf1 and pf2. 
	 * 
	 * @param pf1
	 * @param pf2
	 * @return prime factorization of the lcm of two numbers represented by pf1 and pf2
	 */
	public static PrimeFactorization lcm(PrimeFactorization pf1, PrimeFactorization pf2)
	{
		PrimeFactorizationIterator pfit1 = pf1.iterator();
		PrimeFactorizationIterator pfit2 = pf2.iterator();
		
		Boolean cont = true;
		int count = 0;
		PrimeFactor next1 = pfit1.next();
		PrimeFactor next2 = pfit2.next();
		
		while(cont)
		{
			if (next1.prime == next2.prime)
				count++;
			else
			{
				if (next1.prime > next2.prime)
				{
					if(pfit2.hasNext())
					{
						next2 = pfit2.next();
						count++;
					}
					else
					{
						if(pfit1.hasNext())
						{
							next1 = pfit1.next();
							count++;
						}
						else
							cont = false;
					}
				}
				else
				{
					if(pfit1.hasNext())
					{
						next1 = pfit1.next();
						count++;
					}
					else
					{
						if(pfit2.hasNext())
						{
							next2 = pfit2.next();
							count++;
						}
						else
							cont = false;
				
					}
				}
			}
		}
		
		if (count == 0)
			return new PrimeFactorization();
		
		PrimeFactor arry[] = new PrimeFactor[count];
		
		pfit1 = pf1.iterator();
		pfit2 = pf2.iterator();
		cont = true;
		next1 = pfit1.next();
		next2 = pfit2.next();
		count = 0;
		
		while(cont)
		{
			if (next1.prime == next2.prime)
			{
				if (next1.multiplicity >= next2.multiplicity)
				{
					arry[count] = new PrimeFactor(next1.prime, next1.multiplicity);
					count++;
				}
				else
				{
					arry[count] = new PrimeFactor(next1.prime, next2.multiplicity);
					count++;
				}
			}
			else
			{
				if (next1.prime > next2.prime)
				{
					if(pfit2.hasNext())
					{	
						arry[count] = new PrimeFactor(next2.prime, next2.multiplicity);
						next2 = pfit2.next();
						count++;
					}
					else
					{
						if(pfit1.hasNext())
						{
							arry[count] = new PrimeFactor(next1.prime, next1.multiplicity);
							next1 = pfit1.next();
							count++;
						}
						else
							cont = false;
					}
				}
				else
				{
					if(pfit1.hasNext())
					{
						arry[count] = new PrimeFactor(next1.prime, next1.multiplicity);
						next1 = pfit1.next();
						count++;
					}
					else
					{
						if(pfit2.hasNext())
						{
							arry[count] = new PrimeFactor(next2.prime, next2.multiplicity);
							next2 = pfit2.next();
							count++;
						}
						else
							cont = false;
				
					}
				}
			}
		}
		
		PrimeFactorization out = new PrimeFactorization(arry);
		return out;
	}

	
	// ------------
	// List Methods
	// ------------
	
	/**
	 * Traverses the list to determine if p is a prime factor. 
	 * 
	 * Precondition: p is a prime. 
	 * 
	 * @param p  
	 * @return true  if p is a prime factor of the number v represented by this linked list
	 *         false otherwise 
	 * @throws IllegalArgumentException if p is not a prime
	 */
	public boolean containsPrimeFactor(int p) throws IllegalArgumentException
	{
		if(!isPrime(p))
			throw new IllegalArgumentException("p is not prime");
		
		PrimeFactorizationIterator pfit = new PrimeFactorizationIterator();
    	
    	while (pfit.hasNext())
    	{
    		if (pfit.next().prime == p)
    			return true;
    	}
		return false; 
	}
	
	// The next two methods ought to be private but are made public for testing purpose. Keep
	// them public 
	
	/**
	 * Adds a prime factor p of multiplicity m.  Search for p in the linked list.  If p is found at 
	 * a node N, add m to N.multiplicity.  Otherwise, create a new node to store p and m. 
	 *  
	 * Precondition: p is a prime. 
	 * 
	 * @param p  prime 
	 * @param m  multiplicity
	 * @return   true  if m >= 1
	 *           false if m < 1   
	 */
    public boolean add(int p, int m) 
    {
    	if (m < 1)
    		return false;
    	
    	Boolean found = false;
    	PrimeFactorizationIterator pfit = new PrimeFactorizationIterator();
    	
    	while (pfit.hasNext())
    	{
    		PrimeFactor pf = pfit.next();
    		if(pf.prime == p)
    		{
    			pf.multiplicity += m;
    			found = true;
    		}
    		if (pf.prime >= p)
    		{
    			pfit.previous();
    			pfit.add(new PrimeFactor(p,m));
    			pfit.next();
    			found = true;
    		}
    	}
    	
    	if(!found)
    	{
    		pfit.add(new PrimeFactor(p,m));
    	}
    	
    	return true; 
    }

	    
    /**
     * Removes m from the multiplicity of a prime p on the linked list.  It starts by searching 
     * for p.  Returns false if p is not found, and true if p is found. In the latter case, let 
     * N be the node that stores p. If N.multiplicity > m, subtracts m from N.multiplicity.  
     * If N.multiplicity <= m, removes the node N.  
     * 
     * Precondition: p is a prime. 
     * 
     * @param p
     * @param m
     * @return true  when p is found. 
     *         false when p is not found. 
     * @throws IllegalArgumentException if m < 1
     */
    public boolean remove(int p, int m) throws IllegalArgumentException
    {
		PrimeFactorizationIterator pfit = new PrimeFactorizationIterator();
		
		while (pfit.hasNext())
		{
			PrimeFactor pf = pfit.next();
			if(pf.prime == p)
			{
				if(pf.multiplicity > m)
					pf.multiplicity -= m;
				else
					pfit.remove();
				return true;
			}
		}
    	return false; 
    }


    /**
     * 
     * @return size of the list
     */
	public int size() 
	{
		int size = 0;
		PrimeFactorizationIterator pfit = new PrimeFactorizationIterator();
		
		while (pfit.hasNext())
		{
			size++;
			pfit.next();
		}
		return size; 
	}

	
	/**
	 * Writes out the list as a factorization in the form of a product. Represents exponentiation 
	 * by a caret.  For example, if the number is 5814, the returned string would be printed out 
	 * as "2 * 3^2 * 17 * 19". 
	 */
	@Override 
	public String toString()
	{
		String out = "";
		
		PrimeFactorizationIterator pfit = new PrimeFactorizationIterator();
		
		while (pfit.hasNext())
		{
			out += pfit.next().toString();
		}
		
		return out; 
	}

	
	// The next three methods are for testing, but you may use them as you like.  

	/**
	 * @return true if this PrimeFactorization is representing a value that is too large to be within 
	 *              long's range. e.g. 999^999. false otherwise.
	 */
	public boolean valueOverflow() {
		return value == OVERFLOW;
	}

	/**
	 * @return value represented by this PrimeFactorization, or -1 if valueOverflow()
	 */
	public long value() {
		return value;
	}

	
	public PrimeFactor[] toArray() {
		PrimeFactor[] arr = new PrimeFactor[size];
		int i = 0;
		for (PrimeFactor pf : this)
			arr[i++] = pf;
		return arr;
	}


	
	@Override
	public PrimeFactorizationIterator iterator()
	{
	    return new PrimeFactorizationIterator();
	}
	
	/**
	 * Doubly-linked node type for this class.
	 */
    private class Node 
    {
		public PrimeFactor pFactor;			// prime factor 
		public Node next;
		public Node previous;
		
		/**
		 * Default constructor for creating a dummy node.
		 */
		public Node()
		{
			next = null;
			previous = null;
			pFactor = null; 
		}
	    
		/**
		 * Precondition: p is a prime
		 * 
		 * @param p	 prime number 
		 * @param m  multiplicity 
		 * @throws IllegalArgumentException if m < 1 
		 */
		public Node(int p, int m) throws IllegalArgumentException 
		{	
			if (m < 1)
				throw new IllegalArgumentException("Multiplicity less than 1");
			pFactor = new PrimeFactor(p, m);
			next = null;
			previous = null;
		}   

		
		/**
		 * Constructs a node over a provided PrimeFactor object. 
		 * 
		 * @param pf
		 * @throws IllegalArgumentException
		 */
		public Node(PrimeFactor pf)  
		{
			pFactor = pf;
			next = null;
			previous = null;
		}


		/**
		 * Printed out in the form: prime + "^" + multiplicity.  For instance "2^3". 
		 * Also, deal with the case pFactor == null in which a string "dummy" is 
		 * returned instead.  
		 */
		@Override
		public String toString() 
		{ 
			if (pFactor == null)
				return "Dummy";
			return pFactor.toString(); 
		}
    }

    
    public class PrimeFactorizationIterator implements ListIterator<PrimeFactor>
    {  	
        // Class invariants: 
        // 1) logical cursor position is always between cursor.previous and cursor
        // 2) after a call to next(), cursor.previous refers to the node just returned 
        // 3) after a call to previous() cursor refers to the node just returned 
        // 4) index is always the logical index of node pointed to by cursor

        private Node cursor = head.next;
        private Node pending = null;    // node pending for removal
        private int index = 0;      
  	  
    	// other instance variables ... 
    	  
      
        /**
    	 * Default constructor positions the cursor before the smallest prime factor.
    	 */
    	public PrimeFactorizationIterator()
    	{
    		cursor = head.next;
    		pending = null;
    		index = 0;
    	}

    	@Override
    	public boolean hasNext()
    	{
    		if (cursor.next == tail)
    			return false;
    		return true; 
    	}

    	
    	@Override
    	public boolean hasPrevious()
    	{
    		if (cursor.previous == head)
    			return false;
    		return true;
    	}

 
    	@Override 
    	public PrimeFactor next() 
    	{
    		pending = cursor;
    		cursor = cursor.next;
    		index++;
    		return cursor.pFactor;
    	}

 
    	@Override 
    	public PrimeFactor previous() 
    	{
    		pending = cursor;
    		cursor = cursor.previous;
    		index--; 
    		return pending.pFactor; 
    	}

   
    	/**
    	 *  Removes the prime factor returned by next() or previous()
    	 *  
    	 *  @throws IllegalStateException if pending == null 
    	 */
    	@Override
    	public void remove() throws IllegalStateException
    	{
    		if (pending == null)
    			throw new IllegalStateException("pending = null");
    		unlink(pending);
    		pending = null;
    		index--;
    	}
 
 
    	/**
    	 * Adds a prime factor at the cursor position.  The cursor is at a wrong position 
    	 * in either of the two situations below: 
    	 * 
    	 *    a) pf.prime < cursor.previous.pFactor.prime if cursor.previous != head. 
    	 *    b) pf.prime > cursor.pFactor.prime if cursor != tail. 
    	 * 
    	 * Take into account the possibility that pf.prime == cursor.pFactor.prime. 
    	 * 
    	 * Precondition: pf.prime is a prime. 
    	 * 
    	 * @param pf  
    	 * @throws IllegalArgumentException if the cursor is at a wrong position. 
    	 */
    	@Override
        public void add(PrimeFactor pf) throws IllegalArgumentException 
        {
    		if ((cursor.previous != head && pf.prime < cursor.previous.pFactor.prime) || (cursor != tail && pf.prime > cursor.pFactor.prime))
    			throw new IllegalArgumentException("Adding in worng place");
    		link(cursor, new Node(pf));
    		cursor = cursor.next;
    		index++;
        }


    	@Override
		public int nextIndex() 
		{
			return index;
		}


    	@Override
		public int previousIndex() 
		{
			return index - 1;
		}

		@Deprecated
		@Override
		public void set(PrimeFactor pf) 
		{
			throw new UnsupportedOperationException(getClass().getSimpleName() + " does not support set method");
		}
        
    	// Other methods you may want to add or override that could possibly facilitate 
    	// other operations, for instance, addition, access to the previous element, etc.
    	// 
    	// ...
    	// 
    }

    
    // --------------
    // Helper methods 
    // -------------- 
    
    /**
     * Inserts toAdd into the list after current without updating size.
     * 
     * Precondition: current != null, toAdd != null
     */
    private void link(Node current, Node toAdd)
    {
    	if(current != null && toAdd != null)
    	{
    		current.next.previous = toAdd;
    		toAdd.next = current.next;
    		toAdd.previous = current;
    		current.next = toAdd;
    	}
    }

	 
    /**
     * Removes toRemove from the list without updating size.
     */
    private void unlink(Node toRemove)
    {
    	toRemove.previous.next = toRemove.next;
    	toRemove.next.previous = toRemove.previous;
    	toRemove.next = null;
    	toRemove.previous = null;
    }


    /**
	  * Remove all the nodes in the linked list except the two dummy nodes. 
	  * 
	  * Made public for testing purpose.  Ought to be private otherwise. 
	  */
	public void clearList()
	{
		Node n = head.next;
		while(n != tail)
		{
			n = n.next;
			unlink(n.previous);
		}
	}	
	
	/**
	 * Multiply the prime factors (with multiplicities) out to obtain the represented integer.  
	 * Use Math.multiply(). If an exception is throw, assign OVERFLOW to the instance variable value.  
	 * Otherwise, assign the multiplication result to the variable. 
	 * 
	 */
	private void updateValue()
	{
		try {		
			long v = 1;
			PrimeFactorizationIterator pfit = new PrimeFactorizationIterator();
			
			while (pfit.hasNext())
			{
				PrimeFactor temp = pfit.next();
				
				for (int j = 0; j < temp.multiplicity; j++)
					v = v * temp.prime;
			}
			this.value = v;
		} 
			
		catch (ArithmeticException e) 
		{
			value = OVERFLOW;
		}
	}
}
