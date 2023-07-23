import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonFileComparator {

    public static void main(String[] args) {
        String dynamoJsonString = "{\n" +
                "  \"id\": 123,\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"age\": 30,\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"address\": {\n" +
                "    \"city\": \"New York\",\n" +
                "    \"postalCode\": \"10001\"\n" +
                "  },\n" +
                "  \"isMarried\": false,\n" +
                "  \"phone\": \"123-456-7890\"\n" +
                "}";

        String s3JsonString = "{\n" +
                "  \"id\": 123,\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"email\": \"john.doe@example.com\",\n" +
                "  \"address\": null,\n" +
                "  \"isMarried\": false,\n" +
                "  \"phone\": null,\n" +
                "  \"extraField\": \"This field does not exist in the source JSON.\"\n" +
                "}";

        try {
            JSONObject sourceJson = new JSONObject(dynamoJsonString);
            JSONObject destinationJson = new JSONObject(s3JsonString);

            List<String> failures = new ArrayList<>();
            List<String> warnings = new ArrayList<>();

            // Compare source and destination JSON strings
            compareJsonObjects(sourceJson, destinationJson, failures, warnings);

            if (!failures.isEmpty()) {
                System.out.println("FAIL: JSON files have mismatches.");
                for (String failure : failures) {
                    System.out.println("- " + failure);
                }
            } else {
                System.out.println("SUCCESS: JSON files match successfully!");
            }

            if (!warnings.isEmpty()) {
                System.out.println("WARNINGS:");
                for (String warning : warnings) {
                    System.out.println("- " + warning);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void compareJsonObjects(JSONObject source, JSONObject destination,
                                           List<String> failures, List<String> warnings) throws JSONException {
        if (source.length() != destination.length()) {
            failures.add("Mismatch in attribute count");
            return;
        }

        for (String key : source.keySet()) {
            if (!destination.has(key)) {
                failures.add("Attribute missing in destination JSON: " + key);
            } else {
                Object sourceValue = source.get(key);
                Object destinationValue = destination.get(key);

                if (sourceValue instanceof JSONObject && destinationValue instanceof JSONObject) {
                    // Recursively compare nested JSON objects
                    compareJsonObjects((JSONObject) sourceValue, (JSONObject) destinationValue, failures, warnings);
                } else {
                    // Compare attribute values
                    if (!sourceValue.equals(destinationValue)) {
                        failures.add("Attribute value mismatch for " + key + ": " + sourceValue + " vs. " + destinationValue);
                    }

                    if (destinationValue == JSONObject.NULL) {
                        warnings.add("Attribute going null in destination JSON: " + key);
                    }
                }
            }
        }
    }
}
