/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.gov.es.participe.util.domain.report;

import java.util.List;

/**
 *
 * @author gean.carneiro
 */
public class ReportConfig {
    
    private ReportName reportName;
    private String mainReport;
    private List<Param> params;

    public ReportName getReportName() {
        return reportName;
    }

    public void setReportName(ReportName reportName) {
        this.reportName = reportName;
    }

    public String getMainReport() {
        return mainReport;
    }

    public void setMainReport(String mainReport) {
        this.mainReport = mainReport;
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }
    
    
    
    public static class ReportName {
        private String pt;
        private String en;

        public String getPt() {
            return pt;
        }

        public void setPt(String pt) {
            this.pt = pt;
        }

        public String getEn() {
            return en;
        }

        public void setEn(String en) {
            this.en = en;
        }
        
    }
    
    public static class Param {
        private String paramLabel;
        private String paramName;

        public String getParamLabel() {
            return paramLabel;
        }

        public void setParamLabel(String paramLabel) {
            this.paramLabel = paramLabel;
        }

        public String getParamName() {
            return paramName;
        }

        public void setParamName(String paramName) {
            this.paramName = paramName;
        }
        
        
    }
}
