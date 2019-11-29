package uk.gov.ons.fwmt.csvservice.helper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import uk.gov.ons.census.fwmt.common.error.GatewayException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResetCSVExtensions {

    private String csvToLookFor;
    private String resourcePath = "/Users/scorfs/Documents/Dev/census-fwmt-csv-service/src/test/resources/";

    public void resetCSVExtensions(String ingestType) throws GatewayException, IOException {
        Path csvPath;
        String csvContents;
        String fileToGet;
        String pathFormatted;
        String processCSVExtension;

        if (ingestType.equals("CEIngest")) {
            csvContents = "caseId,caseReference,establishmentType,fieldOfficerId,coordinatorId,organisationName,arid," +
                    "uprn,line1,line2,line3,townName,postCode,oa,lattitude,longitude,ceExpectedCapacity\n" +
            "2f1ea0fd-18b1-4786-b1f7-3e9a79ed1a52,123,CE,1,a,Test1,123,123,1,Station Road,,Chingford,E4,1,50,1,25";
            csvToLookFor = "testCECSV.processed";
            processCSVExtension = "testCECSV.csv";
        } else {
            csvContents = "postCode,latitude,longitude,ccsInterviewer,coordinatorId\n" +
                    "E4 7NG,51.6349,0.0075,JoeBlogs,1";
            csvToLookFor = "testCCSCSV.processed";
            processCSVExtension = "testCCSCSV.csv";
        }

        try (Stream<Path> walk = Files.walk(Paths.get(resourcePath))) {

            List<String> result = walk.map(Path::toString)
                    .filter(f -> f.contains(csvToLookFor))
                    .collect(Collectors.toList());
            fileToGet = result.toString();

        }

        FileSystemResource testResource = new FileSystemResource(resourcePath);

        try (OutputStream os = ((WritableResource) testResource.createRelative(processCSVExtension)).getOutputStream()) {
            os.write(csvContents.getBytes(StandardCharsets.UTF_8));
        }

        try {
            pathFormatted = fileToGet.replaceAll("\\[", "").replaceAll("]", "");
            csvPath = Path.of(pathFormatted);
            Files.delete(csvPath);
        } catch (IOException e) {
            throw new GatewayException(GatewayException.Fault.SYSTEM_ERROR, e, "Unable to delete CE Ingest CSV");
        }
    }
}
