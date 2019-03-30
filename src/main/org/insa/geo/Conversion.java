package org.insa.geo;

public class Conversion {
	public static double toMetersPerSeconds(double speed){
		//Need to divide by 3.6
		return speed*(1/3.6);
	}
}
