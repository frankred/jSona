package de.roth.jsona.tag.detection;

/**
 * This class represents one field as a result of the detection. For example if
 * the user uses the %ARTIST% placeholder, then a field is created with the
 * field identifier ARTIST and the value of this field.
 *
 * @author Frank Roth
 */
public class FieldResult {

    public enum Field {
        ARTIST, TITLE, ALBUM, TRACK_NO, GENRE
    }

    private Field field;
    private String result;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "['" + field + "':'" + result.trim() + "']";
    }

    /**
     * Convert the the field name from a string to a field enum.
     *
     * @param fieldName
     * @return
     */
    public static Field getFieldByString(String fieldName) {
        if (fieldName.equals("ARTIST")) {
            return Field.ARTIST;
        } else if (fieldName.equals("TITLE")) {
            return Field.TITLE;
        } else if (fieldName.equals("GENRE")) {
            return Field.GENRE;
        } else if (fieldName.equals("ALBUM")) {
            return Field.ALBUM;
        } else if (fieldName.equals("TRACK_NO")) {
            return Field.TRACK_NO;
        }
        return null;
    }
}