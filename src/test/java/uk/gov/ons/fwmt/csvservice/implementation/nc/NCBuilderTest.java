package uk.gov.ons.fwmt.csvservice.implementation.nc;

import org.junit.Test;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.NCIntListing;
import uk.gov.ons.fwmt.csvservice.helper.CSVRecordBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static uk.gov.ons.census.fwmt.csvservice.implementation.nc.NonComplianceBuilder.createNcJob;


public class NCBuilderTest {

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
}
