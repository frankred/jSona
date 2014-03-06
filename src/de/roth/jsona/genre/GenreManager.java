package de.roth.jsona.genre;

import java.util.HashMap;

import de.roth.jsona.config.Global;

/**
 * Genre manager holds all genres in a hash map
 * 
 * @author Frank Roth
 * 
 */
public class GenreManager {

	private static final GenreManager instance = new GenreManager();
	private HashMap<String, Integer> genreHashMap;

	public GenreManager() {
		// Create genre hashmap for fast access
		genreHashMap = new HashMap<String, Integer>(Global.ID3_GENRE_LIST.length);

		for (int i = 0; i < Global.ID3_GENRE_LIST.length; i++) {
			genreHashMap.put(Global.ID3_GENRE_LIST[i], i);
		}
	}

	public static GenreManager getInstance() {
		return (instance);
	}

	/**
	 * Get genre index by name
	 * 
	 * @param name
	 *            of Genre
	 * @return Genre index
	 */
	public int getGenreIndexByName(String name) {
		Integer i = genreHashMap.get(name);
		if (i == null) {
			return -1;
		}
		return i.intValue();
	}

	/**
	 * Get genre name by index
	 * 
	 * @param index
	 *            of genre
	 * @return genre name
	 */
	public String getGenreNameByIndex(int index) {
		if (index > -1 && index < Global.ID3_GENRE_LIST.length) {
			return Global.ID3_GENRE_LIST[index];
		}
		return null;
	}
}