package uk.gov.ons.fwmt.csvservice.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.*;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.service.impl.CSVAdapterServiceImpl;
import uk.gov.ons.census.fwmt.csvservice.service.impl.CSVConverterServiceImpl;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import static org.mockito.ArgumentMatchers.*;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;

@RunWith(MockitoJUnitRunner.class)
public class CSVConverterServiceImplTest {
    @InjectMocks
    private CSVConverterServiceImpl csvConverterServiceImpl;

    @Mock
    private CSVAdapterServiceImpl csvAdapterService;

    @Mock
    private GatewayEventManager gatewayEventManager;

    @Test
    public void convertCCSCSVToCanonicalTest () throws GatewayException {
        // Given
        Resource testResource = new ClassPathResource("testCCSCSV.csv");
        ReflectionTestUtils.setField(csvConverterServiceImpl, "ccsPath", testResource);

        // When
        csvConverterServiceImpl.convertCSVToCanonical("CCSIngest");

        // Then
        Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CCS_REQUEST_EXTRACTED), any());

    }

    @Test
    public void convertCECSVToCanonicalTest () throws GatewayException {
        // Given
        Resource testResource = new ClassPathResource("testCECSV.csv");
        ReflectionTestUtils.setField(csvConverterServiceImpl, "cePath", testResource);

        // When
        csvConverterServiceImpl.convertCSVToCanonical("CEIngest");

        // Then
        Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CE_REQUEST_EXTRACTED), any());

    }
}
