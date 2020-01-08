package uk.gov.ons.fwmt.csvservice.implementation.addresscheck;

import org.junit.Test;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.AddressCheckListing;
import uk.gov.ons.census.fwmt.csvservice.dto.PostcodeLookup;
import uk.gov.ons.fwmt.csvservice.helper.CSVRecordBuilder;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckCanonicalBuilder.createAddressCheckJob;

public class AddressCheckCanonicalBuilderTest {

  @Test
  public void createAddressCheckJobTest() {
    AddressCheckListing addressCheckListing = new CSVRecordBuilder().createAddressCheckCSVRecord();
    PostcodeLookup postcodeLookup = new PostcodeLookup();
    postcodeLookup.setAreaRoleId("areaId");
    postcodeLookup.setPostcode("PO15 6LW");
    postcodeLookup.setLaName("laName");
    postcodeLookup.setLa("la");

    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createAddressCheckJob(addressCheckListing, postcodeLookup);

    assertNotNull(createFieldWorkerJobRequest.getCaseId());
    assertEquals(addressCheckListing.getTownName(), createFieldWorkerJobRequest.getAddress().getTownName());
    assertEquals(addressCheckListing.getLatitude(), createFieldWorkerJobRequest.getAddress().getLatitude());
    assertEquals(addressCheckListing.getLongitude(), createFieldWorkerJobRequest.getAddress().getLongitude());
  }

}
