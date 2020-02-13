package uk.gov.ons.fwmt.csvservice.implementation.ccs;

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
import uk.gov.ons.census.fwmt.csvservice.implementation.ccs.CCSConverterService;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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
  private StorageUtils storageUtils;

  @Test
  public void convertCCSCSVToCanonicalTest() throws GatewayException, IOException {
    // Given
    ClassLoader classLoader = getClass().getClassLoader();
    String testPathString = classLoader.getResource("ccsTestCSV.csv").getPath();
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(ccsConverterService, "file", testResource);
    ReflectionTestUtils.setField(ccsConverterService, "directory", "resources/");

    FileInputStream fileInputStream = new FileInputStream(testResource.getFile());
    try (fileInputStream) {
      when(storageUtils.getFileInputStream(any())).thenReturn(fileInputStream);

      // When
      ccsConverterService.convertToCanonical();
    }

    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CCS_REQUEST_EXTRACTED));
  }
}
