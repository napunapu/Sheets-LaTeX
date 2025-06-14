package fi.panukorpela.sheetslatex.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.panukorpela.sheetslatex.service.pgfconverters.BarChartParams;

@Service
public class PgfPlotsChartWriterService {
    @Autowired
    private GoogleSheetsService googleSheetsService;

    public void writeBarChartFromSheets(BarChartParams params) {
        String tab = params.getTab();
        String range = params.getRange();
        boolean errorForDataAfterRange = params.isErrorForDataAfterRange();
        boolean swapColumns = params.isSwapColumns();
        String outputFile = params.getOutputFile();
        String xLabel = params.getXLabel();
        String yLabel = params.getYLabel();
        int labelRotation = params.getLabelRotation();
        boolean reverseOrder = params.isReverseOrder();
        int xLabelMaxLineLength = params.getXLabelMaxLineLength();
        double xLimits = params.getXLimits();
        try {
            // Read data from the Sheet
            List<String[]> table = googleSheetsService.getTableFromSheet(tab, range, errorForDataAfterRange);
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
            double[] counts = new double[nBars];
            String[] labels = new String[nBars];

            for (int i = 0; i < nBars; i++) {
                String[] row = table.get(i);
                // Column D: Article count, Column E: Year
                String countString = row[0].replace(",", ".");
                counts[i] = Double.parseDouble(countString);
                String label = row[1].replace("_", "\\_");
                if (xLabelMaxLineLength > 0) {
                    label = latexAxisLabelLineBreak(label, xLabelMaxLineLength);
                }
                labels[i] = "{" + label + "}";
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
                if (xLabelMaxLineLength == 0) {
                    writer.write("    xticklabel style={rotate=" + labelRotation + ",anchor=east},\n");
                } else {
                    writer.write("    xticklabel style={font=\\scriptsize, align=center, text width=3cm},\n");
                }
                writer.write("    bar width=" + layout.barWidthPt + "pt,\n");
                writer.write("    grid=major,\n");
                writer.write("    xmajorgrids=false,\n");
                //writer.write("    nodes near coords,\n");
                if (xLimits == 0.0) {
                    writer.write("    enlarge x limits=" + layout.enlargeXLimits + ",\n");
                } else {
                    writer.write("    enlarge x limits=" + xLimits + ",\n");
                }
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
    
    public static String latexAxisLabelLineBreak(String label, int maxLineLength) {
        // Escape LaTeX special characters
        label = label.replace("_", "\\_");
        String[] words = label.split(" ");
        StringBuilder result = new StringBuilder();
        int lineLen = 0;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // +1 for the space that will be added (except first word)
            int nextLen = lineLen + (lineLen == 0 ? 0 : 1) + word.length();
            if (lineLen != 0 && nextLen > maxLineLength) {
                result.append("\\\\");
                lineLen = 0;
            } else if (lineLen != 0) {
                result.append(" ");
                lineLen++; // for the space
            }
            result.append(word);
            lineLen += word.length();
        }
        return result.toString();
    }

}
