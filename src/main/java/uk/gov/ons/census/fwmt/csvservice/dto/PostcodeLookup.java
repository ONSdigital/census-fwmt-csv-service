package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostcodeLookup {

  @CsvBindByName(column = "Area_Role_Id")
  private String areaRoleId;

  @CsvBindByName(column = "LA")
  private String la;

  @CsvBindByName(column = "LA_name")
  private String laName;

  @CsvBindByName(column = "Postcode")
  private String postcode;
}
