package uk.gov.ons.census.fwmt.csvservice.implementation.nc;

import uk.gov.ons.census.fwmt.common.rm.dto.ActionInstructionType;
import uk.gov.ons.census.fwmt.common.rm.dto.FwmtActionInstruction;
import uk.gov.ons.census.fwmt.csvservice.dto.NCIntListing;

import java.util.UUID;

public final class NonComplianceBuilder {

  private NonComplianceBuilder() {
  }

  public static FwmtActionInstruction createNcJob(NCIntListing ncIntListing) {
    return FwmtActionInstruction
        .builder()
        .actionInstruction(ActionInstructionType.CREATE)
        .surveyName("CENSUS")
        .addressType(ncIntListing.getAddressType())
        .addressLevel(ncIntListing.getAddressLevel())
        .caseId(String.valueOf(UUID.randomUUID()))
        .caseRef("NC" + ncIntListing.getCaseRef())
        .oldCaseId(ncIntListing.getCaseId())
        .oa(ncIntListing.getOaCode())
        .nc(true)
        .estabType(ncIntListing.getEstablishmentType())
        .fieldCoordinatorId(ncIntListing.getFieldCoordinatorId())
        .fieldOfficerId(ncIntListing.getFieldOfficerId())
        .addressLine1(ncIntListing.getAddressLine1())
        .addressLine2(ncIntListing.getAddressLine2())
        .addressLine3(ncIntListing.getAddressLine3())
        .townName(ncIntListing.getTown())
        .postcode(ncIntListing.getPostcode())
        .longitude(ncIntListing.getLongitude())
        .latitude(ncIntListing.getLatitude())
        .build();
  }
}
