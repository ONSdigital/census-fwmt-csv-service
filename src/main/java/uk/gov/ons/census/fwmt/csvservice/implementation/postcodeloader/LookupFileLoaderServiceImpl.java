package uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uk.gov.census.ffa.storage.utils.StorageUtils;
import uk.gov.ons.census.fwmt.common.error.GatewayException;
import uk.gov.ons.census.fwmt.csvservice.config.GatewayEventsConfig;
import uk.gov.ons.census.fwmt.csvservice.dto.PostcodeLookup;
import uk.gov.ons.census.fwmt.csvservice.service.LookupFileLoaderService;
import uk.gov.ons.census.fwmt.events.component.GatewayEventManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.ons.census.fwmt.csvservice.implementation.postcodeloader.LookupFileLoaderEventsConfig.POSTCODE_LOOKUP_LOADED;

@Component
public class LookupFileLoaderServiceImpl implements LookupFileLoaderService {

  @Value("${gcpBucket.postcodelookuplocation}")
  private Resource file;

  @Autowired
  private GatewayEventManager gatewayEventManager;

  @Autowired
  private StorageUtils storageUtils;

  static final Map<String, PostcodeLookup> postcodeLookupMap = new HashMap<>();

  @Override
  public void loadPostcodeLookupFile() throws GatewayException {
    try {
      InputStream inputStream = storageUtils.getFileInputStream(file.getURI());
      CsvToBean<PostcodeLookup> csvToBean;
      csvToBean = new CsvToBeanBuilder(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
          .withType(PostcodeLookup.class)
          .build();
      for (PostcodeLookup postcodeLookup : csvToBean) {
        postcodeLookupMap.put(postcodeLookup.getPostcode().replaceAll("\\s+", "").toUpperCase(), postcodeLookup);
      }
      gatewayEventManager.triggerEvent("N/A", POSTCODE_LOOKUP_LOADED);
    } catch (IOException e) {
      String msg = "Failed to convert CSV to Bean.";
      gatewayEventManager.triggerErrorEvent(this.getClass(), msg, "N/A", GatewayEventsConfig.UNABLE_TO_READ_CSV);
      throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, msg);
    }
  }

  public Map<String, PostcodeLookup> getLookupMap() {
    return postcodeLookupMap;
  }

  public PostcodeLookup getPostcodeLookup(String key) {
    return postcodeLookupMap.get(key);
  }
}
