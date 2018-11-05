# ThunderPets™ -  Back-End
[![Waffle.io - Columns and their card count](https://badge.waffle.io/academiadev-jlle/wiki-thunderpets.svg?columns=all)](https://waffle.io/academiadev-jlle/wiki-thunderpets)

Projeto da AcademiaDev - Petcodes - Thunderpets

Este projeto consiste na construção de uma plataforma para divulgação de animais perdidos, encontrados ou para adoção.

## Introdução

Os tópicos a seguir irão ajudá-lo a fazer uma cópia do projeto em sua máquina local para o desenvolvimento e o teste do mesmo.

### Pré-Requisitos

* Java 8
* Gradle

### Instalação
```bash
gradle bootrun
```

## Execução
```bash
gradle bootrun
```

Após iniciado, a documentação do projeto pode ser encontrada em:
`http://localhost:8080/swagger-ui.html`

## Testes
```bash
gradle test
```

## Checkstyle
Para manter a padronização nos códigos do projeto, foi feita a definição de um checkstyle.
A instalação do plugin no IntelliJ pode ser feita com os seguintes passos:

1) File -> Settings -> Plugins
2) Digite `checkstyle`. Se informar que não foram encontrados plugins, clique em `Search in repositories`.
3) Seleciona o plugin `CheckStyle-IDEA` e clica em `Install`.
4) Reinicia o IntelliJ
5) File -> Settings -> Other settings -> Checkstyle
6) Clica no botão `+` e seleciona o arquivo `checkstyle.xml` que está na raíz do projeto. 
7) Após selecionar, clica em `Next`, seleciona o plugin na lista de checkstyles e clica em `Apply`.
8) Por fim, na mesma tela, trocar o valor de Scan Scope para `Only Java sources (including tests)` 

O comando do Gradle para verificar se os arquivos estão de acordo com o padrão é:
```bash
gradle checkstyleMain
```

## Deployment
```bash
gradle build
```

## Projetado com

* [Java]
* [Spring]
* [Gradle]

## Versionamento

## Autores

* [Adam Mews](https://github.com/liserline)
* [Diogo Antelo](https://github.com/DiogoAntelo)
* [Helena Dalmazo](https://github.com/nefasta)
* [Luiz Gustavo Eburneo](https://github.com/Botuca)
* [Ramon Artner](https://github.com/rartner)

## Licença

Este projeto está sob a a licença Apache License v2.0
