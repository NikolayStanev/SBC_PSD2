package com.sbc.psd2.data.creditTransfer;

import com.sbc.psd2.data.rest.links.BaseLinks;
import com.sbc.psd2.data.rest.links.Link;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-26
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class GetCreditTransferLinks extends BaseLinks {
  private Link authoriseTransaction;

  public GetCreditTransferLinks() {

  }

  public GetCreditTransferLinks(String selfHref, String statusHref, String authHref) {
    super(selfHref, statusHref);

    authoriseTransaction = new Link(authHref);
  }

  public Link getAuthoriseTransaction() {
    return authoriseTransaction;
  }

  public void setAuthoriseTransaction(Link authoriseTransaction) {
    this.authoriseTransaction = authoriseTransaction;
  }
}
