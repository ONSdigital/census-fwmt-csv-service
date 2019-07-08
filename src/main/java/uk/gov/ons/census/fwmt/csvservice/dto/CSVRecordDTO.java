package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CSVRecordDTO {
  @CsvBindByName(column = "caseId")
  private String caseId;

  @CsvBindByName(column = "caseReference")
  private String caseReference;

  @CsvBindByName(column = "establishmentType")
  private String establishmentType;

  @CsvBindByName(column = "fieldOfficerId")
  private String mandatoryResource;

  @CsvBindByName(column = "coordinatorId")
  private String coordinatorId;

  @CsvBindByName(column = "name")
  private String name;

  @CsvBindByName(column = "organisationName")
  private String organisationName;

  @CsvBindByName(column = "phoneNumber")
  private String phoneNumber;

  @CsvBindByName(column = "uprn")
  private String uprn;

  @CsvBindByName(column = "line1")
  private String line1;

  @CsvBindByName(column = "line2")
  private String line2;

  @CsvBindByName(column = "line3")
  private String line3;

  @CsvBindByName(column = "townName")
  private String townName;

  @CsvBindByName(column = "postCode")
  private String postCode;

  @CsvBindByName(column = "oa")
  private String oa;

  @CsvBindByName(column = "lattitude")
  private BigDecimal lattitude;

  @CsvBindByName(column = "longitude")
  private BigDecimal longitude;

  @CsvBindByName(column = "ceDeliveryReqd")
  private Boolean ceDeliveryReqd;

  @CsvBindByName(column = "ceCE1Complete")
  private Boolean ceCE1Complete;

  @CsvBindByName(column = "ceExpectedCapacity")
  private int ceExpectedResponses;
}
