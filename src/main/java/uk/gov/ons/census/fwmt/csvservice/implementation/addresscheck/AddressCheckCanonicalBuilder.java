package uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck;

import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;

import java.util.UUID;

public final class AddressCheckCanonicalBuilder {

  private static final String CREATE_ACTION_TYPE = "Create";

  public static CreateFieldWorkerJobRequest createAddressCheckJob(AddressCheckListing addressCheckListing) {
    Address address = new Address();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    UUID caseId = UUID.randomUUID();

    createJobRequest.setActionType(CREATE_ACTION_TYPE);
    createJobRequest.setCaseId(caseId);
    createJobRequest.setCaseReference(addressCheckListing.getCaseReference());
    createJobRequest.setEstablishmentType("Residential");
    createJobRequest.setSurveyType("AC");
    createJobRequest.setCaseType("AC");
    createJobRequest.setDescription(addressCheckListing.getGuidancePrompt());
    createJobRequest.setSpecialInstructions(addressCheckListing.getAdditionalInformation());
    createJobRequest.setCoordinatorId("look up on postcode");
    createJobRequest.setUua(false);
    createJobRequest.setSai(false);

    address.setLine1(addressCheckListing.getLine1());
    address.setLine2(addressCheckListing.getLine2());
    address.setLine3(addressCheckListing.getLine3());
    address.setTownName(addressCheckListing.getTownName());
    address.setLatitude(addressCheckListing.getLatitude());
    address.setLongitude(addressCheckListing.getLongitude());
    address.setOa("look up on postcode");

    createJobRequest.setAddress(address);

    return createJobRequest;
  }
}
