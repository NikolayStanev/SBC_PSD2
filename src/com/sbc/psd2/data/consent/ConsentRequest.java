package com.sbc.psd2.data.consent;

import com.sbc.psd2.data.PSD2RequestCommonData;
import com.sbc.psd2.data.rest.AccountAccess;

import java.util.Date;

public class ConsentRequest extends PSD2RequestCommonData {

    private AccountAccess access;
    private Boolean recurringIndicator;
    private Date validUntil;
    private int frequencyPerDay;

    private Boolean combinedServiceIndicator;


    public ConsentRequest() {
    }

    public AccountAccess getAccess() {
        return access;
    }

    public void setAccess(AccountAccess access) {
        this.access = access;
    }

    public Boolean getRecurringIndicator() {
        return recurringIndicator;
    }

    public void setRecurringIndicator(Boolean recurringIndicator) {
        this.recurringIndicator = recurringIndicator;
    }

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Date validUntil) {
        this.validUntil = validUntil;
    }

    public int getFrequencyPerDay() {
        return frequencyPerDay;
    }

    public void setFrequencyPerDay(int frequencyPerDay) {
        this.frequencyPerDay = frequencyPerDay;
    }

    public Boolean getCombinedServiceIndicator() {
        return combinedServiceIndicator;
    }

    public void setCombinedServiceIndicator(Boolean combinedServiceIndicator) {
        this.combinedServiceIndicator = combinedServiceIndicator;
    }
    //    TODO add string override!
}
