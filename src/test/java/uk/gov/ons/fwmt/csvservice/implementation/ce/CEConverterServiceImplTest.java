package uk.gov.ons.fwmt.csvservice.implementation.ce;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
    String testPathString = classLoader.getResource("testCECSV.csv").getPath();
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(ceConverterService, "file", testResource);
    ReflectionTestUtils.setField(ceConverterService, "directory", "file:");

    InputStream in = null;
    try {
      in = new FileInputStream(testResource.getFile());
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      in.close();
    }

    when(storageUtils.getFileInputStream(any())).thenReturn(in);

    // When
    ceConverterService.convertToCanonical();
  }
}
