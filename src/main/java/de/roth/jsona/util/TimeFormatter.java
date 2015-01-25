package de.roth.jsona.util;

/**
 * Class to format time in milliseconds (1/1000s) or seconds to a string
 * with hours, minutes and seconds
 * @author Frank Roth
 *
 */
public class TimeFormatter {
	
	/**
	 * Format seconds to a string containing hours, minutes and seconds
	 * @param value - milliseconds
	 * @return Hours, minutes and seconds as string
	 */
	public static String formatMilliseconds(long value){
		return formatSeconds(value/1000);
	}

	/**
	 * Format seconds to a string containing hours, minutes and seconds
	 * @param value - seconds
	 * @return Hours, minutes and seconds as string
	 */
	public static String formatSeconds(long value){
		int[] ints = getTrinityTime(value);
		
		StringBuffer outputString = new StringBuffer(); 
		
		String hh = String.valueOf(ints[0]);
		String mm = String.valueOf(ints[1]);
		String ss = String.valueOf(ints[2]);

		if(ints[0] < 10){
			hh = String.valueOf("0" + ints[0]);
		}
		
		if(ints[1] < 10){
			mm = String.valueOf("0" + ints[1]);
		}
		
		if(ints[2] < 10){
			ss = String.valueOf("0" + ints[2]);
		}
		
		// hours?
		if(ints[0] != 0){
			outputString.append(hh + ":");
			outputString.append(mm + ":");
			outputString.append(ss);
			return outputString.toString();
		}
		
		if(mm.startsWith("0")){
			mm = mm.substring(1);
		}
		
		outputString.append(mm + ":");
		outputString.append(ss);
		return outputString.toString();
	}
	
	
	/**
	 * Convert time in seconds to hours, minutes and seconds
	 * @param longVal
	 * @return hours, minutes and seconds as int array
	 */
	private static int[] getTrinityTime(long longVal)
	{
	    int hours = (int) longVal / 3600;
	    int remainder = (int) longVal - hours * 3600;
	    int mins = remainder / 60;
	    remainder = remainder - mins * 60;
	    int secs = remainder;
	    int[] ints = {hours , mins , secs};
	    return ints;
	}
}
