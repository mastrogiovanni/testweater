package com.crossover.trial.weather.utility;

import org.apache.commons.lang3.StringUtils;

public class NumberUtility {
	
	public static double parseDoubleOrZero(String value) {
		value = StringUtils.stripToNull(value);
		if ( null == value ) {
			return 0.0;
		}
		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e) {
			return 0.0;
		}
	}

}
