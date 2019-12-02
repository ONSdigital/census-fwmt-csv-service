package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;

import java.util.UUID;

public final class CCSCanonicalBuilder {

  private static final String CREATE_ACTION_TYPE = "Create";

  public static CreateFieldWorkerJobRequest createCCSJob(CCSPropertyListing ccsPropertyListing) {
    Address address = new Address();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    UUID createCaseId = UUID.randomUUID();

    createJobRequest.setActionType(CREATE_ACTION_TYPE);
    createJobRequest.setCaseId(createCaseId);
    createJobRequest.setCaseReference(createCaseId.toString());
    createJobRequest.setCaseType("CCSPL");
    createJobRequest.setCoordinatorId(ccsPropertyListing.getCoordinatorId());
    createJobRequest.setMandatoryResource(ccsPropertyListing.getCcsInterviewer());
    createJobRequest.setSurveyType("CCS PL");
    createJobRequest.setEstablishmentType("HH");
    createJobRequest.setGatewayType(CREATE_ACTION_TYPE);
    createJobRequest.setCategory("Not applicable");

    address.setPostCode(ccsPropertyListing.getPostCode());
    address.setLatitude(ccsPropertyListing.getLatitude());
    address.setLongitude(ccsPropertyListing.getLongitude());
    address.setOa(ccsPropertyListing.getOa());
    createJobRequest.setAddress(address);

    return createJobRequest;
  }
}
