package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCSPropertyListing {

  @CsvBindByName(column = "postCode")
  private String postCode;

  @CsvBindByName(column = "oa")
  private String oa;

  @CsvBindByName(column = "latitude")
  private BigDecimal latitude;

  @CsvBindByName(column = "longitude")
  private BigDecimal longitude;

  @CsvBindByName(column = "coordinatorId")
  private String coordinatorId;

  @CsvBindByName(column = "ccsInterviewer")
  private String ccsInterviewer;

}
