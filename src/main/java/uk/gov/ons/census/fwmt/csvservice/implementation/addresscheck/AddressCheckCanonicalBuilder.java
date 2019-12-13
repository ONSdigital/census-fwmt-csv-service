package uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck;

import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheck;

import java.util.UUID;

public final class AddressCheckCanonicalBuilder {

  private static final String CREATE_ACTION_TYPE = "Create";

  public static CreateFieldWorkerJobRequest createAddressCheckJob(AddressCheck addressCheck) {
    Address address = new Address();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    UUID caseId = UUID.randomUUID();

    createJobRequest.setActionType(CREATE_ACTION_TYPE);
    createJobRequest.setCaseId(caseId);
    createJobRequest.setCaseReference(addressCheck.getCaseReference());
    createJobRequest.setEstablishmentType("Residential");
    createJobRequest.setSurveyType("AC");
    createJobRequest.setCaseType("AC");
    createJobRequest.setDescription(addressCheck.getGuidancePrompt());
    createJobRequest.setSpecialInstructions(addressCheck.getAdditionalInformation());
    createJobRequest.setCoordinatorId("look up on postcode");
    createJobRequest.setUua(false);
    createJobRequest.setSai(false);

    address.setLine1(addressCheck.getLine1());
    address.setLine2(addressCheck.getLine2());
    address.setLine3(addressCheck.getLine3());
    address.setLatitude(addressCheck.getLatitude());
    address.setLongitude(addressCheck.getLongitude());
    address.setOa("look up on postcode");

    createJobRequest.setAddress(address);

    return createJobRequest;
  }
}
