package de.roth.jsona.vlc;

import org.apache.commons.lang3.SystemUtils;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class VLCUtils {

    // implement if no vlc path was defined then search for vlc

    public static boolean vlcPathRequired() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return true;
        }

        return false;
    }

    public static boolean autoSetupVLCPath() {
        NativeDiscovery discovery = new NativeDiscovery();
        return discovery.discover();
    }
}
