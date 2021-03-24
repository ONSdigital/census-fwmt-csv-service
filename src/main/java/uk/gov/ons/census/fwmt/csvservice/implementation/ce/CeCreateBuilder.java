package uk.gov.ons.census.fwmt.csvservice.implementation.ce;

import uk.gov.ons.census.fwmt.common.rm.dto.ActionInstructionType;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.CeCreate;

public final class CeCreateBuilder {

  private CeCreateBuilder() {
  }

  public static FwmtActionInstruction buildCeCreate(CeCreate ceCreate) {
    return FwmtActionInstruction
        .builder()
        .actionInstruction(ActionInstructionType.CREATE)
        .surveyName("CENSUS")
        .addressType("CE")
        .addressLevel("E")
        .caseId(ceCreate.getCaseId())
        .caseRef(ceCreate.getCaseReference())
        .uprn(ceCreate.getUprn())
        .secureEstablishment(ceCreate.getSecureEstablishment())
        .estabType(ceCreate.getEstablishmentType())
        .fieldCoordinatorId(ceCreate.getFieldOfficerId())
        .fieldCoordinatorId(ceCreate.getCoordinatorId())
        .organisationName(ceCreate.getOrganisationName())
        .addressLine1(ceCreate.getAddressLine1())
        .addressLine2(ceCreate.getAddressLine2())
        .addressLine3(ceCreate.getAddressLine3())
        .townName(ceCreate.getTownName())
        .postcode(ceCreate.getPostCode())
        .oa(ceCreate.getOa())
        .latitude(ceCreate.getLatitude())
        .longitude(ceCreate.getLongitude())
        .handDeliver(ceCreate.getHandDelivered())
        .ce1Complete(ceCreate.getCeCE1Complete())
        .ceExpectedCapacity(ceCreate.getCeExpectedCapacity())
        .ceActualResponses(ceCreate.getCeActualResponses())
        .undeliveredAsAddress(ceCreate.getUaa())
        .build();
  }

}
