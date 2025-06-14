package fi.panukorpela.sheetslatex.service;

//This class maps to a single chart entry in your YAML
public class BarChartConfig {
 private String methodName = "writeBarChartFromSheets";
 private String tab;
 private String range;
 private boolean errorForDataAfterRange;
 private boolean swapColumns;
 private String outputFile;
 private String xLabel;
 private String yLabel;
 private int labelRotation;
 private boolean reverseOrder;
 private int xLabelMaxLineLength;
 private double xLimits;

 public String getMethodName() {
     return methodName;
 }

 public void setMethodName(String methodName) {
     this.methodName = methodName;
 }

 // ... (All existing getters and setters) ...

 public String getTab() {
     return tab;
 }

 public void setTab(String tab) {
     this.tab = tab;
 }

 public String getRange() {
     return range;
 }

 public void setRange(String range) {
     this.range = range;
 }

 public boolean isErrorForDataAfterRange() {
     return errorForDataAfterRange;
 }

 public void setErrorForDataAfterRange(boolean errorForDataAfterRange) {
     this.errorForDataAfterRange = errorForDataAfterRange;
 }

 public boolean isSwapColumns() {
     return swapColumns;
 }

 public void setSwapColumns(boolean swapColumns) {
     this.swapColumns = swapColumns;
 }

 public String getOutputFile() {
     return outputFile;
 }

 public void setOutputFile(String outputFile) {
     this.outputFile = outputFile;
 }

 public String getxLabel() {
     return xLabel;
 }

 public void setxLabel(String xLabel) {
     this.xLabel = xLabel;
 }

 public String getyLabel() {
     return yLabel;
 }

 public void setyLabel(String yLabel) {
     this.yLabel = yLabel;
 }

 public int getLabelRotation() {
     return labelRotation;
 }

 public void setLabelRotation(int labelRotation) {
     this.labelRotation = labelRotation;
 }

 public boolean isReverseOrder() {
     return reverseOrder;
 }

 public void setReverseOrder(boolean reverseOrder) {
     this.reverseOrder = reverseOrder;
 }

 public int getxLabelMaxLineLength() {
     return xLabelMaxLineLength;
 }

 public void setxLabelMaxLineLength(int xLabelMaxLineLength) {
     this.xLabelMaxLineLength = xLabelMaxLineLength;
 }

 public double getxLimits() {
     return xLimits;
 }

 public void setxLimits(double xLimits) {
     this.xLimits = xLimits;
 }

 @Override
 public String toString() {
     return "BarChartConfig{" +
            "methodName='" + methodName + '\'' +
            ", tab='" + tab + '\'' +
            ", range='" + range + '\'' +
            ", errorForDataAfterRange=" + errorForDataAfterRange +
            ", swapColumns=" + swapColumns +
            ", outputFile='" + outputFile + '\'' +
            ", xLabel='" + xLabel + '\'' +
            ", yLabel='" + yLabel + '\'' +
            ", labelRotation=" + labelRotation +
            ", reverseOrder=" + reverseOrder +
            ", xLabelMaxLineLength=" + xLabelMaxLineLength +
            ", xLimits=" + xLimits +
            '}';
 }
}
