package uk.gov.ons.fwmt.csvservice.implementation.ccs;

import com.google.cloud.storage.Storage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSConverterService;
import uk.gov.ons.census.fwmt.csvservice.utils.CsvServiceUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSGatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;

@RunWith(MockitoJUnitRunner.class)
public class CCSConverterServiceImplTest {

  @InjectMocks
  private CCSConverterService ccsConverterService;

  @Mock
  private GatewayActionAdapter gatewayActionAdapter;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Mock
  private CsvServiceUtils csvServiceUtils;

  @Mock
  private Storage googleCloudStorage;

  @Test
  public void convertCCSCSVToCanonicalTest() throws GatewayException {
    // Given
    ClassLoader classLoader = getClass().getClassLoader();
    String testPathString = classLoader.getResource("testCCSCSV.csv").getPath();
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(ccsConverterService, "csvGCPFile", testResource);
    ReflectionTestUtils.setField(ccsConverterService, "bucketName", "bucket");

    // When
    ccsConverterService.convertToCanonical();

    // Then
    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CCS_REQUEST_EXTRACTED));
  }
}
