## Decisões e premissas

- **`TIMESTAMP` sem fuso:** Segui o enunciado. Como o `application.yml` fixa `America/Sao_Paulo` no Hibernate e no Jackson, tudo é gravado e lido no horário de Brasília.
- **Unicidade do nome do clube:** Índice único em `lower(nome)`. Isso ignora diferenças entre letras maiúsculas e minúsculas e mantém os acentos, conforme a RN10: `"Águias FC"` e `"Aguias FC"` são considerados nomes diferentes.
- **Índice parcial de contrato ativo:** Garante no banco de dados que um jogador não tenha dois contratos com status `ATIVO`.
- **`ck_jogadores_valor`:** O piso de `1,00` para o valor base, definido pela RN06, fica protegido no banco de dados.