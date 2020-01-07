package uk.gov.ons.census.fwmt.csvservice.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.PostcodeLookup;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class LookupFileLoaderServiceImpl implements LookupFileLoaderService {

  @Value("${gcpBucket.postcodelookuplocation}")
  private Resource csvGCPFile;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  static final Map<String, PostcodeLookup> postcodeLookupMap = new HashMap<>();

  @Override
  public void loadPostcodeLookupFile() throws GatewayException {

    CsvToBean<PostcodeLookup> csvToBean;
    try {
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(csvGCPFile.getInputStream(), StandardCharsets.UTF_8))
          .withType(PostcodeLookup.class)
          .build();
    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }

    for (PostcodeLookup postcodeLookup : csvToBean) {
      postcodeLookupMap.put(postcodeLookup.getPostcode(), postcodeLookup);
    }
  }

  public Map<String, PostcodeLookup> getLookupMap() {
    return postcodeLookupMap;
  }
}
