package com.sbc.psd2.data.coresystem;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 19-8-20
 * Time: 16:25
 * To change this template use File | Settings | File Templates.
 */
public interface CoreSystemAccountInfo {


  public String getIban();

  public void setIban(String iban);

  public String getCurrency();

  public void setCurrency(String currency);

  public String getProduct();

  public void setProduct(String product);

  public String getCashAccountType();

  public void setCashAccountType(String cashAccountType);

  public String getName();

  public void setName(String name);

  public String getBranch();

  public void setBranch(String branch);

  public String getPhoneNumber();
}
