package uk.gov.ons.census.fwmt.csvservice.service.impl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.canonical.CanonicalJobHelper;
import uk.gov.ons.census.fwmt.csvservice.dto.CSVRecordDTO;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class CSVConverterServiceImpl implements CSVConverterService {
  @Value("${gcpBucket.location}")
  private Resource path;

  @Autowired
  private CSVAdapterServiceImpl csvAdapterService;

  @Override
  public void convertCSVToObject() throws GatewayException {
    try {
      CsvToBean<CSVRecordDTO> csvToBean = new CsvToBeanBuilder(
          new InputStreamReader(path.getInputStream(), StandardCharsets.UTF_8))
          .withType(CSVRecordDTO.class)
          .build();

      for (CSVRecordDTO csvRecordDTO : csvToBean) {
        csvAdapterService.sendJobRequest(CanonicalJobHelper.createCEJob(csvRecordDTO));
      }

    } catch (Exception e) {
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e,
          "Failed to convert CSV record to Canonical job");
    }
  }
}
