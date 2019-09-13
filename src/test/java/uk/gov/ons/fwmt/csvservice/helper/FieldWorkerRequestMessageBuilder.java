package uk.gov.ons.fwmt.csvservice.helper;

import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;

import java.math.BigDecimal;
import java.util.UUID;

public class FieldWorkerRequestMessageBuilder {

  public CreateFieldWorkerJobRequest buildCreateFieldWorkerJobRequest() {
    CreateFieldWorkerJobRequest fwmtCreateJobRequest = new CreateFieldWorkerJobRequest();

    fwmtCreateJobRequest.setCaseId(UUID.fromString("edf0e64f-04a7-4c1c-bd03-6fbd69feed2e"));

    Address address = new Address();
    address.setLatitude(BigDecimal.valueOf(1000.00));
    address.setLongitude(BigDecimal.valueOf(1000.00));
    address.setLine1("testLine1");
    address.setLine2("testLine2");
    address.setLine3("testLine3");
    address.setLine4("testLine4");
    address.setPostCode("testPostCode");
    address.setTownName("testTownName");
    fwmtCreateJobRequest.setActionType("create");
    fwmtCreateJobRequest.setGatewayType("Create");
    fwmtCreateJobRequest.setAddress(address);
    fwmtCreateJobRequest.setSurveyType("testSurveyType");

    return fwmtCreateJobRequest;
  }
}
