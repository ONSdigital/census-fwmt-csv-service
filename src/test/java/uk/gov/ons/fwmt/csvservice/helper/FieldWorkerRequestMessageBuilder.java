package uk.gov.ons.fwmt.csvservice.helper;

import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;

import java.util.UUID;

public class FieldWorkerRequestMessageBuilder {

  public CreateFieldWorkerJobRequest buildCreateFieldWorkerJobRequestCCS() {
    CreateFieldWorkerJobRequest fwmtCreateJobRequest = new CreateFieldWorkerJobRequest();

    fwmtCreateJobRequest.setCaseId(UUID.fromString("2f1ea0fd-18b1-4786-b1f7-3e9a79ed1a52"));
    fwmtCreateJobRequest.setCaseType("CCSPL");

    return fwmtCreateJobRequest;
  }
  public CreateFieldWorkerJobRequest buildCreateFieldWorkerJobRequestCE() {
    CreateFieldWorkerJobRequest fwmtCreateJobRequest = new CreateFieldWorkerJobRequest();

    fwmtCreateJobRequest.setCaseId(UUID.fromString("2f1ea0fd-18b1-4786-b1f7-3e9a79ed1a52"));
    fwmtCreateJobRequest.setCaseType("CE");

    return fwmtCreateJobRequest;
  }
}
