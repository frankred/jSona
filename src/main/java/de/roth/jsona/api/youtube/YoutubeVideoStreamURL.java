package de.roth.jsona.api.youtube;

public class YoutubeVideoStreamURL {

    public enum StreamType {
        AUDIO, VIDEO, VIDEO_AUDIO
    }

    private String url;
    private String type;
    private int itag;
    private StreamType streamType;
    private String format;
    private String resolution;
    private String sizeRate;
    private String title;

    public YoutubeVideoStreamURL(String url, String itag) {
        this.url = url;
        this.itag = Integer.parseInt(itag);
        this.init();
    }

    public void init() {
        if (itag == 5) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "FLV";
            this.sizeRate = "240p";
            this.resolution = "320x240";
        } else if (itag == 17) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "3GP";
            this.sizeRate = "144p";
            this.resolution = "176x144";
        } else if (itag == 18) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "360p";
            this.resolution = "640x360";
        } else if (itag == 22) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "720p";
            this.resolution = "1280x720";
        } else if (itag == 34) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "FLV";
            this.sizeRate = "360p";
            this.resolution = "640x360";
        } else if (itag == 35) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "FLV";
            this.sizeRate = "480p";
            this.resolution = "854x480";
        } else if (itag == 36) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "3GP";
            this.sizeRate = "240p";
            this.resolution = "320x240";
        } else if (itag == 37) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "1080p";
            this.resolution = "1920x1080";
        } else if (itag == 38) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "3072p";
            this.resolution = "4096x3072";
        } else if (itag == 43) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "360p";
            this.resolution = "640x360";
        } else if (itag == 44) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "854x480";
        } else if (itag == 45) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "720p";
            this.resolution = "1280x720";
        } else if (itag == 46) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "1080p";
            this.resolution = "1920x1080";
        } else if (itag == 82) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "360p";
            this.resolution = "640x360-3D";
        } else if (itag == 83) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "480p";
            this.resolution = "640x480-3D";
        } else if (itag == 84) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "MP4";
            this.sizeRate = "720p";
            this.resolution = "1280x720-3D";
        } else if (itag == 100) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "360p";
            this.resolution = "640x360-3D";
        } else if (itag == 102) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "720p";
            this.resolution = "1280x720-3D";
        } else if (itag == 133) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.sizeRate = "240p";
            this.resolution = "426x240";
        } else if (itag == 134) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.resolution = "640x360";
            this.sizeRate = "360p";
        } else if (itag == 135) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.sizeRate = "480p";
            this.resolution = "854x480";
        } else if (itag == 136) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.sizeRate = "720p";
            this.resolution = "1280x720";
        } else if (itag == 137) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.sizeRate = "1080p";
            this.resolution = "1920x1080";
        } else if (itag == 138) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.resolution = "4096x3072";
            this.sizeRate = "3072p";
        } else if (itag == 139) {
            this.streamType = StreamType.AUDIO;
            this.format = "M4A";
            this.sizeRate = "48k";
        } else if (itag == 140) {
            this.streamType = StreamType.AUDIO;
            this.format = "M4A";
            this.sizeRate = "128k";
        } else if (itag == 141) {
            this.streamType = StreamType.AUDIO;
            this.format = "M4A";
            this.sizeRate = "256k";
        } else if (itag == 160) {
            this.streamType = StreamType.VIDEO;
            this.format = "M4V";
            this.sizeRate = "144p";
            this.resolution = "256x144";
        } else if (itag == 167) {
            this.streamType = StreamType.VIDEO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "640x480";
        } else if (itag == 168) {
            this.streamType = StreamType.VIDEO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "854x480";
        } else if (itag == 169) {
            this.streamType = StreamType.VIDEO;
            this.format = "WEBM";
            this.sizeRate = "720p";
            this.resolution = "1280x720";
        } else if (itag == 170) {
            this.streamType = StreamType.VIDEO;
            this.format = "WEBM";
            this.sizeRate = "1080p";
            this.resolution = "1920x1080";
        } else if (itag == 171) {
            this.streamType = StreamType.AUDIO;
            this.format = "OGG";
            this.sizeRate = "128k";
        } else if (itag == 172) {
            this.streamType = StreamType.AUDIO;
            this.format = "OGG";
            this.sizeRate = "192k";
        } else if (itag == 242) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "240p";
            this.resolution = "360x240";
        } else if (itag == 243) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "360p";
            this.resolution = "480x360";
        } else if (itag == 244) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "640x480";
        } else if (itag == 245) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "640x480";
        } else if (itag == 246) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "640x480";
        } else if (itag == 247) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "480p";
            this.resolution = "720x480";
        } else if (itag == 248) {
            this.streamType = StreamType.VIDEO_AUDIO;
            this.format = "WEBM";
            this.sizeRate = "1080p";
            this.resolution = "1920x1080";
        } else if (itag == 256) {
            this.streamType = StreamType.AUDIO;
            this.format = "M4A";
            this.sizeRate = "192k";
        } else if (itag == 258) {
            this.streamType = StreamType.AUDIO;
            this.format = "M4A";
            this.sizeRate = "192k";
        } else if (itag == 264) {
            this.streamType = StreamType.VIDEO;
            this.sizeRate = "1080p";
            this.resolution = "1920x1080";
            this.format = "M4V";
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getItag() {
        return itag;
    }

    public void setItag(int itag) {
        this.itag = itag;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public void setStreamType(StreamType streamType) {
        this.streamType = streamType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getSizeRate() {
        return sizeRate;
    }

    public void setSizeRate(String sizeRate) {
        this.sizeRate = sizeRate;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "[type=" + this.type + ", itag=" + this.itag + ", url=" + this.url + "]";
    }
}
