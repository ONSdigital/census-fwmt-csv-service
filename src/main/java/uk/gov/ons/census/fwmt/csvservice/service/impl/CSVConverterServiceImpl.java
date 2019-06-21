package uk.gov.ons.census.fwmt.csvservice.service.impl;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.canonical.v1.CreateFieldWorkerJobRequest;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.dto.CSVRecordDTO;
import uk.gov.ons.census.fwmt.csvservice.service.CSVAdapterService;
import uk.gov.ons.census.fwmt.csvservice.service.CSVConverterService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class CSVConverterServiceImpl implements CSVConverterService {
  @Value("${gcpBucketLocation}")
  private Resource path;

  private Path pathway = Paths.get(URI.create("gs://ce-csv-bucket-dev/ceTestCSV.csv"));

  private CSVAdapterServiceImpl csvAdapterService = new CSVAdapterServiceImpl();

  @Override
  public void convertCSVToObject() throws IOException, GatewayException {

    try {
      CsvToBean<CSVRecordDTO> csvToBean = new CsvToBeanBuilder(new InputStreamReader(path.getInputStream()))
          .withType(CSVRecordDTO.class).build();

      for (CSVRecordDTO csvRecordDTO : csvToBean) {
        CreateFieldWorkerJobRequest createFieldWorkerJobRequest = csvRecordDTO.createCE();
//        System.out.println(createFieldWorkerJobRequest.getAddress());
        csvAdapterService.sendJobRequest(createFieldWorkerJobRequest);
      }

    } catch (Exception e){
        throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, "Incorrect format");
    }
  }
}
