package fi.panukorpela.sheetslatex.service;

import java.util.List;

//This class will hold all your chart configurations
public class ChartConfiguration {
 private List<BarChartConfig> charts;

 public List<BarChartConfig> getCharts() {
     return charts;
 }

 public void setCharts(List<BarChartConfig> charts) {
     this.charts = charts;
 }

 @Override
 public String toString() {
     return "ChartConfiguration{" +
            "charts=" + charts +
            '}';
 }
}
