package uk.gov.ons.fwmt.csvservice.implementation.addresscheck;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.adapter.GatewayActionAdapter;

import uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static uk.gov.ons.census.fwmt.csvservice.implementation.addresscheck.AddressCheckGatewayEventsConfig.CSV_ADDRESS_CHECK_REQUEST_EXTRACTED;

@RunWith(MockitoJUnitRunner.class)
public class AddressCheckConverterServiceImplTest {

  @InjectMocks
  private AddressCheckConverterService addressCheckConverterService;


  @Mock
  private GatewayActionAdapter gatewayActionAdapter;

  @Mock
  private GatewayEventManager gatewayEventManager;

  @Test
  public void convertAddressCheckToCanonicalTest() throws GatewayException {
    // Given
    ClassLoader classLoader = getClass().getClassLoader();
    String testPathString = classLoader.getResource("testAddressCheckCSV.csv").getPath();
    Resource testResource = new FileSystemResource(testPathString);

    ReflectionTestUtils.setField(addressCheckConverterService, "csvGCPFile", testResource);
    ReflectionTestUtils.setField(addressCheckConverterService, "processedPath", testResource);

    // When


    // Then
    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_ADDRESS_CHECK_REQUEST_EXTRACTED));
  }
}
