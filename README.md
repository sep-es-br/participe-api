# Participe | Back-end

## 1. Build

Execute o comando abaixo, substituindo o valor de `profile` pelo correspondente ao ambiente.

```sh
java -Dspring.profiles.active={profile} -jar participe-api.jar
```

Os valores aceitos nesta propriedade são:

- `dev` - para o ambiente de desenvolvimento.
- `prod` - para o ambiente de produção.

Caso nenhum valor seja passado na propriedade `spring.profiles.active`, os valores padrões - geralmente referentes ao ambiente local, de desenvolvimento - serão carregados.


## 1. Registros iniciais

```
CREATE (:LocalityType{name:'País'}),
	(:LocalityType{name:'Estado'}),
	(:LocalityType{name:'Região'}),
  	(:LocalityType{name:'Microrregião'}),
  	(:LocalityType{name:'Município'}),
    (:LocalityType{name:'Bairro'});

````


