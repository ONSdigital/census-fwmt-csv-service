package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NCIntListing {

  @CsvBindByName(column = "Address_Type")
  private String addressType;

  @CsvBindByName(column = "Address_Level")
  private String addressLevel;

  @CsvBindByName(column = "Case_ID")
  private String caseId;

  @CsvBindByName(column = "Case_Ref")
  private String caseRef;

  @CsvBindByName(column = "Establishment_Type")
  private String establishmentType;

  @CsvBindByName(column = "Field_Coordinator_ID")
  private String fieldCoordinatorId;

  @CsvBindByName(column = "Field_Officer_ID")
  private String fieldOfficerId;

  @CsvBindByName(column = "Address_Line1")
  private String addressLine1;

  @CsvBindByName(column = "Address_Line2")
  private String addressLine2;

  @CsvBindByName(column = "Address_Line3")
  private String addressLine3;

  @CsvBindByName(column = "Town")
  private String town;

  @CsvBindByName(column = "Postcode")
  private String postcode;

  @CsvBindByName(column = "OA_Code")
  private String oaCode;

  @CsvBindByName(column = "Latitude")
  private Double latitude;

  @CsvBindByName(column = "Longitude")
  private Double longitude;

}
