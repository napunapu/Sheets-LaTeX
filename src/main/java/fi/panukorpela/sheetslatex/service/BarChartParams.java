package fi.panukorpela.sheetslatex.service;

public class BarChartParams {
    private String tab;
    private String range;
    private boolean errorForDataAfterRange = true;
    private boolean swapColumns = false;
    private String outputFile = "chart.tex";
    private String xLabel = "";
    private String yLabel = "";
    private int labelRotation = 45;
    private boolean reverseOrder = false;
    private int xLabelMaxLineLength = 0;
    private double xLimits = 0.0;

    // Private constructor
    private BarChartParams() {}

    // Getters (add as needed)
    public String getTab() { return tab; }
    public String getRange() { return range; }
    public boolean isErrorForDataAfterRange() { return errorForDataAfterRange; }
    public boolean isSwapColumns() { return swapColumns; }
    public String getOutputFile() { return outputFile; }
    public String getXLabel() { return xLabel; }
    public String getYLabel() { return yLabel; }
    public int getLabelRotation() { return labelRotation; }
    public boolean isReverseOrder() { return reverseOrder; }
    public int getXLabelMaxLineLength() { return xLabelMaxLineLength; }
    public double getXLimits() { return xLimits; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final BarChartParams params = new BarChartParams();

        public Builder tab(String tab) { params.tab = tab; return this; }
        public Builder range(String range) { params.range = range; return this; }
        public Builder errorForDataAfterRange(boolean errorForDataAfterRange) { params.errorForDataAfterRange = errorForDataAfterRange; return this; }
        public Builder swapColumns(boolean swapColumns) { params.swapColumns = swapColumns; return this; }
        public Builder outputFile(String outputFile) { params.outputFile = outputFile; return this; }
        public Builder xLabel(String xLabel) { params.xLabel = xLabel; return this; }
        public Builder yLabel(String yLabel) { params.yLabel = yLabel; return this; }
        public Builder labelRotation(int labelRotation) { params.labelRotation = labelRotation; return this; }
        public Builder reverseOrder(boolean reverseOrder) { params.reverseOrder = reverseOrder; return this; }
        public Builder xLabelMaxLineLength(int xLabelMaxLineLength) { params.xLabelMaxLineLength = xLabelMaxLineLength; return this; }
        public Builder xLimits(double xLimits) { params.xLimits = xLimits; return this; }

        public BarChartParams build() {
            if (params.tab == null || params.range == null) throw new IllegalArgumentException("Tab and range must be set.");
            return params;
        }
    }
}
