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
    createJobRequest.setEstablishmentType(csvRecordDTO.getEstablishmentType());
    createJobRequest.setMandatoryResource(csvRecordDTO.getMandatoryResource());
    createJobRequest.setCoordinatorId(csvRecordDTO.getCoordinatorId());
    createJobRequest.setCategory("Not applicable");

    contact.setForename(csvRecordDTO.getName());
    contact.setOrganisationName(csvRecordDTO.getOrganisationName());
    createJobRequest.setContact(contact);

    address.setUprn(csvRecordDTO.getUprn());
    address.setLine1(csvRecordDTO.getLine1());
    address.setLine2(csvRecordDTO.getLine2());
    address.setLine3(csvRecordDTO.getLine3());
    address.setTownName(csvRecordDTO.getTownName());
    address.setPostCode(csvRecordDTO.getPostCode());
    address.setOa(csvRecordDTO.getOa());
    address.setLatitude(csvRecordDTO.getLattitude());
    address.setLongitude(csvRecordDTO.getLongitude());
    createJobRequest.setAddress(address);

    createJobRequest.setCeDeliveryRequired(csvRecordDTO.getCeDeliveryReqd());
    createJobRequest.setCeCE1Complete(csvRecordDTO.getCeCE1Complete());
    createJobRequest.setCeExpectedResponses(csvRecordDTO.getCeExpectedResponses());
    createJobRequest.setUua(false);
    createJobRequest.setBlankFormReturned(false);
    createJobRequest.setSai(false);

    return createJobRequest;
  }
}
