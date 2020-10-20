package uk.gov.ons.fwmt.csvservice.implementation.ce;

//@RunWith(MockitoJUnitRunner.class)
public class CEConverterServiceImplTest {

//  @InjectMocks
//  private CEConverterService ceConverterService;
//
//  @Mock
//  private GatewayActionAdapter gatewayActionAdapter;
//
//  @Mock
//  private GatewayEventManager gatewayEventManager;
//
//  @Mock
//  private StorageUtils storageUtils;
//
//  @Test
//  public void convertCECSVToCanonicalTest() throws GatewayException, IOException {
//    // Given
//    ClassLoader classLoader = getClass().getClassLoader();
//    String testPathString = classLoader.getResource("ceTestCSV.csv").getPath();
//    Resource testResource = new FileSystemResource(testPathString);
//
//    ReflectionTestUtils.setField(ceConverterService, "file", testResource);
//    ReflectionTestUtils.setField(ceConverterService, "directory", "resources/");
//
//    FileInputStream fileInputStream = new FileInputStream(testResource.getFile());
//    try (fileInputStream) {
//      when(storageUtils.getFileInputStream(any())).thenReturn(fileInputStream);
//
//      // When
//      ceConverterService.convertToCanonical();
//    }
//    Mockito.verify(gatewayEventManager).triggerEvent(anyString(), eq(CSV_CE_REQUEST_EXTRACTED));
//  }
}
