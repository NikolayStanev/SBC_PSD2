package com.sbc.psd2.controller;

import com.sbc.common.exception.ApplicationException;
import com.sbc.psd2.data.consent.ConsentOp;
import com.sbc.psd2.data.consent.dao.ConsentOpDAO;
import com.sbc.psd2.data.creditTransfer.BGNCreditTransferOp;
import com.sbc.psd2.data.creditTransfer.dao.BGNCreditTransferOpDAO;
import com.sbc.psd2.data.statuses.ConsentStatuses;
import com.sbc.psd2.data.statuses.TransactionStatuses;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskExecutor {
  private ExecutorService executor;

  public static final TaskExecutor INSTANCE = new TaskExecutor();
  private TaskExecutor() {
    executor = Executors.newFixedThreadPool(10);
  }

  public static String bookTransaction(BGNCreditTransferOp op) throws ApplicationException {
    // call core system
    String psd2Status = TransactionStatuses.ACCEPT_SETTLEMENT_IN_PROCESS;

    try {
      CoreSystemCommunicator csCommunicator = AbstractCommunicatorFactory.getInstance().getCoreSystemCommunicator();

      String ref = csCommunicator.makeTransaction(op);
      op.setExtRefID(ref);
    } catch (ApplicationException e) {
      // todo handle it, maybe this is the right impl??!
      psd2Status = TransactionStatuses.REJECTED;
    } catch (Exception e) {
      // todo handle it, maybe this is the right impl??!
      psd2Status = TransactionStatuses.REJECTED;
    }

    try {
      op.setTransactionStatus(psd2Status);

      BGNCreditTransferOpDAO.update(op);
    } catch (Exception ex) {
      // todo: what will happen in this case??!
    }

    return psd2Status;

  }

  public void consentAuth (ConsentOp op) {
    String consentStatus = ConsentStatuses.VALID;

    try {

      SCACommunicator scaCommunicator = AbstractCommunicatorFactory.getInstance().getScaCommunicator();

      scaCommunicator.generateOTP(op);

    } catch (ApplicationException e) {
      // todo handle it, maybe this is the right impl??!
      consentStatus = ConsentStatuses.REJECTED;
    } catch (Exception e) {
      // todo handle it, maybe this is the right impl??!
      consentStatus = ConsentStatuses.REJECTED;
    }

    try {
      op.setConsentStatus(consentStatus);

      ConsentOpDAO.updateConsentStatus(op.getDbId(), consentStatus);
    } catch (Exception ex) {
      // todo: what will happen in this case??!
    }
  }



  public final void bookTransactionAsync(BGNCreditTransferOp op) {
    // call core system
    executor.execute(() -> {
      bookTransaction(op);
    });
  }

  public final void startConsentAuth(ConsentOp op) {
    // call core system
    executor.execute(() -> {
      consentAuth(op);
    });
  }

  protected void finalize() {
    stop();
  }
  public void stop() {
    System.out.println("Task executor finalize!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    executor.shutdownNow();
  }

}
