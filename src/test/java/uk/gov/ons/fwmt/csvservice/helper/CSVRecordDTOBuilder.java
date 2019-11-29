package uk.gov.ons.fwmt.csvservice.helper;

import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.dto.CEJobListing;

import java.math.BigDecimal;

public class CSVRecordDTOBuilder {

  public CCSPropertyListing createCCSCSVRecord() {
    CCSPropertyListing csvRecordDTO = new CCSPropertyListing();

    csvRecordDTO.setPostCode("PO15 6LW");
    csvRecordDTO.setCcsInterviewer("Joe Bloggs");
    csvRecordDTO.setCoordinatorId("AW1");
    csvRecordDTO.setLatitude(BigDecimal.valueOf(51));
    csvRecordDTO.setLongitude(BigDecimal.valueOf(0.11));

    return csvRecordDTO;
  }

  public CEJobListing createCECSVRecord() {
    CEJobListing csvceRecordDTO = new CEJobListing();

    csvceRecordDTO.setCaseId("ca48b83b-7e29-4b20-9a0f-4f1ab3575c9a");
    csvceRecordDTO.setCaseReference("9bb60f3a-c0af-4188-965f-e018d39df507");
    csvceRecordDTO.setEstablishmentType("Household");
    csvceRecordDTO.setMandatoryResource("mand1");
    csvceRecordDTO.setCoordinatorId("AW1");
    csvceRecordDTO.setArid("123");
    csvceRecordDTO.setUprn("123");
    csvceRecordDTO.setLine1("1 Station Road");
    csvceRecordDTO.setTownName("Fareham");
    csvceRecordDTO.setPostCode("PO15 6LW");
    csvceRecordDTO.setOa("12");
    csvceRecordDTO.setLatitude(BigDecimal.valueOf(51));
    csvceRecordDTO.setLongitude(BigDecimal.valueOf(0.11));

    return csvceRecordDTO;
  }
}
