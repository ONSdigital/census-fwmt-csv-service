package uk.gov.ons.census.fwmt.csvservice.canonical;

import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.Contact;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.dto.CEJobListing;

import java.util.UUID;

public final class CanonicalJobHelper {

  private static final String CREATE_ACTION_TYPE = "Create";

  public static CreateFieldWorkerJobRequest createCEJob(CEJobListing CEJobListing) {
    Address address = new Address();
    Contact contact = new Contact();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    createJobRequest.setActionType("Create");
    createJobRequest.setCaseId(UUID.fromString(CEJobListing.getCaseId()));
    createJobRequest.setCaseReference(CEJobListing.getCaseReference());
    createJobRequest.setCaseType("CE");
    createJobRequest.setSurveyType("CE EST");
    createJobRequest.setEstablishmentType(CEJobListing.getEstablishmentType());
    createJobRequest.setMandatoryResource(CEJobListing.getMandatoryResource());
    createJobRequest.setCoordinatorId(CEJobListing.getCoordinatorId());
    createJobRequest.setCategory("Not applicable");

    contact.setOrganisationName(CEJobListing.getOrganisationName());
    createJobRequest.setContact(contact);

    address.setArid(CEJobListing.getArid());
    address.setUprn(CEJobListing.getUprn());
    address.setLine1(CEJobListing.getLine1());
    address.setLine2(CEJobListing.getLine2());
    address.setLine3(CEJobListing.getLine3());
    address.setTownName(CEJobListing.getTownName());
    address.setPostCode(CEJobListing.getPostCode());
    address.setOa(CEJobListing.getOa());
    address.setLatitude(CEJobListing.getLatitude());
    address.setLongitude(CEJobListing.getLongitude());
    createJobRequest.setAddress(address);
    createJobRequest.setGatewayType(CREATE_ACTION_TYPE);

    createJobRequest.setCeDeliveryRequired(true);
    createJobRequest.setCeCE1Complete(false);
    createJobRequest.setCeExpectedResponses(CEJobListing.getCeExpectedCapacity());
    createJobRequest.setCeActualResponses(0);
    createJobRequest.setUua(false);
    createJobRequest.setBlankFormReturned(false);
    createJobRequest.setSai(false);

    return createJobRequest;
  }

  public static CreateFieldWorkerJobRequest createCCSJob(CCSPropertyListing ccsPropertyListing) {
    Address address = new Address();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    UUID createCaseId = UUID.randomUUID();

    createJobRequest.setActionType("Create");
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
