package uk.gov.ons.fwmt.csvservice.helper;

import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.dto.CEJobListing;

import java.math.BigDecimal;

public class CSVRecordBuilder {

  public CCSPropertyListing createCCSCSVRecord() {
    CCSPropertyListing ccsPropertyListing = new CCSPropertyListing();

    ccsPropertyListing.setPostCode("PO15 6LW");
    ccsPropertyListing.setCcsInterviewer("Joe Bloggs");
    ccsPropertyListing.setCoordinatorId("AW1");
    ccsPropertyListing.setLatitude(BigDecimal.valueOf(51));
    ccsPropertyListing.setLongitude(BigDecimal.valueOf(0.11));

    return ccsPropertyListing;
  }

  public CEJobListing createCECSVRecord() {
    CEJobListing ceJobListing = new CEJobListing();

    ceJobListing.setCaseId("ca48b83b-7e29-4b20-9a0f-4f1ab3575c9a");
    ceJobListing.setCaseReference("9bb60f3a-c0af-4188-965f-e018d39df507");
    ceJobListing.setEstablishmentType("Household");
    ceJobListing.setMandatoryResource("mand1");
    ceJobListing.setCoordinatorId("AW1");
    ceJobListing.setArid("123");
    ceJobListing.setUprn("123");
    ceJobListing.setLine1("1 Station Road");
    ceJobListing.setTownName("Fareham");
    ceJobListing.setPostCode("PO15 6LW");
    ceJobListing.setOa("12");
    ceJobListing.setLatitude(BigDecimal.valueOf(51));
    ceJobListing.setLongitude(BigDecimal.valueOf(0.11));

    return ceJobListing;
  }

  public AddressCheckListing createAddressCheckCSVRecord() {
    AddressCheckListing addressCheckListing = new AddressCheckListing();

    addressCheckListing.setCaseReference("9bb60f3a-c0af-4188-965f-e018d39df507");
    addressCheckListing.setLine1("1 Station Road");
    addressCheckListing.setLine2("Station Town");
    addressCheckListing.setLine3("Greater Station");
    addressCheckListing.setTownName("Fareham");
    addressCheckListing.setGuidancePrompt("12");
    addressCheckListing.setLatitude(BigDecimal.valueOf(51));
    addressCheckListing.setLongitude(BigDecimal.valueOf(0.11));
    addressCheckListing.setAdditionalInformation("Additional information");

    return addressCheckListing;
  }
}
