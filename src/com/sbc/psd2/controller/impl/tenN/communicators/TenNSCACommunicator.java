package com.sbc.psd2.controller.impl.tenN.communicators;

import com.sbc.common.exception.ApplicationException;
import com.sbc.common.logging.LogManager;
import com.sbc.psd2.controller.AbstractCommunicatorFactory;
import com.sbc.psd2.controller.SCACommunicator;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.coresystem.CoreSystemAccountInfo;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.rest.util.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.HashMap;

public class TenNSCACommunicator implements SCACommunicator {
    @Override
    public void generateOTP(ConsentOp op) throws ApplicationException {
    }

    @Override
    public void generateOTP(BGNCreditTransferOp op) throws ApplicationException {

        LogManager.trace(getClass(), "generateOTP()");

        try{

            URL url = new URL (AbstractCommunicatorFactory.getInstance().getScaCommunicatorEndPoint());

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element signElement = doc.createElement("SIGN");
            doc.appendChild(signElement);

            signElement.setAttribute("username","buts");
            signElement.setAttribute("password","pass1234");

            Element getSignElement = doc.createElement("GETSIGN");
            signElement.appendChild(getSignElement);

            getSignElement.setAttribute("tokenid", op.getPaymentId());
            getSignElement.setAttribute("phone", op.getDebtorPhoneNumber());
            getSignElement.setAttribute("phrase",op.getTransactionFee() + " " + op.getTransactionFeeCurrency());
            getSignElement.setAttribute("description", "PAYMENT");
            getSignElement.setAttribute("referenceid",op.getExtRefID());

            String requestBody = null;

            HttpClient httpClient = new HttpClient(url, "application/json", requestBody);

            String otpResponse = httpClient.doPost(String.class);

        }catch (Exception e) {
            LogManager.log(getClass(), e);
            throw new ApplicationException(ApplicationException.INTERNAL_ERROR,e.getMessage());
        }


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
