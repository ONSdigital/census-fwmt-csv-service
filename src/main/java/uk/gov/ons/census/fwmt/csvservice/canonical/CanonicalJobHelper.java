package uk.gov.ons.census.fwmt.csvservice.canonical;

import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.Contact;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.CSVRecordDTO;

import java.util.UUID;

public final class CanonicalJobHelper {

  public static CreateFieldWorkerJobRequest createCEJob(CSVRecordDTO csvRecordDTO) {
    Address address = new Address();
    Contact contact = new Contact();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    createJobRequest.setActionType("Create");
    createJobRequest.setCaseId(UUID.fromString(csvRecordDTO.getCaseId()));
    createJobRequest.setCaseReference(csvRecordDTO.getCaseReference());
    createJobRequest.setCaseType("CE");
    createJobRequest.setSurveyType("CE-EST");
    createJobRequest.setEstablishmentType(csvRecordDTO.getEstablishmentType());
    createJobRequest.setMandatoryResource(csvRecordDTO.getMandatoryResource());
    createJobRequest.setCoordinatorId(csvRecordDTO.getCoordinatorId());
    createJobRequest.setCategory("Not applicable");

    contact.setOrganisationName(csvRecordDTO.getOrganisationName());
    createJobRequest.setContact(contact);

    address.setArid(csvRecordDTO.getArid());
    address.setUprn(csvRecordDTO.getUprn());
    address.setLine1(csvRecordDTO.getLine1());
    address.setLine2(csvRecordDTO.getLine2());
    address.setLine3(csvRecordDTO.getLine3());
    address.setTownName(csvRecordDTO.getTownName());
    address.setPostCode(csvRecordDTO.getPostCode());
    address.setOa(csvRecordDTO.getOa());
    address.setLatitude(csvRecordDTO.getLatitude());
    address.setLongitude(csvRecordDTO.getLongitude());
    createJobRequest.setAddress(address);

    createJobRequest.setCeDeliveryRequired(true);
    createJobRequest.setCeCE1Complete(false);
    createJobRequest.setCeExpectedResponses(csvRecordDTO.getCeExpectedCapacity());
    createJobRequest.setCeActualResponses(0);
    createJobRequest.setUua(false);
    createJobRequest.setBlankFormReturned(false);
    createJobRequest.setSai(false);

    return createJobRequest;
  }

  public static CreateFieldWorkerJobRequest createCCSJob(CSVRecordDTO csvRecordDTO) {
    Address address = new Address();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    UUID createCaseId = UUID.randomUUID();

    createJobRequest.setActionType("Create");
    createJobRequest.setCaseId(createCaseId);
    createJobRequest.setCaseReference(createCaseId.toString());
    createJobRequest.setCaseType("CCSPL");
    createJobRequest.setCoordinatorId(csvRecordDTO.getCoordinatorId());
    createJobRequest.setMandatoryResource(csvRecordDTO.getCcsInterviewer());
    createJobRequest.setSurveyType("surveyType");
    createJobRequest.setEstablishmentType("HH");

    address.setPostCode(csvRecordDTO.getPostCode());
    address.setLatitude(csvRecordDTO.getLatitude());
    address.setLongitude(csvRecordDTO.getLongitude());
    address.setOa(csvRecordDTO.getOa());
    createJobRequest.setAddress(address);

    return createJobRequest;
  }
}
