package uk.gov.ons.census.fwmt.csvservice.service.impl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.canonical.CanonicalJobHelper;
import uk.gov.ons.census.fwmt.csvservice.dto.CSVRecordDTO;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;

import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CCS_REQUEST_EXTRACTED;
import static uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig.CSV_CE_REQUEST_EXTRACTED;

@Slf4j
@Service
public class CSVConverterServiceImpl implements CSVConverterService {
  @Value("${gcpBucket.celocation}")
  private Resource cePath;

  @Value("${gcpBucket.ccslocation}")
  private Resource ccsPath;

  @Autowired
  private CSVAdapterServiceImpl csvAdapterService;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  public void convertCSVToCanonical(String ingestType) throws GatewayException {

    CanonicalJobHelper canonicalJobHelper;
    CsvToBean<CSVRecordDTO> csvToBean;
    Resource csvGCPFile;

    if (ingestType.equals("CEIngest")) {
      csvGCPFile = cePath;

    } else {
      csvGCPFile = ccsPath;
    }

    try {
      csvToBean = new CsvToBeanBuilder(
          new InputStreamReader(csvGCPFile.getInputStream(), StandardCharsets.UTF_8))
          .withType(CSVRecordDTO.class)
          .build();
    } catch (IOException e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e,
          "Failed to convert CSV to Bean.");
    }

    if (ingestType.equals("CEIngest")) {
      for (CSVRecordDTO csvRecordDTO : csvToBean) {
        csvAdapterService.sendJobRequest(CanonicalJobHelper.createCEJob(csvRecordDTO));
        gatewayEventManager
                .triggerEvent(String.valueOf(csvRecordDTO.getCaseId()), CSV_CE_REQUEST_EXTRACTED, LocalTime.now());
      }

    } else {
      for (CSVRecordDTO csvRecordDTO : csvToBean) {
        CreateFieldWorkerJobRequest createCCSRequest = CanonicalJobHelper.createCCSJob(csvRecordDTO);
        csvAdapterService.sendJobRequest(createCCSRequest);
        gatewayEventManager
                .triggerEvent(String.valueOf(createCCSRequest.getCaseId()), CSV_CCS_REQUEST_EXTRACTED, LocalTime.now());
      }
    }
  }
}
