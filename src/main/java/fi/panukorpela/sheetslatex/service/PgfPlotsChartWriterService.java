package fi.panukorpela.sheetslatex.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PgfPlotsChartWriterService {
    @Autowired
    private GoogleSheetsService googleSheetsService;

    public void writeBarChartFromSheets(String tab, String range, boolean swapColumns, 
            String outputFile, String xLabel, String yLabel, int labelRotation, boolean reverseOrder) {
        try {
            // Read data from the Sheet
            List<String[]> table = googleSheetsService.getTableFromSheet(tab, range);
            if (swapColumns) {
                table.forEach(row -> {
                    if (row.length >= 2) {
                        String temp = row[0];
                        row[0] = row[1];
                        row[1] = temp;
                    }
                });
            }
            
            // Optionally reverse the table for X axis order
            if (reverseOrder) {
                Collections.reverse(table);
            }
            int nBars = table.size();

            // Arrays to hold the data
            int[] counts = new int[nBars];
            String[] labels = new String[nBars];

            for (int i = 0; i < nBars; i++) {
                String[] row = table.get(i);
                // Column D: Article count, Column E: Year
                counts[i] = Integer.parseInt(row[0]);
                labels[i] = "{" + row[1].replace("_", "\\_") + "}";
            }
            BarChartLayout layout = getPgfpBarChartLayout(nBars);

            // Write LaTeX file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write("\\documentclass[tikz, border=1mm]{standalone}\n");
                writer.write("\\usepackage[HTML]{xcolor}\n");
                writer.write("\\usepackage{pgfplots}\n");
                writer.write("\\pgfplotsset{compat=1.18}\n");
                writer.write("\\definecolor{barblue}{HTML}{46a5ff}\n\n");
                writer.write("\\begin{document}\n");
                writer.write("\\begin{tikzpicture}\n");
                writer.write("\\begin{axis}[\n");
                writer.write("    width=14cm,\n");
                writer.write("    height=7cm,\n");
                writer.write("    ybar,\n");
                writer.write("    xlabel=" + xLabel + ",\n");
                writer.write("    ylabel=" + yLabel + ",\n");
                writer.write("    ymin=0,\n");
                writer.write("    xtick=data,\n");
                writer.write("    xticklabels={");

                // Add all years to xticklabels
                for (int i = 0; i < labels.length; i++) {
                    writer.write(labels[i]);
                    if (i != labels.length - 1) writer.write(",");
                }
                writer.write("},\n");
                writer.write("    xticklabel style={rotate=" + labelRotation + ",anchor=east},\n");
                writer.write("    bar width=" + layout.barWidthPt + "pt,\n");
                writer.write("    grid=major,\n");
                writer.write("    xmajorgrids=false,\n");
                //writer.write("    nodes near coords,\n");
                writer.write("    enlarge x limits=" + layout.enlargeXLimits + ",\n");
                writer.write("    reverse legend,\n");
                writer.write("]\n");

                writer.write("\\addplot+[\n");
                writer.write("    fill=barblue,\n");
                writer.write("]\n");
                writer.write("coordinates {\n");
                // Write coordinates (x is index+1, y is count)
                for (int i = 0; i < counts.length; i++) {
                    writer.write("    (" + (i + 1) + "," + counts[i] + ")  % " + labels[i] + "\n");
                }
                writer.write("};\n");
                writer.write("\\end{axis}\n");
                writer.write("\\end{tikzpicture}\n");
                writer.write("\\end{document}\n");
                System.out.println("LaTeX PGFPlots file written: " + outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static class BarChartLayout {
        public final double barWidthPt;
        public final double enlargeXLimits;

        public BarChartLayout(double barWidthPt, double enlargeXLimits) {
            this.barWidthPt = barWidthPt;
            this.enlargeXLimits = enlargeXLimits;
        }
    }

    public static BarChartLayout getPgfpBarChartLayout(int nBars) {
        // Clamp nBars between 5 and 17 for our formula
        int minBars = 5, maxBars = 17;
        nBars = Math.max(minBars, Math.min(maxBars, nBars));

        // Linear interpolation
        double barWidthAtMin = 40.0, barWidthAtMax = 10.0;
        double enlargeAtMin = 0.2, enlargeAtMax = 0.05;

        double t = (nBars - minBars) / (double) (maxBars - minBars);

        double barWidth = barWidthAtMin + (barWidthAtMax - barWidthAtMin) * t;
        double enlarge = enlargeAtMin + (enlargeAtMax - enlargeAtMin) * t;

        // Optionally round for neatness
        barWidth = Math.round(barWidth * 10.0) / 10.0;
        enlarge = Math.round(enlarge * 1000.0) / 1000.0;

        return new BarChartLayout(barWidth, enlarge);
    }

}
