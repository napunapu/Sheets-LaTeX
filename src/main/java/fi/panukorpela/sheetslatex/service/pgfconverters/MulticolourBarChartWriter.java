package fi.panukorpela.sheetslatex.service.pgfconverters;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MulticolourBarChartWriter {

    public static void writeMulticolorBarChartFromSheets(List<String[]> table, BarChartParams params) {
        boolean swapColumns = params.isSwapColumns();
        String outputFile = params.getOutputFile();
        String xLabel = params.getXLabel();
        String yLabel = params.getYLabel();
        boolean reverseOrder = params.isReverseOrder();
        int xLabelMaxLineLength = params.getXLabelMaxLineLength();
        double xLimits = params.getXLimits();
        boolean showValues = params.isShowValuesOnBars();
        
        try {
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
            String[] categories = new String[nBars];

            for (int i = 0; i < nBars; i++) {
                String[] row = table.get(i);
                String countString = row[1].replace(",", ".");
                counts[i] = Double.parseDouble(countString);
                categories[i] = row[0]; // Keep original for symbolic coords
                String label = row[0].replace("_", "\\_");
                if (xLabelMaxLineLength > 0) {
                    label = latexAxisLabelLineBreak(label, xLabelMaxLineLength);
                }
                labels[i] = label;
            }

            // Define colors - you can customize these
            String[] colorDefinitions = {
                "\\definecolor{color1}{RGB}{65,105,225}",
                "\\definecolor{color2}{RGB}{220,53,69}",
                "\\definecolor{color3}{RGB}{255,165,0}",
                "\\definecolor{color4}{RGB}{40,167,69}",
                "\\definecolor{color5}{RGB}{255,193,7}",
                "\\definecolor{color6}{RGB}{23,162,184}",
                "\\definecolor{color7}{RGB}{111,66,193}",
                "\\definecolor{color8}{RGB}{255,218,185}",
                "\\definecolor{color9}{RGB}{255,182,193}",
                "\\definecolor{color10}{RGB}{144,238,144}",
                "\\definecolor{color11}{RGB}{221,160,221}",
                "\\definecolor{color12}{RGB}{135,206,250}",
                "\\definecolor{color13}{RGB}{176,224,230}"
            };

            // Write LaTeX file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write("\\documentclass[border=10pt]{standalone}\n");
                writer.write("\\usepackage{tikz}\n");
                writer.write("\\usepackage{pgfplots}\n");
                writer.write("\\pgfplotsset{compat=1.18}\n\n");
                
                // Write color definitions
                for (int i = 0; i < Math.min(nBars, colorDefinitions.length); i++) {
                    writer.write(colorDefinitions[i] + "\n");
                }
                writer.write("\n");
                
                writer.write("\\begin{document}\n");
                writer.write("\\begin{tikzpicture}\n");
                writer.write("\\begin{axis}[\n");
                writer.write("    ybar=0pt,\n");
                writer.write("    bar width=0.6cm,\n");
                writer.write("    bar shift=0pt,\n");
                writer.write("    width=16cm,\n");
                writer.write("    height=10cm,\n");
                
                if (xLimits == 0.0) {
                    writer.write("    enlarge x limits=0.15,\n");
                } else {
                    writer.write("    enlarge x limits=" + xLimits + ",\n");
                }
                
                if (!xLabel.isEmpty()) {
                    writer.write("    xlabel={" + xLabel + "},\n");
                }
                if (!yLabel.isEmpty()) {
                    writer.write("    ylabel={" + yLabel + "},\n");
                }
                
                // Write symbolic x coords
                writer.write("    symbolic x coords={");
                for (int i = 0; i < categories.length; i++) {
                    writer.write(categories[i]);
                    if (i != categories.length - 1) writer.write(",");
                }
                writer.write("},\n");
                
                // No labels in these plots, everything in legend
                writer.write("    xtick=\\empty,\n");
                
                // Find max value for y-axis
                double maxValue = Arrays.stream(counts).max().orElse(100);
                double yMax = Math.ceil(maxValue / 25) * 25; // Round up to nearest 25
                if (yMax < 100) yMax = 100;
                
                writer.write("    ymin=0,\n");
                writer.write("    ymax=" + yMax + ",\n");
                writer.write("    ytick={0,25,50,75,100");
                if (yMax > 100) {
                    for (int i = 125; i <= yMax; i += 25) {
                        writer.write("," + i);
                    }
                }
                writer.write("},\n");
                writer.write("    ymajorgrids=true,\n");
                writer.write("    grid style={gray!30},\n");
                writer.write("    legend style={\n");
                writer.write("        at={(1.02,1)},\n");
                writer.write("        anchor=north west,\n");
                writer.write("        legend columns=1,\n");
                writer.write("        font=\\small,\n");
                writer.write("        draw=none,\n");
                writer.write("        /tikz/mark size=0pt,\n");
                writer.write("        legend cell align=left\n");
                writer.write("    },\n");
                
                if (showValues) {
                    writer.write("    nodes near coords,\n");
                    writer.write("    nodes near coords align={vertical},\n");
                    writer.write("    every node near coord/.append style={font=\\footnotesize},\n");
                }
                
                writer.write("]\n\n");

                // Add individual bars with different colors
                for (int i = 0; i < nBars; i++) {
                    writer.write("\\addplot[\n");
                    writer.write("    forget plot,\n");
                    writer.write("    ybar,\n");
                    writer.write("    fill=color" + (i + 1) + ",\n");
                    writer.write("    draw=color" + (i + 1) + "\n");
                    writer.write("] coordinates {(" + categories[i] + ", " + counts[i] + ")};\n\n");
                }

                // Create legend manually
                writer.write("% Legend\n");
                for (int i = 0; i < nBars; i++) {
                    writer.write("\\addlegendimage{area legend, fill=color" + (i + 1) + ", draw=none}\n");
                    writer.write("\\addlegendentry{" + labels[i] + "}\n");
                }

                writer.write("\n\\end{axis}\n");
                writer.write("\\end{tikzpicture}\n");
                writer.write("\\end{document}\n");
                
                System.out.println("LaTeX Multicolor Bar Chart file written: " + outputFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Breaks a LaTeX axis label into multiple lines based on maximum line length.
     * Attempts to break at word boundaries when possible.
     * 
     * @param label The label text to break
     * @param maxLineLength Maximum characters per line
     * @return The label with LaTeX line breaks (\\) inserted
     */
    static String latexAxisLabelLineBreak(String label, int maxLineLength) {
        if (label.length() <= maxLineLength) {
            return label;
        }
        
        StringBuilder result = new StringBuilder();
        String[] words = label.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            
            // If adding this word would exceed the limit
            if (currentLine.length() > 0 && 
                currentLine.length() + 1 + word.length() > maxLineLength) {
                // Add current line to result
                if (result.length() > 0) {
                    result.append(" \\\\ ");
                }
                result.append(currentLine.toString());
                currentLine = new StringBuilder();
            }
            
            // Add word to current line
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        
        // Add remaining content
        if (currentLine.length() > 0) {
            if (result.length() > 0) {
                result.append(" \\\\ ");
            }
            result.append(currentLine.toString());
        }
        
        return result.toString();
    }

    /**
     * Alternative version that breaks at exact character positions if needed
     * (useful for labels without spaces or with very long words)
     */
    static String latexAxisLabelLineBreakExact(String label, int maxLineLength) {
        if (label.length() <= maxLineLength) {
            return label;
        }
        
        StringBuilder result = new StringBuilder();
        int start = 0;
        
        while (start < label.length()) {
            int end = Math.min(start + maxLineLength, label.length());
            
            // Try to break at a space if possible
            if (end < label.length()) {
                int spacePos = label.lastIndexOf(' ', end);
                if (spacePos > start) {
                    end = spacePos;
                }
            }
            
            if (start > 0) {
                result.append(" \\\\ ");
            }
            result.append(label.substring(start, end).trim());
            
            start = end;
            // Skip the space if we broke at one
            if (start < label.length() && label.charAt(start) == ' ') {
                start++;
            }
        }
        
        return result.toString();
    }
}
