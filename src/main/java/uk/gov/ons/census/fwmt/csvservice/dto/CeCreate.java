package uk.gov.ons.census.fwmt.csvservice.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CeCreate {

  @CsvBindByName(column = "Case_ID")
  private String caseId;

  @CsvBindByName(column = "Case_Ref")
  private String caseReference;

  @CsvBindByName(column = "UPRN")
  private String uprn;

  @CsvBindByName(column = "Secure_Establishment")
  private Boolean secureEstablishment;

  @CsvBindByName(column = "Establishment_Type")
  private String establishmentType;

  @CsvBindByName(column = "Field_Officer_Id")
  private String fieldOfficerId;

  @CsvBindByName(column = "Field_Coordinator_Id")
  private String coordinatorId;

  @CsvBindByName(column = "Organisation_Name")
  private String organisationName;

  @CsvBindByName(column = "Address_Line1")
  private String addressLine1;

  @CsvBindByName(column = "Address_Line2")
  private String addressLine2;

  @CsvBindByName(column = "Address_Line3")
  private String addressLine3;

  @CsvBindByName(column = "Town_Name")
  private String townName;

  @CsvBindByName(column = "Postcode")
  private String postCode;

  @CsvBindByName(column = "OA")
  private String oa;

  @CsvBindByName(column = "Latitude")
  private Double latitude;

  @CsvBindByName(column = "Longitude")
  private Double longitude;

  @CsvBindByName(column = "Hand_Deliver")
  private Boolean handDelivered;

  @CsvBindByName(column = "CE1_Complete")
  private Boolean ceCE1Complete;

  @CsvBindByName(column = "CE_Expected_Capacity")
  private int ceExpectedCapacity;

  @CsvBindByName(column = "CE_Actual_Responses")
  private int ceActualResponses;

  @CsvBindByName(column = "UAA")
  private Boolean uaa;

}

