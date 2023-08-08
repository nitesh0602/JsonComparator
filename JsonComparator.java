import org.json.JSONObject;

import java.util.Iterator;

public class JsonComparator {

    public static void main(String[] args) {
        String dynamoJsonString = "{\n" +
                "  \"sk\": \"ACCOUNT#00044CD663E46A00\",\n" +
                "  \"accountClosedDate\": \"2023-07-15T14:46:31.851+01:00\",\n" +
                "  \"accountCreatedDate\": \"2023-06-02T14:24:35.899+01:00\",\n" +
                "  \"accountOpenedDate\": \"2023-06-02T14:24:37.758+01:00\",\n" +
                "  \"agencyAccountNumber\": \"32275260\",\n" +
                "  \"agencySortCode\": \"232112\",\n" +
                "  \"applicationId\": \"707dca34-20b7-11ee-b828-3f9326bfe685\",\n" +
                "  \"pk\": \"ACCOUNT#00044CD663E46A40\",\n" +
                "  \"applicationRefenceNumber\": \"3f062dfe-9292-47bb-a9fb-5a80c659eabe\",\n" +
                "  \"businessUniCode\": \"BUK\",\n" +
                "  \"creditLimit\": \"5000\",\n" +
                "  \"creditscore\": 80\n" +
                "}"; // Replace this with your Dynamo JSON string
        String s3JsonString = "{\n" +
                "  \"sk\": \"ACCOUNT#00044CD663E46A40\",\n" +
                "  \"accountClosedDate\": \"2023-07-15T14:46:31.851+01:00\",\n" +
                "  \"accountCreatedDate\": \"2023-06-02T14:24:35.899+01:00\",\n" +
                "  \"accountOpenedDate\": \"2023-06-02T14:24:37.758+01:00\",\n" +
                "  \"agencyAccountNumber\": \"32275260\",\n" +
                "  \"agencySortCode\": \"232111\",\n" +
                "  \"applicationId\": \"707dca34-20b7-11ee-b828-3f9326bfe685\",\n" +
                "  \"applicationRefenceNumber\": \"3f062dfe-9292-47bb-a9fb-5a80c659eabe\",\n" +
                "  \"pk\": \"ACCOUNT#00044CD663E46A40\",\n" +
                "  \"businessUniCode\": \"BUK\",\n" +
                "  \"creditLimit\": 5000,\n" +
                "  \"creditpop\": 888\n" +
                "}";     // Replace this with your S3 JSON string

        JSONObject dynamoJson = new JSONObject(dynamoJsonString);
        JSONObject s3Json = new JSONObject(s3JsonString);

        compareJsonAttributes(dynamoJson, s3Json);
    }

    private static void compareJsonAttributes(JSONObject dynamoJson, JSONObject s3Json) {
        Iterator<String> keys = dynamoJson.keys();
        boolean passing = true;

        while (keys.hasNext()) {
            String key = keys.next();
            Object dynamoValue = dynamoJson.get(key);
            Object s3Value = s3Json.opt(key);

            if (s3Value == null) {
                passing = false;
                System.out.println("Faliure: Attribute '" + key + "' is missing in S3 JSON.");
            } else {
                // Convert dynamoValue to String for comparison
                String dynamoStringValue = dynamoValue.toString();
                String s3StringValue = s3Value.toString();

                if (!dynamoStringValue.equals(s3StringValue)) {
                    passing = false;
                    System.out.println("Failure: Attribute '" + key + "' value does not match between Dynamo and S3.");
                }
            }
        }
//        if(passing == true){
//            System.out.println("Passed");
//        }

        // Check for attributes present in S3 but not in Dynamo
        Iterator<String> s3Keys = s3Json.keys();
        while (s3Keys.hasNext()) {
            String key = s3Keys.next();
            if (!dynamoJson.has(key)) {
                System.out.println("Warning: Attribute '" + key + "' is present in S3 but not in Dynamo.");
            }
        }
        if(passing == true){
            System.out.println("Passed");
        }
    }
}
