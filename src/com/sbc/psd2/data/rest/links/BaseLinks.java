package com.sbc.psd2.data.rest.links;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-26
 * Time: 17:03
 * To change this template use File | Settings | File Templates.
 */
public class BaseLinks {
  private Link self;
  private Link status;


  public BaseLinks() {
  }

  public BaseLinks(Link self, Link status) {
    this.self = self;
    this.status = status;
  }

  public BaseLinks(String _self, String _status) {
    this.self = new Link(_self);
    this.status = new Link(_status);
  }

  public Link getSelf() {
    return self;
  }

  public void setSelf(Link self) {
    this.self = self;
  }

  public Link getStatus() {
    return status;
  }

  public void setStatus(Link status) {
    this.status = status;
  }


}
