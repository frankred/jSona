package de.roth.jsona.tag;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v1;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.AbstractID3v2Frame;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_3Frame;
import org.farng.mp3.lyrics3.AbstractLyrics3;

import de.roth.jsona.config.Global;
import de.roth.jsona.genre.GenreManager;
import de.roth.jsona.model.MusicListItem;
import de.roth.jsona.util.ASCIIFilter;

public class MP3Tagger {

	public static void parseID3v2(AbstractID3v2 id3v2_4, File file) {

		ID3v2 tag = new ID3v2();
		tag.setAlbum(id3v2_4.getAlbumTitle());
		tag.setArtist(id3v2_4.getLeadArtist());

		Iterator<AbstractID3v2Frame> iter = id3v2_4.getFrameIterator();

		while (iter.hasNext()) {
			AbstractID3v2Frame o = (AbstractID3v2Frame) iter.next();

			if (o instanceof ID3v2_3Frame) {
				ID3v2_3Frame id3v2_3Frame = (ID3v2_3Frame) iter.next();
			}
		}
	}

	public static de.roth.jsona.tag.ID3v1 parseID3v1(ID3v1 id3v1RawTag, File file) {

		de.roth.jsona.tag.ID3v1 tag = new de.roth.jsona.tag.ID3v1();

		tag.setFilepath(file.getAbsolutePath());

		if (id3v1RawTag != null) {
			try {
				tag.setArtist(id3v1RawTag.getArtist());
			} catch (UnsupportedOperationException e) {
			}
			try {
				tag.setTitle(id3v1RawTag.getTitle());
			} catch (UnsupportedOperationException e) {
			}
			try {
				tag.setAlbum(id3v1RawTag.getAlbum());
			} catch (UnsupportedOperationException e) {
			}
			try {
				tag.setYear(Integer.parseInt(id3v1RawTag.getYear()));
			} catch (UnsupportedOperationException e) {
			} catch (NumberFormatException e) {
			}
			try {
				tag.setComment(id3v1RawTag.getSongComment());
			} catch (UnsupportedOperationException e) {
			}
			try {
				tag.setGenre(Integer.parseInt(id3v1RawTag.getSongGenre()));
			} catch (UnsupportedOperationException e) {
			} catch (NumberFormatException e) {
			}
			try {
				tag.setTrackNumber(Integer.parseInt(id3v1RawTag.getTrackNumberOnAlbum()));
			} catch (UnsupportedOperationException e) {
			}
		}
		return tag;
	}

	public static MusicListItem tag(MusicListItem item) {

		String title = null;
		String artist = null;
		String album = null;
		String trackNo = null;
		String year = null;
		String genre = null;

		try {
			MP3File mp3File = new MP3File(item.getFile());

			AbstractLyrics3 lyrics3 = mp3File.getLyrics3Tag();
			AbstractID3v1 id3v1 = mp3File.getID3v1Tag();
			AbstractID3v2 id3v2 = mp3File.getID3v2Tag();

			String id3v1_songTitle = null;
			String id3v2_songTitle = null;
			String lyrics3_songTitle = null;
			String id3v1_albumTitle = null;
			String id3v2_albumTitle = null;
			String lyrics3_albumTitle = null;
			String id3v1_year = null;
			String id3v2_year = null;
			String lyrics3_year = null;
			String id3v1_genre = null;
			String id3v2_genre = null;
			String lyrics3_genre = null;
			String id3v1_trackNo = null;
			String id3v2_trackNo = null;
			String lyrics3_trackNo = null;
			String id3v1_artist = null;
			String id3v2_artist = null;
			String lyrics3_artist = null;

			// Lyrics3
			if (lyrics3 != null) {
				try {
					lyrics3_songTitle = lyrics3.getSongTitle();
					if (lyrics3_songTitle != null && !lyrics3_songTitle.equals("")) {
						title = lyrics3_songTitle;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					lyrics3_artist = lyrics3.getLeadArtist();
					if (lyrics3_artist != null && !lyrics3_artist.equals("")) {
						artist = lyrics3_artist;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					lyrics3_albumTitle = lyrics3.getAlbumTitle();
					if (lyrics3_albumTitle != null && !lyrics3_albumTitle.equals("")) {
						album = lyrics3_albumTitle;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					lyrics3_year = lyrics3.getYearReleased();
					if (lyrics3_year != null && !lyrics3_year.equals("")) {
						year = lyrics3_year;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					lyrics3_genre = lyrics3.getSongGenre();
					if (lyrics3_genre != null && !lyrics3_genre.equals("")) {
						genre = lyrics3_genre;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					lyrics3_trackNo = lyrics3.getTrackNumberOnAlbum();
					if (lyrics3_trackNo != null && !lyrics3_trackNo.equals("") && !lyrics3_trackNo.equals("0")) {
						trackNo = lyrics3_trackNo;
					}
				} catch (UnsupportedOperationException e) {
				}
			}

			// IDV2
			if (id3v2 != null) {
				try {
					id3v2_songTitle = id3v2.getSongTitle();
					title = id3v2_songTitle;
					if (id3v2_songTitle != null && !id3v2_songTitle.equals("")) {
						title = id3v2_songTitle;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v2_artist = id3v2.getLeadArtist();
					if (id3v2_artist != null && !id3v2_artist.equals("")) {
						artist = id3v2_artist;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v2_albumTitle = id3v2.getAlbumTitle();
					if (id3v2_albumTitle != null && !id3v2_albumTitle.equals("")) {
						album = id3v2_albumTitle;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v2_year = id3v2.getYearReleased();
					if (id3v2_year != null && !id3v2_year.equals("")) {
						year = id3v2_year;
					}

				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v2_genre = id3v2.getSongGenre();
					if (id3v2_genre != null && !id3v2_genre.equals("")) {
						genre = id3v2_genre;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v2_trackNo = id3v2.getTrackNumberOnAlbum();
					if (id3v2_trackNo != null && !id3v2_trackNo.equals("") && !id3v2_trackNo.equals("0")) {
						trackNo = id3v2_trackNo;
					}
				} catch (UnsupportedOperationException e) {
				}
			}

			if (id3v1 != null) {
				try {
					id3v1_songTitle = id3v1.getSongTitle();
					title = id3v1_songTitle;
					if (id3v1_songTitle != null && !id3v1_songTitle.equals("")) {
						title = id3v1_songTitle;
					}
				} catch (UnsupportedOperationException e) {
				}

				try {
					id3v1_artist = id3v1.getLeadArtist();
					if (id3v1_artist != null && !id3v1_artist.equals("")) {
						artist = id3v1_artist;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v1_albumTitle = id3v1.getAlbumTitle();
					if (id3v1_albumTitle != null && !id3v1_albumTitle.equals("")) {
						album = id3v1_albumTitle;
					}

				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v1_year = id3v1.getYearReleased();
					if (id3v1_year != null && !id3v1_year.equals("")) {
						year = id3v1_year;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v1_genre = id3v1.getSongGenre();
					if (id3v1_genre != null && !id3v1_genre.equals("")) {
						genre = id3v1_genre;
					}
				} catch (UnsupportedOperationException e) {
				}
				try {
					id3v1_trackNo = id3v1.getTrackNumberOnAlbum();
					if (id3v1_trackNo != null && !id3v1_trackNo.equals("") && !id3v1_trackNo.equals("0")) {
						trackNo = id3v1_trackNo;
					}
				} catch (UnsupportedOperationException e) {
				}
			}

			if (album != null) {
				item.setAlbum(ASCIIFilter.filter(Global.ASCII_FILTER, album));
			}

			if (artist != null) {
				item.setArtist(ASCIIFilter.filter(Global.ASCII_FILTER, artist));
			}

			if (title != null) {
				item.setTitle(ASCIIFilter.filter(Global.ASCII_FILTER, title));
			}

			if (trackNo != null) {
				item.setTrackNo(ASCIIFilter.filter(Global.ASCII_FILTER, trackNo));
			}

			if (year != null) {
				item.setYear(ASCIIFilter.filter(Global.ASCII_FILTER, year));
			}

			if (genre != null) {
				if (isInteger(genre)) {
					int genreIndex = Integer.parseInt(genre);
					if (genreIndex >= 0) {
						item.setGenre(genreIndex);
					}
				} else {
					int genreIndex = GenreManager.getInstance().getGenreIndexByName(genre);
					if (genreIndex >= 0) {
						item.setGenre(genreIndex);
					}
				}
			}

		} catch (UnsupportedOperationException uoe) {

		} catch (IOException e1) {

		} catch (TagException e1) {

		}

		return item;
	}

	/**
	 * Check if a string is parsable into an integer
	 * 
	 * @param s
	 * @return
	 */
	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}
}