## Decisões e premissas

### Banco de dados

- **`TIMESTAMP` sem fuso:** Segui o enunciado. Como o `application.yml` fixa `America/Sao_Paulo` no Hibernate e no Jackson, tudo é gravado e lido no horário de Brasília.
- **Unicidade do nome do clube (RN10):** Checagem no service (`existsByNameIgnoreCase`) e índice único em `lower(nome)` no banco como garantia final. Isso ignora diferenças entre maiúsculas e minúsculas e mantém os acentos: `"Águias FC"` e `"Aguias FC"` são nomes diferentes.
- **Índice parcial de contrato ativo:** Garante no banco que um jogador não tenha dois contratos com status `ATIVO`.
- **`ck_jogadores_valor`:** O piso de `1,00` para o valor base, definido pela RN06, fica protegido no banco.

### Jogadores e clubes

- **Listagem de jogadores em memória:** O valor de mercado é calculado a cada consulta e não é gravado, então filtros por valor, ordenação por valor e paginação são aplicados em memória sobre os jogadores ativos. O volume do estudo de caso (máx. 20 clubes e 12 jogadores por clube) torna isso aceitável.
- **Validação estrita de query params:** Valores vazios, zeros à esquerda, maiúsculas trocadas e campos repetidos no `sort` são recusados com 400 (`PlayerListQuery` e `QueryParams`).
- **Valor do elenco e patrimônio:** Calculados a cada consulta, nunca gravados.
- **`GET /clubs`:** Uma consulta de jogadores por clube (N+1), aceitável com no máximo 20 clubes.
- **Detalhe do clube:** `@JsonUnwrapped` achata o jogador e o clube no JSON, para não duplicar campos nos DTOs.

### Datas

- **Datas nas respostas:** Serializadas sempre com segundos e `-03:00` (`BrasiliaDateTimeSerializer`).
- **Datas no corpo das requisições:** Recebidas como texto e validadas de forma estrita por `BrasiliaDateTimeParser` (formato `AAAA-MM-DDTHH:MM-03:00`, sem segundos). Datas inexistentes (`2027-02-30`, `24:00`) são recusadas com 400. O parser fica separado do serializer porque o formato de entrada e o de saída são diferentes, e a mensagem de erro indica o campo.

### Mercado (compra e venda)

- **Operações atômicas:** Compra e venda rodam em uma única transação (`@Transactional`): caixa, elenco, contrato e transação do histórico mudam juntos ou nada muda. Toda recusa deixa o banco intacto.
- **Preço sempre recalculado:** O valor pago na compra e o valor recebido na venda são o valor de mercado do momento, recalculado na hora, nunca o valor base.
- **Ordem das validações da compra:** Corpo e data inválidos (400), depois jogador e clube inexistentes (404) e por fim as regras de negócio (409). Assim nunca se combinam dois erros no mesmo pedido.
- **Limite de 50% do caixa:** Comparado como `custo × 2 ≤ caixa`, sem divisão nem arredondamento. Custo exatamente igual ao limite é aceito.
- **Fim da fidelidade:** Precisa ser posterior ao momento da compra e no máximo 1.095 dias depois dele.
- **Recompra:** Um clube não compra jogador que ele mesmo já vendeu, verificado pelas transações `VENDA` do histórico. Outros clubes podem comprá-lo.
- **Contrato ao vender:** Vira `RESCINDIDO` quando há multa (dias restantes maiores que zero) e `ENCERRADO` quando a fidelidade já foi cumprida.
- **Cálculos isolados:** Dias restantes e percentual de multa ficam em `LoyaltyRules`; multa e comissão ficam em `SaleCalculator`. Nenhum acessa o banco, o que facilita testá-los.
- **Jogador com clube sem contrato ativo:** Só ocorre se o banco for alterado manualmente. Nesse caso a venda falha com 500 e a transação é desfeita.

### Histórico de transações

- **Filtro e ordenação em memória:** O histórico filtra por tipo e jogador e ordena (mais recente primeiro, empate por id decrescente) em memória.
- **Validação antes da consulta:** O formato dos filtros (400) é validado antes de consultar clube e jogador (404).
- **Valores gravados:** Cada transação guarda os valores e o nome do jogador do momento e nunca é recalculada.
- **`summary`:** Calculado a cada consulta sobre as transações já filtradas, arredondado para 2 casas.
- **Jogador excluído:** Suas transações antigas continuam no histórico. Filtrar por ele devolve 404.

### Exportação CSV

- **Mesmo resultado do JSON:** A exportação reaproveita `TransactionQueryService.filtered`, então filtros, validações e ordem são idênticos aos do histórico.
- **Sem biblioteca externa:** O CSV é gerado com `StringBuilder`, escapando `;` e `"` nos nomes conforme a RN18.
- **Formato:** UTF-8 sem BOM, `\n` ao fim de todas as linhas (inclusive a última), vírgula decimal com 2 casas e `lucro` vazio quando nulo.
- **Sem `produces` no endpoint:** O `Content-Type` é definido na resposta de sucesso, para que erros (400 e 404) continuem saindo em JSON, mesmo que o cliente envie `Accept: application/json`.


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