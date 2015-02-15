package de.roth.jsona.api.musicbrainz;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * XML-Parser class to parse the musicbrainz api response.
 *
 * @author Frank Roth
 */
public class MusicbrainzXMLv2ContentHandler implements ContentHandler {

    private ArrayList<MusicbrainzSearchResult> mbids;

    public MusicbrainzXMLv2ContentHandler(ArrayList<MusicbrainzSearchResult> mbids) {
        this.mbids = mbids;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals("artist")) {
            MusicbrainzSearchResult s = new MusicbrainzSearchResult();
            s.setMbid(atts.getValue("id"));
            s.setScore(Integer.parseInt(atts.getValue("ext:score")));
            this.mbids.add(s);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // TODO Auto-generated method stub

    }

}
