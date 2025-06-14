package fi.panukorpela.sheetslatex.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LatexCompiler {

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
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("PDF generated successfully!");
                return true;
            } else {
                System.err.println("pdflatex failed with exit code " + exitCode);
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Example usage
    public static void main(String[] args) {
        String outputDir = "downloads";
        //runPdflatex("downloads/Articles per year.tex", outputDir);
        //runPdflatex("downloads/Research design.tex", outputDir);
        //runPdflatex("downloads/Analysis methods.tex", outputDir);
        //runPdflatex("downloads/Data sources.tex", outputDir);
        //runPdflatex("downloads/Articles per participant count.tex", outputDir);
        //runPdflatex("downloads/Dropout definition source.tex", outputDir);
        //runPdflatex("downloads/Criteria for passing.tex", outputDir);
        runPdflatex("downloads/Criteria for dropout.tex", outputDir);
    }
}

