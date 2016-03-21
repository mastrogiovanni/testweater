package com.crossover.trial.weather.utility;

import org.apache.commons.lang3.StringUtils;

public class NumberUtility {
	
	public static Double parseDoubleOrNull(String value) {
		value = StringUtils.stripToNull(value);
		if ( null == value ) {
			return null;
		}
		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}
