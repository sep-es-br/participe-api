package br.gov.es.participe.controller;

import br.gov.es.participe.controller.dto.LocalityTypeDto;
import br.gov.es.participe.controller.dto.controlPanel.ControlPanelDto;
import br.gov.es.participe.enumerator.ResultTypeControlPanelEnum;
import br.gov.es.participe.service.ControlPanelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/control-panel-dashboard")
public class ControlPanelController {

  @Autowired
  private ControlPanelService service;

  @GetMapping
  public ResponseEntity<ControlPanelDto> getDashboardInfo(
    @RequestParam Long idConference,
    @RequestParam(required = false) ResultTypeControlPanelEnum result,
    @RequestParam(required = false) String origin, @RequestParam(required = false) List<Long> meetings,
    @RequestParam(required = false) Long microregionChartAgroup,
    @RequestParam(required = false) Long microregionLocalitySelected,
    @RequestParam(required = false) Long structureItemSelected,
    @RequestParam(required = false) Long structureItemPlanSelected,
    @RequestParam(required = false, defaultValue = "false") Boolean stLastLevelLocality,
    @RequestParam(required = false, defaultValue = "false") Boolean stLastLevelPlanItem
  ) {
    ControlPanelDto response = service.getInformationsDashboard(idConference, result, origin, meetings,
                                                                microregionChartAgroup, microregionLocalitySelected,
                                                                structureItemSelected, structureItemPlanSelected, stLastLevelLocality, stLastLevelPlanItem
    );
    return ResponseEntity.status(200).body(response);
  }

  @GetMapping("/locality-parents")
  public ResponseEntity<List<LocalityTypeDto>> getAllTypeLocalityFromParents(@RequestParam Long idDomain,
                                                                             @RequestParam Long idTypeLocality) {
    List<LocalityTypeDto> response = service.getAllTypeLocality(idDomain, idTypeLocality);
    return ResponseEntity.status(200).body(response);
  }

}
