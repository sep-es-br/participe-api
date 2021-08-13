package br.gov.es.participe.controller.dto;

public class PlanParamDto {

    private Long id;
    private String name;
    private StructureParamDto structure;
    private DomainParamDto domain;
    private LocalityTypeDto localitytype;

    public PlanParamDto() {
    }

    public PlanParamDto(PlanDto plan) {
        id = plan.getId();
        name = plan.getName();
        structure = new StructureParamDto(plan.getStructure());
        domain = new DomainParamDto(plan.getDomain());
        localitytype = plan.getlocalitytype();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StructureParamDto getStructure() {
        return structure;
    }

    public void setStructure(StructureParamDto structure) {
        this.structure = structure;
    }

    public DomainParamDto getDomain() {
        return domain;
    }

    public void setDomain(DomainParamDto domain) {
        this.domain = domain;
    }

	public LocalityTypeDto getlocalitytype() {
		return localitytype;
	}

	public void setlocalitytype(LocalityTypeDto type) {
		this.localitytype = type;
	}
}
