package uk.gov.ons.census.fwmt.csvservice.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import uk.gov.ons.census.fwmt.csvservice.dto.GatewayCache;

import javax.persistence.LockModeType;

@Repository
public interface GatewayCacheRepository extends JpaRepository<GatewayCache, Long> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  GatewayCache findByCaseId(String caseId);

}
