package uk.gov.ons.census.fwmt.csvservice.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

public class CSVAdapterServiceImpl {
  @Value("${sftp.pgp.logistics}") Resource csv;
}
