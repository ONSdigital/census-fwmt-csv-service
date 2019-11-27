package uk.gov.ons.census.fwmt.csvservice.service.impl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.canonical.CanonicalJobHelper;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.CSVRecordDTO;
import uk.gov.ons.census.fwmt.csvservice.service.CSVAdapterService;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;

@Slf4j
@Service
public class CSVConverterServiceImpl implements CSVConverterService {
    @Value("${gcpBucket.celocation}")
    private Resource cePath;

    @Value("${gcpBucket.ccslocation}")
    private Resource ccsPath;

    @Value("${gcpBucket.celocation}")
    private Path cePathFileName;

    @Value("${gcpBucket.ccslocation}")
    private Path ccsPathFileName;

    @Autowired
    private CSVAdapterService csvAdapterService;

    @Autowired
    private GatewayEventManager gatewayEventManager;

    public void convertCSVToCanonical(String ingestType) throws GatewayException {

        CsvToBean<CSVRecordDTO> csvToBean;
        Date formattedDate = new Date(System.currentTimeMillis());
        Path csvPath;
        Resource csvGCPFile;
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'_'HH-mm-ss");
        String csvToUpdate;
        String dateAndTime;
        String csvFileExtension;
        String processCSVExtension;

        if (ingestType.equals("CEIngest")) {
            csvGCPFile = cePath;
            csvPath = cePathFileName;
        } else {
            csvGCPFile = ccsPath;
            csvPath = ccsPathFileName;
        }

        try {
            csvToBean = new CsvToBeanBuilder(
                    new InputStreamReader(csvGCPFile.getInputStream(), StandardCharsets.UTF_8))
                    .withType(CSVRecordDTO.class)
                    .build();

            csvToUpdate = Files.readString(csvPath, StandardCharsets.UTF_8);

            dateAndTime = formatDate.format(formattedDate);

            csvFileExtension = csvGCPFile.getFilename();

            if (csvFileExtension != null) {
                processCSVExtension = csvFileExtension.replace(".csv", ".processed_" + dateAndTime);
                try (OutputStream os = ((WritableResource) csvGCPFile.createRelative(processCSVExtension)).getOutputStream()) {
                    os.write(csvToUpdate.getBytes(StandardCharsets.UTF_8));
                }
            }

        } catch (IOException e) {
            String msg = "Failed to convert CSV to Bean.";
            gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
            throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
        }

        if (ingestType.equals(CSVConverterService.CE_INGEST)) {
            for (CSVRecordDTO csvRecordDTO : csvToBean) {
                csvAdapterService.sendJobRequest(CanonicalJobHelper.createCEJob(csvRecordDTO));
                gatewayEventManager.triggerEvent(String.valueOf(csvRecordDTO.getCaseId()), CSV_CE_REQUEST_EXTRACTED);
            }

            try {
                Files.delete(csvPath);
            } catch (IOException e) {
                throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to delete CE Ingest CSV");
            }

        } else {
            for (CSVRecordDTO csvRecordDTO : csvToBean) {
                CreateFieldWorkerJobRequest createCCSRequest = CanonicalJobHelper.createCCSJob(csvRecordDTO);
                csvAdapterService.sendJobRequest(createCCSRequest);
                gatewayEventManager.triggerEvent(String.valueOf(createCCSRequest.getCaseId()), CSV_CCS_REQUEST_EXTRACTED);
            }

            try {
                Files.delete(csvPath);
            } catch (IOException e) {
                throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to delete CCS Ingest CSV");
            }
        }
    }
}
