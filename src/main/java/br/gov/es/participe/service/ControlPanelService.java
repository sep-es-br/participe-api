package br.gov.es.participe.service;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import br.gov.es.participe.controller.dto.controlPanel.ControlPanelChartDto;
import br.gov.es.participe.controller.dto.controlPanel.ControlPanelDto;
import br.gov.es.participe.controller.dto.controlPanel.HeatMapChartDto;
import br.gov.es.participe.controller.dto.controlPanel.MicroregionChartQueryDto;
import br.gov.es.participe.enumerator.ResultTypeControlPanelEnum;
import br.gov.es.participe.repository.AttendRepository;
import br.gov.es.participe.repository.ControlPanelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
public class ControlPanelService {
	
  private final ControlPanelRepository repository;
  private final HighlightService highlightService;
  private final AttendRepository attendRepository;
  private final LocalityTypeService localityTypeService;

  @Autowired
  public ControlPanelService(
      ControlPanelRepository repository,
      HighlightService highlightService,
      AttendRepository attendRepository,
      LocalityTypeService localityTypeService
  ) {
    this.repository = repository;
    this.highlightService = highlightService;
    this.attendRepository = attendRepository;
    this.localityTypeService = localityTypeService;
  }

  private HeatMapChartDto heatChartDto (String latitudeLongitude1, Long count) {
	  HeatMapChartDto heatDto = new HeatMapChartDto();    	  
	  if (count == null) {
	   		heatDto.setCount(0L);
 	  }else{		   	    	   
 	    	heatDto.setCount(count);		
 	  }		   
	  String[] latitudeLongitude = (latitudeLongitude1 == null || latitudeLongitude1.isEmpty())
	   	        ? new String[0]
	   	        : latitudeLongitude1.split(",");
 	     if (latitudeLongitude.length <= 0){
 	    	 heatDto.setLat(null);
 	    	 heatDto.setLng(null);		   	      
 	     }else{		   	    	
	   	     String latitude = (latitudeLongitude.length == 2) ? latitudeLongitude[0] : "0";
	   	     String longitude = (latitudeLongitude.length == 2) ? latitudeLongitude[1] : "0";
	   	     BigDecimal latitudeConvertido = new BigDecimal(latitude.trim());
	   	     BigDecimal longitudeConvertido = new BigDecimal(longitude.trim());	
	   	     heatDto.setLat(latitudeConvertido);
	   	     heatDto.setLng(longitudeConvertido);
 	     }	  	   
   return heatDto;
  }

  private String returnOriginConverted(String origin) {
    if (origin == null || origin.isEmpty()) {
      return origin;
    }

    if (origin.equals("PRESENTIAL")) {
      return "pres";
    }

    if (origin.equals("REMOTE")) {
      return "rem";
    }

    throw new IllegalArgumentException("Invalid origin.");
  }

  public List<LocalityTypeDto> getAllTypeLocality(Long idDomain, Long idTypeLocality) {
    List<LocalityTypeDto> dto = repository.findDataTypeLocality(idDomain, idTypeLocality);
    Collections.reverse(dto);
    dto.add(new LocalityTypeDto(localityTypeService.find(idTypeLocality)));
    return dto;
  }
  
    
  public ControlPanelDto getInformationsDashboard(
      Long idConference,
      ResultTypeControlPanelEnum result,
      String origin,
      List<Long> meetings,
      Long microregionChartAgroup,
      Long microregionLocalitySelected,
      Long structureItemSelected,
      Long structureItemPlanSelected,
      Boolean stLastLevelLocality,
      Boolean stLastLevelPlanItem
  ) {
    ControlPanelDto dto = new ControlPanelDto();
    List<ControlPanelChartDto> microregionChartDto = new ArrayList<>();   
    List<HeatMapChartDto> heatMapChartDto = new ArrayList<>();    
    List<ControlPanelChartDto> strategicAreaChart= new ArrayList<>();   
    List<MicroregionChartQueryDto> microregionChartQueryResult;
    List<MicroregionChartQueryDto> planItemChartQueryResult;

    origin = returnOriginConverted(origin);

    //Totalizers dashboard
 	  
	   if (origin == null || origin.isEmpty()) {
	    	 dto.setParticipants(attendRepository.countParticipationAllOriginsByConference(idConference));
	    	 dto.setHighlights(highlightService.countHighlightAllOriginsByConference(idConference));
	    	 dto.setProposals(attendRepository.countCommentAllOriginsByConference(idConference));
	    	 dto.setCounties(attendRepository.countLocalityAllOriginsByConference(idConference));
	   }
	   else if (origin.equals("rem")) {
		   dto.setParticipants(attendRepository.countParticipationRemoteOriginByConference(idConference));    
		   dto.setHighlights(highlightService.countHighlightRemoteOriginByConference(idConference));   
		   dto.setProposals(attendRepository.countCommentRemoteOriginByConference(idConference));  
		   dto.setCounties(attendRepository.countLocalityRemoteOriginByConference(idConference));
	   }
	   else if (origin.equals("pres")) {
		   dto.setParticipants(attendRepository.countParticipationPresentialOriginByConference(idConference, meetings));  
		   dto.setHighlights(highlightService.countHighlightPresentialOriginByConference(idConference, meetings));	
		   dto.setProposals(attendRepository.countCommentPresentialOriginByConference(idConference, meetings)); 
		   dto.setCounties(attendRepository.countLocalityPresentialOriginByConference(idConference, meetings));
	   }    	  

	      	   
	   if (ResultTypeControlPanelEnum.PARTICIPANTS.equals(result)) {	           	  
		     if(origin == null || origin.isEmpty()){   			    	 
		    	  microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceParticipationAllAgroup(idConference,microregionChartAgroup
		    			                              ,microregionLocalitySelected,structureItemPlanSelected);   		    	 		    	 	    	  
		    	  for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 		    		   		  
		    		  ControlPanelChartDto microDto = new ControlPanelChartDto();		    		  
		    		  microDto.setId(microregionChartQueryResult.get(i).getId());
			   		   microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
			   		   if (microregionChartQueryResult.get(i).getQuantityParticipation() == null){
			   			   microDto.setQuantity(0L);
			   	       }else{		   	    	   
			   	    	   microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityParticipation());		
			   	       }		   		     		   
			   		   microregionChartDto.add(microDto);	 	 			   		  			   		   	 
			   	      heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityParticipation()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
		   		 }				    	  		    
		    	 planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceParticipationAllPlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected);		    	    	  
		    	 for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 		    			    		 		    		  
		    		   ControlPanelChartDto strategicDto = new ControlPanelChartDto(); 
		    		   strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());		    		  	    		   
		    		   if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
		    			   strategicDto.setDescription("Without Name");	    			   		    			   
		    		   }else {
		    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
		    		   }		    		   
		    		   if (planItemChartQueryResult.get(i).getQuantityParticipation() == null){
			   			strategicDto.setQuantity(0L);
			   	       }else{		   	    	   
			   	    	    strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityParticipation());		
			   	       }	
			   		   strategicAreaChart.add(strategicDto);		    		 
		    	 }			    	 
		     }else if (origin.equals("rem")){
		    	 
		    	microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceParticipationRemotoAgroup(idConference,microregionChartAgroup
		    								,microregionLocalitySelected,structureItemPlanSelected);   		    	 					    		    	    	  			
				for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 					
					ControlPanelChartDto microDto = new ControlPanelChartDto();
					microDto.setId(microregionChartQueryResult.get(i).getId());
					microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
					if (microregionChartQueryResult.get(i).getQuantityParticipation() == null){
					 microDto.setQuantity(0L);
					}else{		   	    	   
					     microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityParticipation());		
					}		   		     		   
					microregionChartDto.add(microDto);	 	 			   		  									   	 
					heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityParticipation()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
				}									
				planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceParticipationRemotoPlanItemAgroup(idConference,
											microregionLocalitySelected,structureItemPlanSelected);									
				for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 						    		 
					 ControlPanelChartDto strategicDto = new ControlPanelChartDto();
					strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
					if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
		    			   strategicDto.setDescription("Without Name");	    			   		    			   
		    		}else{
		    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
		    		}   		   
					if (planItemChartQueryResult.get(i).getQuantityParticipation() == null){
					strategicDto.setQuantity(0L);
					}else{		   	    	   
					      strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityParticipation());		
					}	
					strategicAreaChart.add(strategicDto);		    		 
				}		    	  		    	 		    	 		    	 		    	 		    	 		 
		 	 }else if (origin.equals("pres")){		 			  		 	 		 		
			 		microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceParticipationPresenteAgroup(idConference,microregionChartAgroup
							,microregionLocalitySelected,structureItemPlanSelected, meetings);   		    	 	    		    	    	  			
					for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 						
						ControlPanelChartDto microDto = new ControlPanelChartDto();
						microDto.setId(microregionChartQueryResult.get(i).getId());
						microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
						if (microregionChartQueryResult.get(i).getQuantityParticipation() == null){
						microDto.setQuantity(0L);
						}else{		   	    	   
						 microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityParticipation());		
						}		   		     		   
						microregionChartDto.add(microDto);	 	 			   		  												   	 
						heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityParticipation()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
					}									
					planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceParticipationPresentePlanItemAgroup(idConference
											,microregionLocalitySelected,structureItemPlanSelected,meetings);									
					for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 						    		 
						 ControlPanelChartDto strategicDto = new ControlPanelChartDto();
						strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
						if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
			    			   strategicDto.setDescription("Without Name");	    			   		    			   
			    		}else{
			    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
			    		}	   		   
						if (planItemChartQueryResult.get(i).getQuantityParticipation() == null){
						strategicDto.setQuantity(0L);
						}else{		   	    	   
						      strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityParticipation());		
						}	
						strategicAreaChart.add(strategicDto);		    		 
					}		    	  	 	 
		 	 }  		    	     	  		    			  	   
	   }else if(ResultTypeControlPanelEnum.HIGHLIGHTS.equals(result)){         
		   if(origin == null || origin.isEmpty()){   			    	 
		      microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceHighlightAllAgroup(idConference,microregionChartAgroup
		    			                              ,microregionLocalitySelected,structureItemPlanSelected);   		    	 		 		    	  
		    	  for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 		    		 
		    		  ControlPanelChartDto microDto = new ControlPanelChartDto();
		    		   microDto.setId(microregionChartQueryResult.get(i).getId());
			   		   microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
			   		   if (microregionChartQueryResult.get(i).getQuantityHighlight() == null){
			   			   microDto.setQuantity(0L);
			   	       }else{		   	    	   
			   	    	   microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityHighlight());		
			   	       }		   		     		   
			   		   microregionChartDto.add(microDto);	 	 			   		  						   	 
			   	      heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityHighlight()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
		   		 }				    	  		    	
		    	 planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceHighlightAllPlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected);		    	 
		    	 for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 		    			    		 
		    		   ControlPanelChartDto strategicDto = new ControlPanelChartDto();
		    		   strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
		    		   if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
		    			   strategicDto.setDescription("Without Name");	    			   		    			   
		    		   }else{
		    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
		    		   }	 	   		   
			   		   if (planItemChartQueryResult.get(i).getQuantityHighlight() == null){
			   			strategicDto.setQuantity(0L);
			   	       }else{		   	    	   
			   	    	    strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityHighlight());		
			   	       }	
			   		   strategicAreaChart.add(strategicDto);		    		 
		    	 }			    	 
		     }else if (origin.equals("rem")){
		    	 
		    	microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceHighlightRemotoAgroup(idConference,microregionChartAgroup
		    								,microregionLocalitySelected,structureItemPlanSelected);   		    	 					    		    	    	  			
				for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 				
					ControlPanelChartDto microDto = new ControlPanelChartDto();
					microDto.setId(microregionChartQueryResult.get(i).getId());
					microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
					if (microregionChartQueryResult.get(i).getQuantityHighlight() == null){
					 microDto.setQuantity(0L);
					}else{		   	    	   
					     microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityHighlight());		
					}		   		     		   
					microregionChartDto.add(microDto);	 	 			   		  										   	 
					heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityHighlight()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
				}									
				planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceHighlightRemotoPlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected);									
				for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 						    		 
					ControlPanelChartDto strategicDto = new ControlPanelChartDto();
					strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
					if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
		    			   strategicDto.setDescription("Without Name");	    			   		    			   
		    		}else{
		    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
		    		}	 		   		   
					if (planItemChartQueryResult.get(i).getQuantityHighlight() == null){
					strategicDto.setQuantity(0L);
					}else{		   	    	   
					      strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityHighlight());		
					}	
					strategicAreaChart.add(strategicDto);		    		 
				}		    	  		    	 		    	 		    	 		    	 		    	 		 
		 	 }else if (origin.equals("pres")){		 			  		 	 		 		
			 		microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceHighlightPresenteAgroup(idConference,microregionChartAgroup
							,microregionLocalitySelected,structureItemPlanSelected, meetings);   		    	 	    		    	    	  			
					for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 					
						ControlPanelChartDto microDto = new ControlPanelChartDto();
						microDto.setId(microregionChartQueryResult.get(i).getId());
						microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
						if (microregionChartQueryResult.get(i).getQuantityHighlight() == null){
						microDto.setQuantity(0L);
						}else{		   	    	   
						 microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityHighlight());		
						}		   		     		   
						microregionChartDto.add(microDto);	 	 			   		  												   	 
						heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityHighlight()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
					}											
					planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceHighlightPresentePlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected,meetings);										
					for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 						    		 
						ControlPanelChartDto strategicDto = new ControlPanelChartDto();
						strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
						if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
			    			   strategicDto.setDescription("Without Name");	    			   		    			   
			    		}else{
			    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
			    		}	 	   		   
						if (planItemChartQueryResult.get(i).getQuantityHighlight() == null){
						strategicDto.setQuantity(0L);
						}else{		   	    	   
						      strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityHighlight());		
						}	
						strategicAreaChart.add(strategicDto);		    		 
					}		    	  	 	 
		 	 }  		    			  	   
	   }else if(ResultTypeControlPanelEnum.PROPOSALS.equals(result)){	      	     
		   if(origin == null || origin.isEmpty()){   			    	 
		    	  microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceProposalsAllAgroup(idConference,microregionChartAgroup
		    			                              ,microregionLocalitySelected,structureItemPlanSelected);   		    	 		 		    	  
		    	  for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 		    		 
		    		  ControlPanelChartDto microDto = new ControlPanelChartDto();
		    		   microDto.setId(microregionChartQueryResult.get(i).getId());
			   		   microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
			   		   if (microregionChartQueryResult.get(i).getQuantityComment() == null){
			   			   microDto.setQuantity(0L);
			   	       }else{		   	    	   
			   	    	   microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityComment());		
			   	       }		   		     		   
			   		   microregionChartDto.add(microDto);	 	 			   		  			   		   	 
			   	       heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityComment()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
		   		 }			    	  		    	
		    	 planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceProposalsAllPlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected);		    	 
		    	 for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 		    			    		 
		    		   ControlPanelChartDto strategicDto = new ControlPanelChartDto();
		    		   strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
		    		   if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
		    			   strategicDto.setDescription("Without Name");	    			   		    			   
		    		   }else{
		    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
		    		   }	    		   
			   		   if (planItemChartQueryResult.get(i).getQuantityComment() == null){
			   			strategicDto.setQuantity(0L);
			   	       }else{		   	    	   
			   	    	    strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityComment());		
			   	       }	
			   		   strategicAreaChart.add(strategicDto);		    		 
		    	 }		    	 
		     }else if (origin.equals("rem")){	    	 
		    	microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceProposalsRemotoAgroup(idConference,microregionChartAgroup
		    								,microregionLocalitySelected,structureItemPlanSelected);   		    	 				    		    	    	  			
				for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 				
					ControlPanelChartDto microDto = new ControlPanelChartDto();
					microDto.setId(microregionChartQueryResult.get(i).getId());
					microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
					if (microregionChartQueryResult.get(i).getQuantityComment() == null){
					 microDto.setQuantity(0L);
					}else{		   	    	   
					     microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityComment());		
					}		   		     		   
					microregionChartDto.add(microDto);	 	 			   		  											   	 
					heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityComment()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
				}										
				planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceProposalsRemotoPlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected);									
				for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 						    		 
					ControlPanelChartDto strategicDto = new ControlPanelChartDto();
					strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
					if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
		    			   strategicDto.setDescription("Without Name");	    			   		    			   
		    		}else{
		    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
		    		}	 		   		   
					if (planItemChartQueryResult.get(i).getQuantityComment() == null){
					strategicDto.setQuantity(0L);
					}else{		   	    	   
					      strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityComment());		
					}	
					strategicAreaChart.add(strategicDto);		    		 
				}		    	  
		    	 		    	 		    	 		    	 		    	 		 
		 	 }else if (origin.equals("pres")){		 			  		 	 		 		
			 		microregionChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceProposalsPresenteAgroup(idConference,microregionChartAgroup
							,microregionLocalitySelected,structureItemPlanSelected, meetings);   		    	 	    		    	    	  			
					for (int i = 0; i < microregionChartQueryResult.size(); i++) {		    			 		    		 						
						ControlPanelChartDto microDto = new ControlPanelChartDto();
						microDto.setId(microregionChartQueryResult.get(i).getId());
						microDto.setDescription(microregionChartQueryResult.get(i).getName());		   		   
						if (microregionChartQueryResult.get(i).getQuantityComment()== null){
						microDto.setQuantity(0L);
						}else{		   	    	   
						 microDto.setQuantity(microregionChartQueryResult.get(i).getQuantityComment());		
						}		   		     		   
						microregionChartDto.add(microDto);	 	 			   		  														   	 
						heatMapChartDto.add(heatChartDto(microregionChartQueryResult.get(i).getLatitudeLongitude(),microregionChartQueryResult.get(i).getQuantityComment()));			   	     			   	     			   	      			   	    			   	      			   	      			   	      
					}	
					planItemChartQueryResult= repository.findDataMicroregionMapDashboardFromIdConferenceProposalsPresentePlanItemAgroup(idConference,microregionLocalitySelected,structureItemPlanSelected,meetings);										
					for(int i = 0; i < planItemChartQueryResult.size(); i++) {		    		 						    		 
						ControlPanelChartDto strategicDto = new ControlPanelChartDto();
						strategicDto.setId(planItemChartQueryResult.get(i).getIdPlanItem());
						if(planItemChartQueryResult.get(i).getPlanItemName() == null) {
			    			   strategicDto.setDescription("Without Name");	    			   		    			   
			    		}else{
			    			   strategicDto.setDescription(planItemChartQueryResult.get(i).getPlanItemName());		     			   
			    		}	 		   		   
						if (planItemChartQueryResult.get(i).getQuantityComment() == null){
						strategicDto.setQuantity(0L);
						}else{		   	    	   
						      strategicDto.setQuantity(planItemChartQueryResult.get(i).getQuantityComment());		
						}	
						strategicAreaChart.add(strategicDto);		    		 
					}		    	  	 	 
		 	 }  		    	
		   
	   }
	   dto.setMicroregionChart(microregionChartDto); 		
	   dto.setHeatMapChart(heatMapChartDto);
	   dto.setStrategicAreaChart(strategicAreaChart);	   	   
  return dto;

  }
}
