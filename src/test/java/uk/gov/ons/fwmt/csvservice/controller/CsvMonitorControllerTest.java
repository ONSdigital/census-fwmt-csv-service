package uk.gov.ons.fwmt.csvservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import uk.gov.ons.census.fwmt.csvservice.controller.CsvMonitorController;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CsvMonitorControllerTest {
  @InjectMocks
  private CsvMonitorController csvMonitorController;

  @Mock
  private Map<String, CSVConverterService> csvConverterServiceImpl;

  @Test
  public void testCECsvMonitorController() throws Exception {

    ResponseEntity responseEntity = csvMonitorController.ingestCeCsvFile();

    assertEquals("CSV adapter service activated", responseEntity.getBody());
    assertEquals(200, responseEntity.getStatusCodeValue());
    verify(csvConverterServiceImpl).get("CE").convertCSVToCanonical();
  }

  @Test
  public void testCCSCsvMonitorController() throws Exception {

    ResponseEntity responseEntity = csvMonitorController.ingestCSSCsvFile();

    assertEquals("CSV adapter service activated", responseEntity.getBody());
    assertEquals(200, responseEntity.getStatusCodeValue());
    verify(csvConverterServiceImpl).get("CCS").convertCSVToCanonical();
  }
}
