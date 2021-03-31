package uk.gov.ons.fwmt.csvservice.implementation;

import org.junit.Test;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;
import uk.gov.ons.census.fwmt.csvservice.dto.CeCreate;
import uk.gov.ons.census.fwmt.csvservice.dto.NCIntListing;
import uk.gov.ons.fwmt.csvservice.helper.CSVRecordBuilder;

import static org.junit.Assert.*;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSCanonicalBuilder.createCCSJob;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CeCreateBuilder.buildCeCreate;
import static uk.gov.ons.census.fwmt.csvservice.implementation.nc.NonComplianceBuilder.createNcJob;


public class CanonicalBuilderTests {

  @Test
  public void createNCJobTest() {
    // Given
    NCIntListing ncIntListing = new CSVRecordBuilder().createNCCSVRecord();

    // When
    FwmtActionInstruction fwmtActionInstruction = createNcJob(ncIntListing);

    // Then
    assertNotEquals(ncIntListing.getCaseId(), fwmtActionInstruction.getCaseId());
    assertEquals("NC" + ncIntListing.getCaseRef(), fwmtActionInstruction.getCaseRef());
    assertEquals(ncIntListing.getCaseId(), fwmtActionInstruction.getOldCaseId());
  }

  @Test
  public void createCCSJobTest() {
    // Given
    CCSPropertyListing ccsPropertyListing = new CSVRecordBuilder().createCCSCSVRecord();
    int testCaseRefCount = 1;
    String expectedTestCaseRef = "PL" +testCaseRefCount;

    // When
    FwmtActionInstruction fwmtActionInstruction = createCCSJob(ccsPropertyListing, testCaseRefCount);

    // Then
    assertNotNull(fwmtActionInstruction.getCaseId());
    assertEquals(expectedTestCaseRef, fwmtActionInstruction.getCaseRef());
  }

  @Test
  public void createCeCreatJobTest() {
    // Given
    CeCreate ceCreate = new CSVRecordBuilder().createCeCreateRecord();
    String expectedCaseId = "e8bfa75f-ebbd-487f-9973-279233ad0887";

    // When
    FwmtActionInstruction fwmtActionInstruction = buildCeCreate(ceCreate);

    //Then
    assertNotNull(fwmtActionInstruction.getCaseId());
    assertEquals(expectedCaseId,fwmtActionInstruction.getCaseId());
    assertNotNull(fwmtActionInstruction.getEstabUprn());
  }
}
