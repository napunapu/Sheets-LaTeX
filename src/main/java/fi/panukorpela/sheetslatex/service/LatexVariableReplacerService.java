package fi.panukorpela.sheetslatex.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class LatexVariableReplacerService {
    private Logger log = LoggerFactory.getLogger(LatexVariableReplacerService.class);
    @Autowired
    private GoogleSheetsService googleSheetsService;
    private String latexTemplateFilename;
    private String latexOutputFilename;
    
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
        latexTemplateFilename = props.getProperty("latexTemplateFilename");
        latexOutputFilename = props.getProperty("latexOutputFilename");
    }
    
    public void replaceVarsInLatexFile() throws Exception {
        Map<String, String> variables = googleSheetsService.readLatexVariables();
        String content = new String(Files.readAllBytes(Paths.get(latexTemplateFilename)));

        // Regex to match \VAR{...}
        Pattern pattern = Pattern.compile("\\\\VAR\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);

        StringBuffer sb = new StringBuffer();
        int replacements = 0;
        while (matcher.find()) {
            String varName = matcher.group(1);
            String replacement = variables.get(varName);
            // If the variable is missing, keep the placeholder as is
            if (replacement != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
                log.info("Replaced \\VAR{{}} with '{}'", varName, replacement);
                replacements++;
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("\\VAR{" + varName + "}"));
                log.warn("No value found for variable '{}', placeholder left unchanged.", varName);
            }
        }
        matcher.appendTail(sb);

        String outputContent = sb.toString();
        Files.write(Paths.get(latexOutputFilename), outputContent.getBytes());
        log.info("Wrote {} replacements to '{}'", replacements, latexOutputFilename);


        // After replacement, check for any unprocessed \VAR{...} placeholders
        Pattern remainingPattern = Pattern.compile("\\\\VAR\\{([^}]+)\\}");
        Matcher remainingMatcher = remainingPattern.matcher(outputContent);
        Set<String> unprocessed = new HashSet<>();
        while (remainingMatcher.find()) {
            unprocessed.add(remainingMatcher.group(1));
        }
        for (String var : unprocessed) {
            log.error("Unprocessed placeholder still in output: \\VAR{{}}}", var);
        }
        if (unprocessed.isEmpty()) {
            log.info("All placeholders processed.");
        }
        // Check for spreadsheet errors like #NAME?, #REF!, etc.
        Pattern sheetErrorPattern = Pattern.compile("#(NAME\\?|REF!|VALUE!|N/A|ERROR!)");
        Matcher errorMatcher = sheetErrorPattern.matcher(outputContent);
        Set<String> sheetErrors = new HashSet<>();

        while (errorMatcher.find()) {
            sheetErrors.add(errorMatcher.group());
        }

        if (!sheetErrors.isEmpty()) {
            log.error("Detected spreadsheet error values in the output: {}", sheetErrors);
            throw new RuntimeException("Output contains spreadsheet errors: " + sheetErrors);
        }

    }
}
