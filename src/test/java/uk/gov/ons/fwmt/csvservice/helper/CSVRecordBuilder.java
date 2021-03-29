package uk.gov.ons.fwmt.csvservice.helper;

import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.dto.CeCreate;
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

  public CeCreate createCeCreateRecord() {
    CeCreate ceCreate = new CeCreate();

    ceCreate.setCaseId("e8bfa75f-ebbd-487f-9973-279233ad0887");
    ceCreate.setCaseReference("2776536191");
    ceCreate.setUprn("9992776536191");
    ceCreate.setEstabUprn("9992776536191");
    ceCreate.setSecureEstablishment(false);
    ceCreate.setEstablishmentType("CARE HOME");
    ceCreate.setFieldOfficerId("SH-RNL1-ZF-02");
    ceCreate.setCoordinatorId("SH-RNL1-ZF");
    ceCreate.setOrganisationName("Org name");
    ceCreate.setAddressLine1("Carehome");
    ceCreate.setAddressLine2("Road");
    ceCreate.setAddressLine3("Enfield");
    ceCreate.setTownName("London");
    ceCreate.setPostCode("EN3 6AY");
    ceCreate.setOa("E00007186");
    ceCreate.setLatitude(51.668884);
    ceCreate.setLongitude(-0.031528);
    ceCreate.setHandDelivered(true);
    ceCreate.setCeCE1Complete(true);
    ceCreate.setCeExpectedCapacity(15);
    ceCreate.setCeActualResponses(4);
    ceCreate.setUaa(false);

    return ceCreate;
  }
}
