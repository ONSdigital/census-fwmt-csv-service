package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.census.fwmt.canonical.v1.Address;
import uk.gov.ons.census.fwmt.canonical.v1.Contact;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;


import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSVRecordDTO {
  @CsvBindByName(column = "caseId")
  private String caseId;
  @CsvBindByName(column = "caseReference")
  private String caseReference;
  @CsvBindByName(column = "establishmentType")
  private String establishmentType;
  @CsvBindByName(column = "fieldOfficerId")
  private String mandatoryResource;
  @CsvBindByName(column = "coordinatorId")
  private String coordinatorId;
  @CsvBindByName(column = "organisationName")
  private String organisationName;
  @CsvBindByName(column = "arid")
  private String arid;
  @CsvBindByName(column = "uprn")
  private String uprn;
  @CsvBindByName(column = "line1")
  private String line1;
  @CsvBindByName(column = "line2")
  private String line2;
  @CsvBindByName(column = "line3")
  private String line3;
  @CsvBindByName(column = "townName")
  private String townName;
  @CsvBindByName(column = "postCode")
  private String postCode;
  @CsvBindByName(column = "oa")
  private String oa;
  @CsvBindByName(column = "lattitude")
  private BigDecimal lattitude;
  @CsvBindByName(column = "longitude")
  private BigDecimal longitude;
  @CsvBindByName(column = "ceExpectedCapacity")
  private int ceExpectedResponses;

  public CreateFieldWorkerJobRequest createCE() {
    Address address = new Address();
    Contact contact = new Contact();
    CreateFieldWorkerJobRequest createJobRequest = new CreateFieldWorkerJobRequest();

    createJobRequest.setCaseId(UUID.fromString(caseId));
    createJobRequest.setCaseReference(caseReference);
    createJobRequest.setEstablishmentType(establishmentType);
    createJobRequest.setMandatoryResource(mandatoryResource);
    createJobRequest.setCoordinatorId(coordinatorId);

    contact.setOrganisationName(organisationName);
    createJobRequest.setContact(contact);

    address.setArid(arid);
    address.setUprn(uprn);
    address.setLine1(line1);
    address.setLine2(line2);
    address.setLine3(line3);
    address.setTownName(townName);
    address.setPostCode(postCode);
    address.setOa(oa);
    address.setLatitude(lattitude);
    address.setLongitude(longitude);
    createJobRequest.setAddress(address);

    createJobRequest.setCeExpectedResponses(ceExpectedResponses);

    return createJobRequest;
  }
}
