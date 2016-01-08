package de.roth.jsona.model;

import de.roth.jsona.information.Link;

import java.util.List;

public interface MainViewViewable {

    public String getMainImage();
    public String getHeading();
    public String getSubheading();
    public String getText();
    public List<Link> getSubLinks();

}
