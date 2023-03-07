package com.sbc.psd2.controller.impl.mb.util;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.mb.MbCoreSystemAccountInfo;
import com.sbc.psd2.data.rest.*;
import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class XmlParserMBIB {

    private static final String DOC_ID_MARKER = "<DOCUMENT id=\"";

    public static ArrayList<Balance> parseBalances (String xml) {
        ArrayList<Balance> list = new ArrayList<>();

        String[] line = xml.split("<ACCOUNT");

        line[1].trim();
        int endIndex = line[1].indexOf(">");
        String accData = line[1].substring(0, endIndex);
        String[] attPairs = accData.split(" ");

        String key = "";
        String value = "";

        String currency = "";

        for(String curr : attPairs) {

            String[] keyValue = curr.split("=");
            if (keyValue.length > 1) {
                key = keyValue[0];
                value = keyValue[1].replace("\"", "");
                switch (key) {
                    case "curr": {
                        currency = value;
                        break;
                    }
                    case "openingbalance": {
                        value = value.replace(",", "");
                        Balance balance = new Balance(BalanceTypes.OPENING_BOOKED,new Amount(currency,new BigDecimal(value)));
                        list.add(balance);
                        break;
                    }
                    case "currbalance": {
                        value = value.replace(",", "");
                        Balance balance = new Balance(BalanceTypes.EXPECTED,new Amount(currency,new BigDecimal(value)));
                        list.add(balance);
                        break;
                    }
                    case "avlbbalance": {
                        value = value.replace(",", "");
                        Balance balance = new Balance(BalanceTypes.AUTHORIZED,new Amount(currency,new BigDecimal(value)));
                        list.add(balance);
                        break;
                    }
                }
            }
        }
        return list;
    }

    public static ArrayList<CoreSystemAccountInfo> parseAccounts(String xml) throws ApplicationException {
        ArrayList<CoreSystemAccountInfo> list = new ArrayList<>();

        String[] lines = xml.split("<ACCOUNT");

        boolean isAppending = false;

        String key = "";
        String value = "";

        for (String line : lines) {

            MbCoreSystemAccountInfo account = new MbCoreSystemAccountInfo();
            line = line.trim();

            if (line.startsWith("id=")) {

                int index = line.indexOf(">");
                String accData = line.substring(0, index);
                String[] attPairs = accData.split(" ");

                for (String pair : attPairs) {
                    String[] keyValue = pair.split("=");

                    if (keyValue.length == 1) {
                        value = value + " " + keyValue[0];
                        isAppending = true;

                    }
                    if (isAppending) {
                        value = value.replace("\"", "");

                    } else {
                        key = keyValue[0];
                        value = keyValue[1].replace("\"", "");

                    }
                    isAppending = false;
                    switch (key) {
                        case "iban": {
                            account.setIban(value);
                            break;
                        }
                        case "curr": {
                            account.setCurrency(value);
                            break;
                        }
                        case "subType": {
                            account.setCashAccountType(value);
                            break;
                        }
                        case "alias": {
                            account.setName(value);
                            break;
                        }
                    }

                }
                list.add(account);
            }
        }
        return list;
    }

    public static MbCoreSystemAccountInfo parseAccount(String xml) throws ApplicationException {

        MbCoreSystemAccountInfo account = new MbCoreSystemAccountInfo();
        String accDetails = "";

        int marker = xml.indexOf("<ACCOUNT ") + 9;
        if (marker >= 0) {
            accDetails = xml.substring(marker, xml.indexOf("/>", marker));
        }

        String key = "";
        String value = "";

        accDetails = accDetails.trim();

        String[] attPairs = accDetails.split("\" ");

        for (String pair : attPairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length > 1) {

                key = keyValue[0];
                value = keyValue[1].replace("\"", "");

                switch (key) {
                    case "iban": {
                        account.setIban(value);
                        break;
                    }
                    case "curr": {
                        account.setCurrency(value);
                        break;
                    }
                    case "subtype": {
                        account.setCashAccountType(value);
                        break;
                    }
                    case "alias": {
                        account.setName(value);
                        break;
                    }
                }
            }
        }
        return account;
    }

    public static Transactions parseTransactionDetails(String xml) throws ApplicationException {
        String[] lines = xml.split("<DOCDETAILS");
        String line = lines[1];

        String key = "";
        String value = "";

        Transactions transactionDetails = new Transactions();

        line = line.replace("><DOCUMENT", "");
        int index = 0;

        if (line.contains("<LINE")) {
            index = line.indexOf(">");
        } else {
            line = line.replace("/><SIGNER", "");
            index = line.indexOf("/>");
        }

        String transactionData = line.substring(0, index);
        String[] attPairs = transactionData.split("\" ");
        Amount amount = new Amount();

        for (String pair : attPairs) {
            String[] keyValue = pair.split("=");

            if (keyValue.length > 1) {
                key = keyValue[0];
                value = keyValue[1].replace("\"", "");

                switch (key) {
                    case "id": {
                        transactionDetails.setTransactionId(value);
                        break;
                    }
                    case "namekt": {
                        transactionDetails.setCreditorName(value);
                    }
                    case "ibankt": {
                        transactionDetails.setCreditorAccount(new AccountReference(value));
                        break;
                    }
                    case "namedt": {
                        transactionDetails.setDebtorName(value);
                    }
                    case "ibandt": {
                        transactionDetails.setDebtorAccount(new AccountReference(value));
                        break;
                    }
                    case "amount": {
                        amount.setContent(new BigDecimal(value.replace(",", "")));
                        transactionDetails.setTransactionAmount(amount);
                        break;
                    }
                    case "currdt": {
                        amount.setCurrency(value);
                        transactionDetails.setTransactionAmount(amount);
                        break;
                    }
                    case  "system": {
                        transactionDetails.setMandateId(value);
                        break;
                    }
                    case "datesign": {
                        try {
                            Date bookingDate = new SimpleDateFormat("dd-MM-yyyy").parse(value);
                            transactionDetails.setBookingDate(bookingDate);
                        } catch (Exception e) {
                            throw new ApplicationException(e.getMessage());
                        }
                        break;
                    }
                }
            }
        }
        transactionDetails.setTransactionAmount(amount);
        return transactionDetails;
    }

    public static ReadTransactionsListResponse parseTransactionsList (String xml, String bookingStatus){
        ReadTransactionsListResponse readTransactionsListResponse = new ReadTransactionsListResponse();

        AccountReport accountReport = new AccountReport();
        ArrayList<Transactions> booked = new ArrayList<>();
        ArrayList<Transactions> pending = new ArrayList<>();

        String[] lines = xml.split("<DOCSENT ");


        String key = "";
        String value = "";
        String status = "";

        lines = ArrayUtils.removeElement(lines, lines[0]);

        for (String line : lines) {
            line = line.replace(">\n      <DOCUMENT", "");
            line = line.trim();

            Transactions transactionDetails = new Transactions();
            Amount amount = new Amount();
            int index = 0;

            if (line.contains("<LINE")) {
                index = line.indexOf(">");
            } else {
                index = line.indexOf("/>");
            }
            String accData = line.substring(0, index);
            String[] attPairs = accData.split("\" ");

            for (String pair : attPairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length > 1) {
                    key = keyValue[0];
                    value = keyValue[1].replace("\"", "");

                    switch (key) {
                        case "status": {
                            status = value;
                            break;
                        }
                        case "id": {
                            transactionDetails.setTransactionId(value);
                            break;
                        }
                        case "namedt": {
                            transactionDetails.setDebtorName(value);
                            break;
                        }
                        case "ibandt": {
                            transactionDetails.setDebtorAccount(new AccountReference(value));
                            break;
                        }
                        case "namekt": {
                            transactionDetails.setCreditorName(value);
                            break;
                        }
                        case "ibankt": {
                            transactionDetails.setCreditorAccount(new AccountReference(value));
                            break;
                        }
                        case "system": {
                            transactionDetails.setMandateId(value);
                            break;
                        }
                        case "datetime": {
                            try {
                                Date bookingDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(value);
                                transactionDetails.setBookingDate(bookingDate);
                            } catch (Exception e) {
                                throw new ApplicationException(e.getMessage());
                            }
                            break;
                        }
                        case "amount": {
                            amount.setContent(new BigDecimal(value.replace(",", "")));
                            transactionDetails.setTransactionAmount(amount);
                            break;
                        }
                        case "currdt": {
                            amount.setCurrency(value);
                            transactionDetails.setTransactionAmount(amount);
                            break;
                        }
                    }
                }
            }
            if (status.equals("Posted")) {
                booked.add(transactionDetails);
            }else {
                pending.add(transactionDetails);
                }
        }
        if (bookingStatus.equals("booked")) {
            accountReport.setBooked(booked);
        }else if (bookingStatus.equals("pending")){
            accountReport.setPending(pending);
        }else {
            accountReport.setBooked(booked);
            accountReport.setPending(pending);
        }
        readTransactionsListResponse.setTransactions(accountReport);

        return readTransactionsListResponse;
    }

    public static String parseRef(String xml) {
        int index = xml.indexOf(DOC_ID_MARKER);

        String ref = null;
        if (index >= 0) {
            int index2 = xml.indexOf("\"", index + DOC_ID_MARKER.length() + 1);

            if (index2 >= 0) {
                ref = xml.substring(index + DOC_ID_MARKER.length(), index2);
            }
        }

        return ref;
    }

    public static String parseTransactionStatus(String xml) {
        String marker = "statusid=\"";

        int index = xml.indexOf(marker);

        String status = null;
        if (index >= 0) {
            int index2 = xml.indexOf("\"", index + marker.length() + 1);

            if (index2 >= 0) {
                status = xml.substring(index + marker.length(), index2);
            }
        }

        return status;
    }
}
