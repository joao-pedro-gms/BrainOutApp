# Guia de contribuição

Regras de trabalho da equipe, conforme as seções 6.1, 6.2 e 6.3 do documento norteador do Projeto Integrador (ADS, PUC Goiás, 2026/2).

## Branches

```
master (protegida)
  ├── feature/<escopo>   funcionalidades novas
  ├── fix/<escopo>       correção de defeitos
  ├── docs/<escopo>      documentação
  ├── chore/<escopo>     configuração e infra
  └── test/<escopo>      testes
```

1. Nada de commit direto na `master`. Código entra por pull request, revisado por outro integrante.
2. Uma branch por issue, com nome tipo `feature/auth-login` ou `fix/validacao-tarefa`.
3. Commite cedo e com frequência. O documento norteador proíbe submissão concentrada de código perto das entregas, e o histórico distribuído entre os integrantes entra na nota (R13).
4. Depois do merge, apague a branch remota.

## Mensagens de commit

O documento proíbe mensagens genéricas ("ajustes", "update", "fix"). Usamos [Conventional Commits](https://www.conventionalcommits.org/pt-br/):

```
<tipo>(<escopo>): <descrição no imperativo>

feat(auth): adiciona login com e-mail e senha
fix(tarefas): corrige validação de data de entrega
docs(readme): atualiza instruções de instalação
test(projetos): cobre regra de negócio RN02
chore(ci): adiciona workflow de segurança
```

Tipos aceitos: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`, `ci`, `build`, `revert`.

O hook `commit-msg` bloqueia commit fora desse formato. Rode `scripts/setup.sh` uma vez após clonar para ativar os hooks.

## Pull requests

1. Abra o PR e preencha o template (ele carrega sozinho).
2. Vincule a issue com `Closes #N`.
3. Espere a revisão de pelo menos 1 integrante e o CI verde antes de mergar.
4. Se a branch tiver commits intermediários confusos, use squash merge. Caso contrário, merge normal.
5. Na revisão, confira: checklist do PR, requisitos R* cobertos, nenhum secret no diff.

## Segredos e credenciais

Senhas, chaves de API, keystores (`*.jks`, `*.keystore`) e `google-services.json` nunca entram no repositório. O `.gitignore` já cobre esses arquivos, e o hook `pre-commit` roda um scan de secrets antes de cada commit.

Configuração real vai em `local.properties` (Android) e `.env` (backend). Para cada um, mantenha um arquivo de exemplo versionado: `local.properties.example` e `.env.example`.

## Issues e quadro de tarefas

Toda tarefa vira issue antes de sair do papel, com labels de ciclo, requisito (R1 a R14), tipo e prioridade. O quadro fica no GitHub Projects e precisa estar sempre atualizado, porque o docente acompanha por ali o semestre inteiro.

No fim de cada ciclo, registramos o que foi concluído, o que foi replanejado e os impedimentos (modelo do Apêndice B do documento norteador).

## Antes de abrir PR

```bash
./scripts/quality-check.sh
```

O script roda o lint do Android (ktlint) e do backend (ruff). Só abra PR com ele passando, com tratamento de erros implementado (R10) e sem quebrar nenhum fluxo principal.
