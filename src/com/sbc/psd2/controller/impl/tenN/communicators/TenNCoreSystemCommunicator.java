package com.sbc.psd2.controller.impl.tenN.communicators;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.controller.SCACommunicator;
import com.sbc.psd2.controller.UserFilter;
import com.sbc.psd2.data.UserInfo;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import com.sbc.psd2.data.rest.*;
import com.sbc.psd2.data.tenN.TenNCoreTransactionDetails;
import com.sbc.psd2.data.tenN.TenNTaxes;
import com.sbc.psd2.data.tenN.pojo.*;
import com.sbc.psd2.data.tenN.TenNCoreAccount;
import com.sbc.psd2.data.tenN.TenNCoreIndividual;
import com.sbc.psd2.rest.util.HttpClient;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TenNCoreSystemCommunicator implements CoreSystemCommunicator {

    private final String getTransactionStatusUrl = "OpenBanking/transaction-details";

    private final String makeTransactionUrl = "OpenBanking/money-transfer-to-3rd-party-iban";
    private final String getTexes = "IndividualPayment/{id}/get-money-transfer-charge";

    private final String getAccountsUrl = "OpenBanking/individual";

    private final String getAccountDetailsUrl = "OpenBanking/individual/account";

    private final String getAccountBalancesUrl = "OpenBanking/individual/account";

    private final String readTransactionsDetailsUrl = "OpenBanking/transaction-details";

    private final String readTransactionsListUrl = "";

    private final String confirmFundsUrl = "";

    @Override
    public String getTransactionStatus(String refID) throws ApplicationException {
        LogManager.trace(getClass(), "getTransactionStatus()", refID);

        try{

            URL getAllAccountsURL = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + getTransactionStatusUrl);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", TenNIdentityManagementCommunicator.tokenType + " " + TenNIdentityManagementCommunicator.apiToken);

            String customerRef = UserFilter.getUserInfo().getUserID();

            GetTransactionDetailsPoJo requestBody = new GetTransactionDetailsPoJo(refID, customerRef);

            HttpClient httpClient = new HttpClient(getAllAccountsURL, "application/json", headers);
            httpClient.setRequestBody(requestBody);

            TenNCoreTransactionDetails transactionDetails = httpClient.doGet(TenNCoreTransactionDetails.class);

            LogManager.trace(getClass(), "getTransactionStatus() returns: " + transactionDetails.getTransactionStatus() + " status for ref " + refID);

            return transactionDetails.getTransactionStatus();

        }catch (Exception e) {
            LogManager.log(getClass(), e);
            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }
    }

    @Override
    public String makeTransaction(BGNCreditTransferOp op) throws ApplicationException {


        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", TenNIdentityManagementCommunicator.tokenType + " " + TenNIdentityManagementCommunicator.apiToken);
            //Get accDetails to obtain customer ref.
            TenNCoreAccount tenNCoreAccount = getAccountDetails(op.getDebtorAccount().getIban().getIban());

            //Call for taxes
            URL getTaxesURL = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + makeTransactionUrl.replace("{id}", tenNCoreAccount.getCustomerNumber()));
            GetTaxesPojo getTaxesPojo = new GetTaxesPojo(tenNCoreAccount.getCustomerAccountNumber(),op.getInstructedAmount().getContent(), op.getPaymentType().getServiceLevel());

            HttpClient http = new HttpClient(getTaxesURL,"application/json", headers);
            http.setRequestBody(getTaxesPojo);

            TenNTaxes taxes = http.doGet(TenNTaxes.class);

            //TODO: send taxes to SCA!
            SCACommunicator communicator = AbstractCommunicatorFactory.getInstance().getScaCommunicator();
            communicator.generateOTP(op);

            //Make the transaction
            MakeTransactionPojo requestBody = new MakeTransactionPojo();

            //TODO: check if properties match!
            requestBody.setSourceCustomerNumber(tenNCoreAccount.getCustomerNumber());
            requestBody.setSourceCustomerAccount(op.getDebtorAccount().getIban().getIban());
            requestBody.setBeneficiaryCustomerName(op.getCreditorName());
            requestBody.setBeneficiaryCustomerAccount(op.getCreditorAccount().getIban().getIban());
            requestBody.setAmount(op.getInstructedAmount().getContent());
            requestBody.setProductCode(op.getPaymentType().getServiceLevel());
            requestBody.setNotes(op.getRemittanceInformationUnstructured());

            URL makeTransaction = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + makeTransactionUrl);

            HttpClient httpClient = new HttpClient(makeTransaction, "application/json", headers);
            httpClient.setRequestBody(requestBody);

            String transactionRef = httpClient.doPost(String.class);

            op.setCustomerNumber(tenNCoreAccount.getCustomerNumber());
            BGNCreditTransferOpDAO.update(op);

            return transactionRef;
        }catch (Exception e) {
            LogManager.log(getClass(), e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }

    }

    @Override
    public ArrayList<CoreSystemAccountInfo> getAccounts(UserInfo userInfo) throws ApplicationException {
        LogManager.trace(getClass(), "getAccounts()", userInfo.toString());

        try {

            URL getAllAccountsURL = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + getAccountsUrl);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", TenNIdentityManagementCommunicator.tokenType+ " " + TenNIdentityManagementCommunicator.apiToken);

            GetAccountsPoJo requestBody = new GetAccountsPoJo(userInfo.getUserID());

            HttpClient httpClient = new HttpClient(getAllAccountsURL, "application/json", headers);
            httpClient.setRequestBody(requestBody);

            TenNCoreIndividual tenNCoreIndividual = httpClient.doPost(TenNCoreIndividual.class);
            ArrayList<CoreSystemAccountInfo> accounts = new ArrayList<>(tenNCoreIndividual.getAccounts());

            LogManager.trace(getClass(), "getAccounts() returns: " + accounts.size() + " accounts for " + userInfo.getUserID());

            return accounts;

        }catch (Exception e) {
            LogManager.log(getClass(), e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }
    }

    @Override
    public TenNCoreAccount getAccountDetails(String iban) throws ApplicationException {
        LogManager.trace(getClass(), "getAccountDetails()", iban);

        try{

            URL getAllAccountsURL = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + getAccountDetailsUrl);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", TenNIdentityManagementCommunicator.tokenType + " " + TenNIdentityManagementCommunicator.apiToken);

            GetAccountDetailsPoJo requestBody = new GetAccountDetailsPoJo(iban);

            HttpClient httpClient = new HttpClient(getAllAccountsURL, "application/json", headers);
            httpClient.setRequestBody(requestBody);

            TenNCoreAccount coreAccount = httpClient.doPost(TenNCoreAccount.class);

            LogManager.trace(getClass(), "getAccountDetails() returns: " + coreAccount.getIban());

            return coreAccount;

        }catch (Exception e) {
            LogManager.log(getClass(), e);
            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }
    }

    @Override
    public ArrayList<Balance> getAccountBalances(String iban) throws ApplicationException {
        LogManager.trace(getClass(), "getAccountBalances()", iban);

        ArrayList<Balance> balances = new ArrayList<>();

        try{

            URL getAllAccountsURL = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + getAccountBalancesUrl);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", TenNIdentityManagementCommunicator.tokenType + " " + TenNIdentityManagementCommunicator.apiToken);


            GetAccountDetailsPoJo requestBody = new GetAccountDetailsPoJo(iban);

            HttpClient httpClient = new HttpClient(getAllAccountsURL, "application/json", headers);
            httpClient.setRequestBody(requestBody);

            TenNCoreAccount coreAccount = httpClient.doPost(TenNCoreAccount.class);

            balances.add(new Balance(BalanceTypes.AUTHORIZED,new Amount(coreAccount.getCurrency(),new BigDecimal(coreAccount.getCurrentBalance()))));

            LogManager.trace(getClass(), "getAccountDetails() returns: " + coreAccount.getIban());

            return balances;

        }catch (Exception e) {
            LogManager.log(getClass(), e);
            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }
    }

    @Override
    public Transactions readTransactionsDetails(String transactionId) throws ApplicationException {
        LogManager.trace(getClass(), "readTransactionsDetails()", transactionId);

        try{

            URL getAllAccountsURL = new URL(AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicatorEndPoint() + readTransactionsDetailsUrl);

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Authorization", TenNIdentityManagementCommunicator.tokenType + " " + TenNIdentityManagementCommunicator.apiToken);

            String customerRef = UserFilter.getUserInfo().getUserID();

            GetTransactionDetailsPoJo requestBody = new GetTransactionDetailsPoJo(transactionId, customerRef);

            HttpClient httpClient = new HttpClient(getAllAccountsURL, "application/json", headers);
            httpClient.setRequestBody(requestBody);

            TenNCoreTransactionDetails transactionDetails = httpClient.doGet(TenNCoreTransactionDetails.class);

            Transactions transactions = new Transactions();
            transactions.setTransactionId(transactionId);
            transactions.setBookingDate(new Date(transactionDetails.getDateTime()));
            transactions.setTransactionAmount(new Amount(transactionDetails.getCurrency(),new BigDecimal(transactionDetails.getTotalDebit())));
            transactions.setCreditorAccount(new AccountReference(transactionDetails.getBeneficiaryAccountNumber()));
            transactions.setCreditorName(transactionDetails.getBeneficiaryName());
            transactions.setDebtorAccount(new AccountReference(transactionDetails.getSourceAccountNumber()));
            transactions.setDebtorName(transactionDetails.getSourceName());
            transactions.setMandateId(transactionDetails.getNetwork());

            LogManager.trace(getClass(), "readTransactionsDetails() returns: " + transactionDetails);



            return transactions;

        }catch (Exception e) {
            LogManager.log(getClass(), e);
            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }
    }

    @Override
    public ReadTransactionsListResponse readTransactionsList(String accountId, Date dateFrom, Date dateTo, String bookingStatus) throws ApplicationException {
        return null;
    }

    @Override
    public boolean confirmFunds(String accIban, BigDecimal amount, String currency) throws ApplicationException {
        return false;
    }

    @Override
    public void validateIBANs(String iban, UserInfo userInfo) throws ApplicationException {

    }

    @Override
    public void validateIBANs(HashMap<String, Account> accountMap, UserInfo userInfo) throws ApplicationException {

    }


}
