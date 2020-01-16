package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressCheckListing {

  @CsvBindByName(column = "caseReference")
  private String caseReference;

  @CsvBindByName(column = "guidancePrompt")
  private String guidancePrompt;

  @CsvBindByName(column = "line1")
  private String line1;

  @CsvBindByName(column = "line2")
  private String line2;

  @CsvBindByName(column = "line3")
  private String line3;

  @CsvBindByName(column = "townName")
  private String townName;

  @CsvBindByName(column = "postCode")
  private String postcode;

  @CsvBindByName(column = "latitude")
  private BigDecimal latitude;

  @CsvBindByName(column = "longitude")
  private BigDecimal longitude;

  @CsvBindByName(column = "additionalInformation")
  private String additionalInformation;
}

