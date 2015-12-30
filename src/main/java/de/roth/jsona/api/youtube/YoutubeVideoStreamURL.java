package de.roth.jsona.api.youtube;

public class YoutubeVideoStreamURL {

    private String title;
    private String url;
    private String type;
    private String itag;

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

    public String getItag() {
        return itag;
    }

    public void setItag(String itag) {
        this.itag = itag;
    }

    public String getQuality() {
        if (itag == null) {
            return "unknown";
        }


        if (itag.equals("5"))
            return "normal, 240p, FLV, 320x240";
        else if (itag.equals("17"))
            return "normal, 144p, 3GP, 176x144";
        else if (itag.equals("18"))
            return "normal, 360p, MP4, 640x360";
        else if (itag.equals("22"))
            return "normal, 720p, MP4, 1280x720";
        else if (itag.equals("34"))
            return "normal, 360p, FLV, 640x360";
        else if (itag.equals("35"))
            return "normal, 480p, FLV, 854x480";
        else if (itag.equals("36"))
            return "normal, 240p, 3GP, 320x240";
        else if (itag.equals("37"))
            return "normal, 1080p, MP4, 1920x1080";
        else if (itag.equals("38"))
            return "normal, 3072p, MP4, 4096x3072";
        else if (itag.equals("43"))
            return "normal, 360p, WebM, 640x360";
        else if (itag.equals("44"))
            return "normal, 480p, WebM, 854x480";
        else if (itag.equals("45"))
            return "normal, 720p, WebM, 1280x720";
        else if (itag.equals("46"))
            return "normal, 1080p, WebM, 1920x1080";
        else if (itag.equals("82"))
            return "normal, 360p, MP4, 640x360-3D";
        else if (itag.equals("83"))
            return "normal, 480p, MP4, 640x480-3D";
        else if (itag.equals("84"))
            return "normal, 720p, MP4, 1280x720-3D";
        else if (itag.equals("100"))
            return "normal, 360p, WebM, 640x360-3D";
        else if (itag.equals("102"))
            return "normal, 720p, WebM, 1280x720-3D";

        else if (itag.equals("133"))
            return "video only, 240p, m4v, 426x240";
        else if (itag.equals("134"))
            return "video only, 360p, m4v, 640x360";
        else if (itag.equals("135"))
            return "video only, 480p, m4v, 854x480";
        else if (itag.equals("136"))
            return "video only, 720p, m4v, 1280x720";
        else if (itag.equals("137"))
            return "video only, 1080p, m4v, 1920x1080";
        else if (itag.equals("138"))
            return "video only, 3072p, m4v, 4096x3072";

        else if (itag.equals("139"))
            return "audio only, 48k, m4a";
        else if (itag.equals("140"))
            return "audio only, 128k, m4a";
        else if (itag.equals("141"))
            return "audio only, 256k, m4a";
        else if (itag.equals("160"))
            return "video only, 144p, m4v, 256x144";
        else if (itag.equals("167"))
            return "video only, 480p, webm, 640x480";
        else if (itag.equals("168"))
            return "video only, 480p, webm, 854x480";
        else if (itag.equals("169"))
            return "video only, 720p, webm, 1280x720";
        else if (itag.equals("170"))
            return "video only, 1080p, webm, 1920x1080";
        else if (itag.equals("171"))
            return "audio only,  128k, ogg";
        else if (itag.equals("172"))
            return "audio only, 192k, ogg";

        else if (itag.equals("242"))
            return "normal, 240p, webm, 360x240";
        else if (itag.equals("243"))
            return "normal, 360p, webm, 480x360";
        else if (itag.equals("244"))
            return "normal, 480p, webm, 640x480";
        else if (itag.equals("245"))
            return "normal, 480p, webm, 640x480";
        else if (itag.equals("246"))
            return "normal, 480p, webm, 640x480";
        else if (itag.equals("247"))
            return "normal, 480p, webm, 720x480";
        else if (itag.equals("248"))
            return "normal, 1080p, webm,  1920x1080";

        else if (itag.equals("256"))
            return "audio, 192k, m4a";
        else if (itag.equals("258"))
            return "audio, 320k, m4a";
        else if (itag.equals("264"))
            return "video only, 1080p, m4v, 1920x1080";
        else
            return itag;
    }

    @Override
    public String toString() {
        return "[type=" + this.type + ", itag=" + this.itag + ", quality=" + this.getQuality() + ", url=" + this.url + "]";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
