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
import uk.gov.ons.census.fwmt.csvservice.implementation.ce.CEConverterService;
import uk.gov.ons.census.fwmt.csvservice.implementation.ce.CSVAdapterServiceImpl;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.OutputStream;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;

@RunWith(MockitoJUnitRunner.class)
public class CEConverterServiceImplTest {

  @InjectMocks
  private CEConverterService ceConverterService;

  @Mock
  private CSVAdapterServiceImpl csvAdapterService;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Mock
  private OutputStream outputStream;

  @Mock
  private WritableResource writableResource;

  @Test
  public void convertCECSVToCanonicalTest() throws GatewayException {
    // Given
    ClassLoader classLoader = getClass().getClassLoader();
    String testPathString = classLoader.getResource("testCECSV.csv").getPath();
    Path testPath = Path.of("/", testPathString);
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(ceConverterService, "csvGCPFile", testResource);
    ReflectionTestUtils.setField(ceConverterService, "csvPath", testPath);
    ReflectionTestUtils.setField(ceConverterService, "processedPath", testPath);

    // When
    ceConverterService.convertCSVToCanonical();

    // Then
    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CE_REQUEST_EXTRACTED));
  }
}
