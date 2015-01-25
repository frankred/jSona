package de.roth.jsona.util;


public class NumberUtil {

	public static int keepInRange(int from, int to, int value) {
		if (value > 100) {
			value = 100;
		} else if (value < 0) {
			value = 0;
		}
		return value;
	}
}
