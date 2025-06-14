package fi.panukorpela.sheetslatex.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatexCompiler {
    private static Logger log = LoggerFactory.getLogger(LatexCompiler.class);

    /**
     * Compiles the given LaTeX file with pdflatex.
     * @param texFile      Path to the .tex file
     * @param outputDir    Output directory for PDF (can be null to use default)
     * @return true if compilation succeeded, false otherwise
     */
    public static boolean runPdflatex(String texFile, String outputDir) {
        try {
            ProcessBuilder pb;
            if (outputDir != null && !outputDir.isEmpty()) {
                pb = new ProcessBuilder(
                        "/Library/TeX/texbin/pdflatex", "-interaction=nonstopmode", "-output-directory", outputDir, texFile
                );
            } else {
                pb = new ProcessBuilder(
                        "/Library/TeX/texbin/pdflatex", "-interaction=nonstopmode", texFile
                );
            }
            pb.redirectErrorStream(true);
            Process process = pb.start();
            // Print pdflatex output to console (optional but useful)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.debug(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("PDF generated successfully!");
                return true;
            } else {
                log.error("pdflatex failed with exit code " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}

