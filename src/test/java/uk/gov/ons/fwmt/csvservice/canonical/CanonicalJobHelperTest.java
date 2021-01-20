package uk.gov.ons.fwmt.csvservice.canonical;

import org.junit.Test;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.fwmt.csvservice.helper.CSVRecordBuilder;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CECanonicalBuilder.createCEJob;

public class CanonicalJobHelperTest {

  @Test
  public void createCE() {
    // Given
    CEJobListing ceJobListing = new CSVRecordBuilder().createCECSVRecord();

    // When
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createCEJob(ceJobListing);

    // Then
    assertEquals(UUID.fromString(ceJobListing.getCaseId()), createFieldWorkerJobRequest.getCaseId());
    assertEquals(ceJobListing.getEstablishmentType(), createFieldWorkerJobRequest.getEstablishmentType());
    assertEquals(ceJobListing.getLatitude(), createFieldWorkerJobRequest.getAddress().getLatitude());
    assertEquals(ceJobListing.getCoordinatorId(), createFieldWorkerJobRequest.getCoordinatorId());
  }
}
