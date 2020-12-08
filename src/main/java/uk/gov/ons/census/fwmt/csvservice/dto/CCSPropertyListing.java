package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CCSPropertyListing {

  @CsvBindByName(column = "Postcode")
  private String postCode;

  @CsvBindByName(column = "OA_Code")
  private String oa;

  @CsvBindByName(column = "Longitude")
  private Double longitude;

  @CsvBindByName(column = "Latitude")
  private Double latitude;

  @CsvBindByName(column = "Field_Coordinator_ID")
  private String fieldCoordinatorId;

  @CsvBindByName(column = "Field_Officer_ID")
  private String fieldOfficerId;

}
