# Política de Segredos

Nada de credencial no repo (R12, Seção 6.2 do norteador). Esta página é a fonte da verdade para quem estiver contribuindo.

## O que NUNCA vai pro repo

- Senhas, tokens, chaves de API
- `*.jks`, `*.keystore`, `google-services.json`
- `.env`, `.pem`, `.p12`, `id_rsa`
- Strings com `sk-`, `AKIA`, `AIza`, `ghp_`, `xox[abprs]-`

Tudo isso é coberto pelo `.gitignore` e pelo hook `pre-commit` (ver [`dev-guide.md`](../dev-guide.md)).

## Onde cada coisa vive

| Camada | Onde | Exemplo versionado | Onde o real vai |
|--------|------|---------------------|-----------------|
| Android — paths locais | `android/local.properties` | `android/local.properties.example` | Cada dev cria o seu |
| Android — keystore release | caminho fora do repo | — | GitHub Secrets em CI |
| Backend — variáveis | `backend/.env` | `backend/.env.example` | Cada dev cria o seu |
| CI — secrets | GitHub Actions Secrets | (na interface web) | Encrypted, scope por env |

## Procedimento ao commitar

1. `./scripts/setup.sh` instala `pre-commit` que bloqueia secrets automaticamente.
2. Antes do PR, `./scripts/quality-check.sh` roda gitleaks novamente.
3. CI (`Security` workflow) roda gitleaks + pip-audit toda semana.

## Se um secret vazar

1. **Revogar imediatamente** o token/keystore na origem (GitHub, AWS, GCP, ...)
2. **Remover do histórico**: `git filter-repo --path <arquivo> --invert-paths` (ou BFG)
3. **Avisar a equipe** — incidente de segurança
4. **Gerar nova credencial** e atualizar CI/devs
5. **Pós-mortem** registrado em `docs/auditoria/` (criar quando necessário)
