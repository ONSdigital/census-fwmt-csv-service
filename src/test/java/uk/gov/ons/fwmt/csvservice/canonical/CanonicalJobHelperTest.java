package uk.gov.ons.fwmt.csvservice.canonical;

import org.junit.Test;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.csvservice.canonical.CanonicalJobHelper;
import uk.gov.ons.census.fwmt.csvservice.dto.CSVRecordDTO;
import uk.gov.ons.fwmt.csvservice.helper.CSVRecordDTOBuilder;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CanonicalJobHelperTest {

    @Test
    public void createCE () {
        // Given
        CSVRecordDTO csvceRecordDTO = new CSVRecordDTOBuilder().createCECSVRecord();

        // When
        CreateFieldWorkerJobRequest createFieldWorkerJobRequest = CanonicalJobHelper.createCEJob(csvceRecordDTO);

        // Then
        assertEquals(UUID.fromString(csvceRecordDTO.getCaseId()), createFieldWorkerJobRequest.getCaseId());
        assertEquals(csvceRecordDTO.getEstablishmentType(), createFieldWorkerJobRequest.getEstablishmentType());
        assertEquals(csvceRecordDTO.getLatitude(), createFieldWorkerJobRequest.getAddress().getLatitude());
        assertEquals(csvceRecordDTO.getCoordinatorId(), createFieldWorkerJobRequest.getCoordinatorId());
    }

    @Test
    public void createCCS () {
        // Given
        CSVRecordDTO csvRecordDTO = new CSVRecordDTOBuilder().createCCSCSVRecord();

        // When
        CreateFieldWorkerJobRequest createFieldWorkerJobRequest = CanonicalJobHelper.createCCSJob(csvRecordDTO);

        // Then
        assertNotNull(createFieldWorkerJobRequest.getCaseId());
        assertEquals(csvRecordDTO.getPostCode(), createFieldWorkerJobRequest.getAddress().getPostCode());
        assertEquals(csvRecordDTO.getLatitude(), createFieldWorkerJobRequest.getAddress().getLatitude());
        assertEquals(csvRecordDTO.getCoordinatorId(), createFieldWorkerJobRequest.getCoordinatorId());
    }
}
