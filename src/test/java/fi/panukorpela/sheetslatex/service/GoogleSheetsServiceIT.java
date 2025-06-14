package fi.panukorpela.sheetslatex.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GoogleSheetsServiceIT {

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @Test
    void testReadLatexVariablesWithRealSheet() throws Exception {
        // ACT: Call the real service method
        Map<String, String> result = googleSheetsService.readLatexVariables();

        // ASSERT: Only basic checks here - adapt to your sheet data!
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Result map should not be empty");

        // Example: check for specific variable names if you know them
        assertTrue(result.containsKey("primary_study_count"));
        assertNotNull(result.get("primary_study_count"));

        // Optionally print for debug
        result.forEach((k, v) -> System.out.println(k + " = " + v));
    }
    
    @Test
    void getTableFromSheet() throws Exception {
        List<String[]> table = googleSheetsService.getTableFromSheet("Visualisations", "D1:E18", true);
        System.out.println("Results from 'Visualisations'!D1:E18:");
        for (String[] row : table) {
            // Print both columns, handling possible missing values
            String col1 = row.length > 0 ? row[0] : "";
            String col2 = row.length > 1 ? row[1] : "";
            System.out.println("Column D: " + col1 + " | Column E: " + col2);
        }
    }
}

