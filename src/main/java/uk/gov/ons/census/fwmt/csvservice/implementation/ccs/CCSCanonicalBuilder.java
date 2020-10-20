package uk.gov.ons.census.fwmt.csvservice.implementation.ccs;

import uk.gov.ons.census.fwmt.common.data.tm.SurveyType;
import uk.gov.ons.census.fwmt.common.rm.dto.ActionInstructionType;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.CCSPropertyListing;

import java.util.UUID;

public final class CCSCanonicalBuilder {

  public static FwmtActionInstruction createCCSJob(CCSPropertyListing ccsPropertyListing, int caseRefCountInt) {
    return FwmtActionInstruction
        .builder()
        .actionInstruction(ActionInstructionType.CREATE)
        .caseId(String.valueOf(UUID.randomUUID()))
        .caseRef("PL" + caseRefCountInt)
        .surveyName("CCS-PL")
        .surveyType(SurveyType.CCS_PL)
        .fieldOfficerId(ccsPropertyListing.getFieldOfficerId())
        .fieldCoordinatorId(ccsPropertyListing.getFieldCoordinatorId())
        .postcode(ccsPropertyListing.getPostCode())
        .oa(ccsPropertyListing.getOa())
        .build();
  }
}
