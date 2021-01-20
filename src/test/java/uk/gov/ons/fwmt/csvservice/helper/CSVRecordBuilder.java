package uk.gov.ons.fwmt.csvservice.helper;

import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.dto.NCIntListing;

import java.math.BigDecimal;

public class CSVRecordBuilder {

  public CCSPropertyListing createCCSCSVRecord() {
    CCSPropertyListing ccsPropertyListing = new CCSPropertyListing();

    ccsPropertyListing.setPostCode("PO15 6LW");
    ccsPropertyListing.setFieldOfficerId("Joe Bloggs");
    ccsPropertyListing.setFieldCoordinatorId("AW1");
    ccsPropertyListing.setLatitude(51d);
    ccsPropertyListing.setLongitude(0.11);

    return ccsPropertyListing;
  }

  public NCIntListing createNCCSVRecord() {
    NCIntListing ncIntListing = new NCIntListing();

    ncIntListing.setCaseId("ca48b83b-7e29-4b20-9a0f-4f1ab3575c9a");
    ncIntListing.setCaseRef("9bb60f3a-c0af-4188-965f-e018d39df507");
    ncIntListing.setEstablishmentType("Household");
    ncIntListing.setLatitude(Double.valueOf(51));
    ncIntListing.setLongitude(Double.valueOf(0.11));

    return ncIntListing;
  }
}
