package uk.gov.ons.fwmt.csvservice.implementation.ce;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;
import uk.gov.ons.census.fwmt.csvservice.implementation.ce.CEConverterService;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.FileInputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.ons.census.fwmt.csvservice.implementation.ce.CEGatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;

@RunWith(MockitoJUnitRunner.class)
public class CEConverterServiceImplTest {

  @InjectMocks
  private CEConverterService ceConverterService;

  @Mock
  private GatewayActionAdapter gatewayActionAdapter;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Mock
  private StorageUtils storageUtils;

  @Test
  public void convertCECSVToCanonicalTest() throws GatewayException, IOException {
    // Given
    ClassLoader classLoader = getClass().getClassLoader();
    String testPathString = classLoader.getResource("ceTestCSV.csv").getPath();
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(ceConverterService, "file", testResource);
    ReflectionTestUtils.setField(ceConverterService, "directory", "resources/");

    FileInputStream fileInputStream = new FileInputStream(testResource.getFile());
    try (fileInputStream) {
      when(storageUtils.getFileInputStream(any())).thenReturn(fileInputStream);

      // When
      ceConverterService.convertToCanonical();
    }

    // When
    ceConverterService.convertToCanonical();

    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CE_REQUEST_EXTRACTED));
  }
}
