## Decisões e premissas

- **`TIMESTAMP` sem fuso:** Segui o enunciado. Como o `application.yml` fixa `America/Sao_Paulo` no Hibernate e no Jackson, tudo é gravado e lido no horário de Brasília.
- **Unicidade do nome do clube:** Índice único em `lower(nome)`. Isso ignora diferenças entre letras maiúsculas e minúsculas e mantém os acentos, conforme a RN10: `"Águias FC"` e `"Aguias FC"` são considerados nomes diferentes.
- **Índice parcial de contrato ativo:** Garante no banco de dados que um jogador não tenha dois contratos com status `ATIVO`.
- **`ck_jogadores_valor`:** O piso de `1,00` para o valor base, definido pela RN06, fica protegido no banco de dados.

## Tecnologias e dependências

### Backend

| Biblioteca | Motivo |
|---|---|
| Spring Boot 3.5.x (`spring-boot-starter-web`) | Framework da API REST, com controllers, processamento JSON e servidor Tomcat embutido. |
| Spring Data JPA + Hibernate | ORM que mapeia as tabelas do banco para entidades Java e facilita a execução de consultas. |
| Spring Boot Validation (`spring-boot-starter-validation`) | Bean Validation para validar os dados recebidos nas requisições. |
| PostgreSQL JDBC Driver (`postgresql`) | Driver de conexão com o PostgreSQL. |
| Flyway (`flyway-core` e `flyway-database-postgresql`) | Gerenciamento de migrations versionadas para criação e evolução do esquema do banco de dados. |
| springdoc-openapi (`springdoc-openapi-starter-webmvc-ui`) | Geração automática da documentação OpenAPI e disponibilização da interface Swagger UI. |
| `spring-boot-starter-test` (JUnit 5, Mockito e AssertJ) | Testes automatizados, incluindo testes do cálculo do valor de mercado. |
| Lombok | Redução de código repetitivo, como getters, setters e construtores. |

Dependências transitivas, como Jackson, HikariCP e Tomcat, são fornecidas pelo ecossistema Spring Boot e não precisam ser declaradas separadamente nesta lista.

### Frontend

| Biblioteca | Motivo |
|---|---|
| React 18+ e Vite (`react-ts`) | Base da interface e ferramenta para desenvolvimento e build da aplicação. |
| TypeScript | Tipagem estática para reduzir erros e melhorar a segurança nas chamadas à API. |
| Tailwind CSS (`tailwindcss` e `@tailwindcss/vite`) | Estilização por classes utilitárias, com suporte a layouts responsivos. |
| React Router (`react-router-dom`) | Navegação entre as telas da aplicação, utilizando o modo biblioteca. |
| ESLint | Análise estática do código para identificar possíveis problemas e inconsistências. |
| Prettier e `eslint-config-prettier` | Padronização da formatação do código e prevenção de conflitos entre Prettier e ESLint. |

### Infraestrutura e ferramentas

| Tecnologia | Motivo |
|---|---|
| Docker | Containerização dos serviços necessários ao ambiente de desenvolvimento. |
| Docker Compose | Inicialização e gerenciamento dos serviços, incluindo o PostgreSQL, com um único comando. |
| Maven Wrapper (`mvnw`) | Execução do build do backend sem exigir uma instalação global do Maven, utilizando a versão 3.9.9. |