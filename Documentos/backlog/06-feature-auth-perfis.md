---
title: "[FEATURE] Autenticação local com perfis Gerente e Colaborador"
labels: [feature, android, ciclo-1, "prioridade: alta", R2, R10]
---

## Requisito(s)
- [x] R2 — Cadastro/login com ≥ 2 perfis e permissões distintas
- [x] R10 — Erros e estados de UI no fluxo de auth

## História de usuário
**Como** usuário do app  
**quero** cadastrar-me e entrar como Gerente ou Colaborador  
**para** ver apenas as ações permitidas ao meu perfil.

## Escopo
### Inclui
- Telas de login e cadastro com validação (e-mail/senha)
- Persistência da sessão (DataStore/SharedPreferences ou Room)
- Modelo `UserRole = MANAGER | COLLABORATOR`
- Gate de navegação: rotas de criação de projeto só para Gerente
- Mensagens de erro legíveis (credenciais inválidas, campos vazios, loading)
### Não inclui
- OAuth / biometria (fora do Must)
- Sync remoto do usuário (pode mockar até o Ciclo 3)

## Critérios de aceite
- [ ] Cadastro + login funcionam offline nesta fase
- [ ] Colaborador **não** acessa criar/excluir projeto (UI e camada domain)
- [ ] Logout limpa sessão
- [ ] Estados: loading, erro, sucesso

## Notas técnicas
- Regras de permissão centralizadas no domain (não só esconder botão)
- Preparar interface `AuthRepository` para trocar implementação local → API depois

## Plano de teste
| Nº | Cenário | Esperado |
|----|---------|----------|
| 1 | Cadastro Gerente → login | Home de projetos |
| 2 | Login Colaborador tenta criar projeto | Bloqueado com feedback |
| 3 | Senha errada | Mensagem, sem crash |
