package util;

import java.util.TreeSet;

//import org.apache.commons.math3.util.CombinatoricsUtils;

public class Util {
	private static long factorialArray[] = {1l,1l,2l,6l,24l,120l,720l,5040l,40320l,362880l,3628800l,39916800l,479001600l,6227020800l,87178291200l,1307674368000l,20922789888000l,355687428096000l,6402373705728000l,121645100408832000l,2432902008176640000l};
	public static Object search(TreeSet treeset, Object key) {
	    Object ceil  = treeset.ceiling(key); // least elt >= key
	    Object floor = treeset.floor(key);   // highest elt <= key
	    return ceil == floor ? ceil : null; 
	}
	
	public static long calculatePermutations(int k1, int k2) {
		int kT = k1+k2;
		int kM = Math.max(k1, k2);
		int km = Math.min(k1,  k2);
		long middle = 1;
		for (int i = kM+1; i<=kT;i++) {
			middle *= i;
		}
		//return middle/CombinatoricsUtils.factorial(km);
		return middle/factorial(km);
	}
	
	public static long factorial(int value) {
		
		long partial = 1;
		for (; value > 20; value--) {
			partial *= value;
		}
		partial *= factorialArray[value];
		return partial;
	}
}
