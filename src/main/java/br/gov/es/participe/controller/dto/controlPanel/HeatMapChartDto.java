package br.gov.es.participe.controller.dto.controlPanel;

import java.math.BigDecimal;

public class HeatMapChartDto {
    private BigDecimal lat;
    private BigDecimal lng;
    private Long count;

    public HeatMapChartDto(BigDecimal lat, BigDecimal lng, Long count) {
        this.lat = lat;
        this.lng = lng;
        this.count = count;
    }

    public HeatMapChartDto() {
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLng() {
        return lng;
    }

    public void setLng(BigDecimal lng) {
        this.lng = lng;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
