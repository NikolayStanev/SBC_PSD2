package com.sbc.psd2.controller.impl.tenN.communicators;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.config.AppConfig;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.CoreSystemCommunicator;
import com.sbc.psd2.controller.SCACommunicator;
import com.sbc.psd2.data.account.Account;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.rest.util.HttpClient;
import com.sbc.psd2.rest.util.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TenNSCACommunicator implements SCACommunicator {
    @Override
    public void generateOTP(ConsentOp op) throws ApplicationException {

        LogManager.trace(getClass(), "generateOTP()");

        try{

            CoreSystemCommunicator communicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();
            String iban = "";

            for(Map.Entry<String, Account> entry : op.getAccountMap().entrySet()){
                iban = entry.getKey();
            }

            CoreSystemAccountInfo coreSystemAccountInfo = communicator.getAccountDetails(iban);

            String description = "Please authorize consent for accounts: " + Util.genTextForSigning10n(op) + " for " + op.getFrequencyPerDay() + "times a day \n"
                    + "Valid until: " + op.getValidUntil();
            String requestBody = prepareXML(coreSystemAccountInfo.getPhoneNumber(), op.getConsentId(), description, op.getConsentId());

            LogManager.trace(getClass(), "generateOTP for iban: "+ coreSystemAccountInfo.getIban()+ "; phone number: " + coreSystemAccountInfo.getPhoneNumber());

            //TODO remove hack
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            String answer = Util.doPostSync(AppConfig.getInstance().getScaCommunicatorEndPoint(), requestBody, "application/xml");
            LogManager.trace(getClass(), "generateOTP() -> " + answer);

            //TODO remove hack
            if (answer.contains("<ERRORS>")) {
                throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Not valid otp!");
            }

        } catch (ApplicationException e) {
            LogManager.log(getClass(), e);

            throw e;
        } catch (Exception e) {
            LogManager.log(getClass(), e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }

    }

    @Override
    public void generateOTP(BGNCreditTransferOp op) throws ApplicationException {

        LogManager.trace(getClass(), "generateOTP()");

        try{
            String description = "Please authorize payment to " + op.getCreditorName() + " for " + op.getInstructedAmount().getAmount() + " " + op.getInstructedAmount().getCurrency() + " \n"
                                + "Taxes for the payment are: " + op.getTransactionFee() + " " + op.getTransactionFeeCurrency();
            String requestBody = prepareXML(op.getDebtorPhoneNumber(), op.getPaymentId(), description, op.getExtRefID());

            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            String answer = Util.doPostSync(AppConfig.getInstance().getScaCommunicatorEndPoint(), requestBody, "application/xml");
            LogManager.trace(getClass(), "generateOTP() -> " + answer);

            //TODO:remove comment
            if (answer.contains("<ERRORS>")) {
                throw new ApplicationException(ApplicationException.PSU_CREDENTIALS_INVALID, "Not valid otp!");
            }

        } catch (ApplicationException e) {
            LogManager.log(getClass(), e);

            throw e;
        } catch (Exception e) {
            LogManager.log(getClass(), e);

            throw new ApplicationException(ApplicationException.INTERNAL_ERROR, "Internal error!");
        }

    }

    public String prepareXML(String phone, String phrase, String description, String refId) throws ParserConfigurationException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();


        Document doc = docBuilder.newDocument();
        Element signElement = doc.createElement("SIGN");
        doc.appendChild(signElement);

        signElement.setAttribute("username", AppConfig.getInstance().getScaUser());
        signElement.setAttribute("password",AppConfig.getInstance().getScaPassword());

        Element getSignElement = doc.createElement("GETSIGN");
        signElement.appendChild(getSignElement);

//            getSignElement.setAttribute("tokenid", op.getConsentId());
        getSignElement.setAttribute("phone", phone);//"0885741901");
        getSignElement.setAttribute("phrase", phrase);
        getSignElement.setAttribute("description", description);
        getSignElement.setAttribute("referenceid",refId);

        String requestBody = "";

        StringWriter writer = new StringWriter();

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        //String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
        requestBody = writer.getBuffer().toString();

        return requestBody;
    }

    @Override
    public boolean checkOTP(BGNCreditTransferOp op, String otp) throws ApplicationException {
        return false;
    }

    @Override
    public boolean checkOTP(ConsentOp op, String otp) throws ApplicationException {
        return false;
    }
}
