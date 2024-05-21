package br.gov.es.participe.controller.dto;

public class EvaluatorServerDto extends EvaluatorDataDto {

    private String agentePublicoNome;
    private String agentePublicoSub;

    public EvaluatorServerDto(String guid, String name, String agentePublicoNome, String agentePublicoSub) {
        super(guid, name);
        this.agentePublicoNome = agentePublicoNome;
        this.agentePublicoSub = agentePublicoSub;
    }
    public String getagentePublicoNome() {
        return agentePublicoNome;
    }
    public void setagentePublicoNome(String agentePublicoNome) {
        this.agentePublicoNome = agentePublicoNome;
    }
    public String getagentePublicoSub() {
        return agentePublicoSub;
    }
    public void setagentePublicoSub(String agentePublicoSub) {
        this.agentePublicoSub = agentePublicoSub;
    }

}
