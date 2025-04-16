package br.gov.es.participe.controller.dto;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class PersonMeetingFilteredDto extends PersonMeetingDto {
        
        private Boolean preRegistered;
        private Date preRegisteredDate;

        public Boolean getPreRegistered() {
            return preRegistered;
        }

        public void setPreRegistered(Boolean preRegistered) {
            this.preRegistered = preRegistered;
        }

        public Date getPreRegisteredDate() {
            return preRegisteredDate;
        }
        
        public void setPreRegisteredDate(Date preRegisteredDate) {
            this.preRegisteredDate = preRegisteredDate;
        }
}
