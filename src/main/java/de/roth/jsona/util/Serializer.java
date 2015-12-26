package de.roth.jsona.util;

import java.io.*;

/**
 * The SerializeManager saves or loads Java objects to a or from a file.
 *
 * @author Frank Roth
 */
public class Serializer {

    public static Object load(String file) {
        File data = new File(file);
        if (data.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(data);
                ObjectInputStream in = new ObjectInputStream(fis);
                Object o = in.readObject();
                in.close();
                return o;
            } catch (FileNotFoundException e) {
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void save(String file, Object o) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            @SuppressWarnings("resource")
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(o);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
