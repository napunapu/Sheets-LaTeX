package fi.panukorpela.sheetslatex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import jakarta.annotation.PostConstruct;

@Service
public class GoogleSheetsService {
    private static final String APPLICATION_NAME = "Sheets-Latex-Integration";
    private Logger log = LoggerFactory.getLogger(GoogleSheetsService.class);
    private Sheets sheetsService;
    private String spreadsheetId;
    
    @PostConstruct
    void init() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        File propFile = new File("config.properties");
        if (propFile.exists()) {
            try (InputStream propInputStream = new FileInputStream(propFile)) {
                props.load(propInputStream);
            }
        } else {
            throw new RuntimeException("Property file 'config.properties' not found");
        }
        spreadsheetId = props.getProperty("spreadsheetId");
    }

    // Inject the path from properties
    public GoogleSheetsService(@Value("${google.sheets.credentials.path}") String credentialsPath) throws Exception {
        log.info(credentialsPath);
        this.sheetsService = getSheetsService(credentialsPath);
    }

    private Sheets getSheetsService(String credentialsPath) throws Exception {
        InputStream in = new FileInputStream(credentialsPath);
        GoogleCredentials credentials = GoogleCredentials.fromStream(in)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY));

        return new Sheets.Builder(
                com.google.api.client.googleapis.javanet.GoogleNetHttpTransport.newTrustedTransport(),
                com.google.api.client.json.jackson2.JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


    /**
     * Reads LaTeX variable-value pairs from the sheet, stopping at the first empty variable name.
     * @return Map of variable name (from col C) -> value (from col B)
     */
    public Map<String, String> readLatexVariables() throws Exception {
        String range = "LaTeX!B2:C"; // Start at B2:C2, continues down
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> rows = response.getValues();

        Map<String, String> variables = new HashMap<>();

        if (rows != null) {
            for (List<Object> row : rows) {
                // Defensive: skip if col C is missing/empty
                if (row.size() < 2 || row.get(1) == null || row.get(1).toString().trim().isEmpty()) {
                    break; // Stop at first empty variable name (col C)
                }
                String value = row.size() > 0 ? row.get(0).toString() : "";
                // Check: value looks like a decimal number with comma as decimal sep
                // E.g. "123,45" or "-0,05"
                if (value.matches("-?\\d{1,3}(,\\d+)?")) {
                    value = value.replace(',', '.');
                }
                String variable = row.get(1).toString().trim();
                // Check for duplicate key
                if (variables.containsKey(variable)) {
                    throw new IllegalArgumentException("Duplicate variable name found in Google Sheet: '" + variable + "'");
                }
                // If variable name ends with _percent or _percentage, append \%
                if (variable.endsWith("_percent") || variable.endsWith("_percentage")) {
                    value = value + "\\%";
                }
                variables.put(variable, value);
            }
        }

        return variables;
    }
    
    /**
     * Returns a table of values from the given tab and range in the Google Sheet.
     * @param tabName e.g. "ArticleCounts"
     * @param rangeString e.g. "A2:B18"
     * @return List of String arrays (one per row)
     * @throws IOException if Sheets API fails
     */
    public List<String[]> getTableFromSheet(String tabName, String rangeString) throws IOException {
        String fullRange = tabName + "!" + rangeString;
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, fullRange)
                .execute();

        List<List<Object>> values = response.getValues();
        List<String[]> result = new ArrayList<>();
        if (values != null) {
            for (List<Object> row : values) {
                String[] rowArr = new String[row.size()];
                for (int i = 0; i < row.size(); i++) {
                    rowArr[i] = row.get(i).toString();
                }
                result.add(rowArr);
            }
        }
        return result;
    }

    /**
     * Fetches a value (or row) from a Google Sheet.
     * @param spreadsheetId The Sheet ID from the URL
     * @param range E.g. "Sheet1!B2"
     * @return List of values in the range
     */
    public List<Object> readRange(String range) throws Exception {
        ValueRange response = sheetsService.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        return response.getValues() != null && !response.getValues().isEmpty()
                ? response.getValues().get(0)
                : Collections.emptyList();
    }
}

