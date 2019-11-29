package uk.gov.ons.fwmt.csvservice.implementation.ce;

import org.junit.Test;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.dto.CEJobListing;
import uk.gov.ons.fwmt.csvservice.helper.CSVRecordDTOBuilder;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CECanonicalBuilder.createCEJob;


public class CECanonicalBuilderTest {

  @Test
  public void createCE () {
    // Given
    CEJobListing ceJobListing = new CSVRecordDTOBuilder().createCECSVRecord();

    // When
    CreateFieldWorkerJobRequest createFieldWorkerJobRequest = createCEJob(ceJobListing);

    // Then
    assertEquals(UUID.fromString(ceJobListing.getCaseId()), createFieldWorkerJobRequest.getCaseId());
    assertEquals(ceJobListing.getEstablishmentType(), createFieldWorkerJobRequest.getEstablishmentType());
    assertEquals(ceJobListing.getLatitude(), createFieldWorkerJobRequest.getAddress().getLatitude());
    assertEquals(ceJobListing.getCoordinatorId(), createFieldWorkerJobRequest.getCoordinatorId());
  }
}
