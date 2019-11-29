package uk.gov.ons.fwmt.csvservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.implementation.ce.CCSConverterServiceImpl;
import uk.gov.ons.census.fwmt.csvservice.implementation.ce.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.OutputStream;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;

@RunWith(MockitoJUnitRunner.class)
public class CCSConverterServiceImplTest {

  @InjectMocks
  private CCSConverterService ccsConverterService;

  @Mock
  private GatewayActionAdapter gatewayActionAdapter;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Mock
  private OutputStream outputStream;

  @Mock
  private WritableResource writableResource;

  @Test
  public void convertCCSCSVToCanonicalTest() throws GatewayException {
    // Given
    ClassLoader classLoader = getClass().getClassLoader();
    String testPathString = classLoader.getResource("testCCSCSV.csv").getPath();
    Path testPath = Path.of("/", testPathString);
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(ccsConverterService, "csvGCPFile", testResource);
    ReflectionTestUtils.setField(ccsConverterService, "csvPath", testPath);
    ReflectionTestUtils.setField(ccsConverterService, "processedPath", testPath);

    // When
    ccsConverterService.convertCSVToCanonical();

    // Then
    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CCS_REQUEST_EXTRACTED));
  }
}
