package br.gov.es.participe.controller.dto.controlPanel;

import java.util.List;

public class ControlPanelDto {
    private int participants;
    private int proposals;
    private int highlights;
    private int counties;
    private List<ControlPanelChartDto> microregionChart;
    private List<HeatMapChartDto> heatMapChart;
    private List<ControlPanelChartDto> strategicAreaChart;

    public int getParticipants() {
        return participants;
    }

    public List<ControlPanelChartDto> getStrategicAreaChart() {
        return strategicAreaChart;
    }

    public void setStrategicAreaChart(List<ControlPanelChartDto> strategicAreaChart) {
        this.strategicAreaChart = strategicAreaChart;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public int getProposals() {
        return proposals;
    }

    public void setProposals(int proposals) {
        this.proposals = proposals;
    }

    public int getHighlights() {
        return highlights;
    }

    public void setHighlights(int highlights) {
        this.highlights = highlights;
    }

    public int getCounties() {
        return counties;
    }

    public void setCounties(int counties) {
        this.counties = counties;
    }

    public List<ControlPanelChartDto> getMicroregionChart() {
        return microregionChart;
    }

    public void setMicroregionChart(List<ControlPanelChartDto> microregionChart) {
        this.microregionChart = microregionChart;
    }

    public List<HeatMapChartDto> getHeatMapChart() {
        return heatMapChart;
    }

    public void setHeatMapChart(List<HeatMapChartDto> heatMapChart) {
        this.heatMapChart = heatMapChart;
    }

}
