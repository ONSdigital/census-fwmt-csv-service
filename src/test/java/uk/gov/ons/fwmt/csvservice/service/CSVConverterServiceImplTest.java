package uk.gov.ons.fwmt.csvservice.service;

import org.junit.Ignore;
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

import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Mock
    private OutputStream outputStream;

    @Mock
    private WritableResource writableResource;

    @Test
    public void convertCCSCSVToCanonicalTest () throws GatewayException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        String testPathString = classLoader.getResource("testCCSCSV.csv").getPath();
        Path testPath = Path.of("/", testPathString);
        Resource testResource = new FileSystemResource(testPathString);
        ReflectionTestUtils.setField(csvConverterServiceImpl, "ccsPath", testResource);
        ReflectionTestUtils.setField(csvConverterServiceImpl, "ccsPathFileName", testPath);

        // When
        csvConverterServiceImpl.convertCSVToCanonical("CCSIngest");

        // Then
        Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CCS_REQUEST_EXTRACTED));

    }

    @Test
    public void convertCECSVToCanonicalTest () throws GatewayException {
        // Given
        ClassLoader classLoader = getClass().getClassLoader();
        String testPathString = classLoader.getResource("testCECSV.csv").getPath();
        Path testPath = Path.of("/", testPathString);
       
        Resource testResource = new FileSystemResource(testPathString);
        ReflectionTestUtils.setField(csvConverterServiceImpl, "cePath", testResource);
        ReflectionTestUtils.setField(csvConverterServiceImpl, "cePathFileName", testPath);

        // When
        csvConverterServiceImpl.convertCSVToCanonical("CEIngest");

        // Then
        Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CE_REQUEST_EXTRACTED));

    }
}
