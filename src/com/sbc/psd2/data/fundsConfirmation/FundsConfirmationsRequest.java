package com.sbc.psd2.data.fundsConfirmation;

import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.rest.Amount;

public class FundsConfirmationsRequest {

    private String cardNumber;
    private Account account;
    private Amount instructedAmount;

    public FundsConfirmationsRequest() {
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Amount getInstructedAmount() {
        return instructedAmount;
    }

    public void setInstructedAmount(Amount instructedAmount) {
        this.instructedAmount = instructedAmount;
    }
}
