package uk.gov.ons.census.fwmt.csvservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectionReportEntry {

  private String caseRef;

  private String reason;

}
