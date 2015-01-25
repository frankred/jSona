package de.roth.jsona.util;

public class JoinUtil {

	/**
	 * Join a over given float array
	 *
	 * @param numbers
	 * @param joinSeperator
	 * @param showIndex
	 * @return
	 */
	public static String join(float[] numbers, String joinSeperator, boolean showIndex) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < numbers.length; i++) {
			if (i != 0) {
				buffer.append(joinSeperator);

			}
			if (showIndex) {
				buffer.append(i);
				buffer.append(": ");
			}
			buffer.append(numbers[i]);
		}
		return buffer.toString();
	}
}
