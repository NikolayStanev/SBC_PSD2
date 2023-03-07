package com.sbc.psd2.data.rest.links;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-7-23
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
public class Link {
  private String href;

  public Link() {
  }

  public Link(String href) {
    this.href = href;
  }

  public String getHref() {
    return href;
  }

  public void setHref(String href) {
    this.href = href;
  }

  @Override
  public String toString() {
    return "Link{" +
            "href='" + href + '\'' +
            '}';
  }
}
