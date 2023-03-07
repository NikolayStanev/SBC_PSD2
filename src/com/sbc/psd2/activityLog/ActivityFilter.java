package com.sbc.psd2.activityLog;

import com.sbc.common.logging.LogManager;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.routing.Filter;


public class ActivityFilter extends Filter {

  public static InheritableThreadLocal<Integer> threadLocalActivityId = new InheritableThreadLocal<>();

  @Override
  protected int beforeHandle(Request _request, Response response) {

    try {
      String httpBody = _request.getEntity().getText();

      _request.setEntity(new StringRepresentation(httpBody, MediaType.APPLICATION_JSON));

      int activityId = ActivityDAO.createActivity(_request);
      threadLocalActivityId.set(activityId);

      return CONTINUE;

    } catch (Throwable e) {
      LogManager.trace(getClass(), e.getClass().getName());

      Status newStatus = new Status(500, "Internal server error!");

      response.setStatus(newStatus);
      return STOP;
    }

  }


  @Override
  protected void afterHandle(Request request, Response response) {
    super.afterHandle(request, response);

    try {
      Representation responseRep = response.getEntity();
      String responseBody = responseRep.getText();

      String errorFlag = "N";
      if (response.getStatus().getCode() >= 500) {
        errorFlag = "Y";
      }

      ActivityDAO.updateActivity(threadLocalActivityId.get(), responseBody, errorFlag);

    } catch (Throwable t) {
      LogManager.trace(getClass(), t.getClass().getName());
    }
  }


}
