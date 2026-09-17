# Modelo de Dados (resumo)

Diagrama ER — versão inicial. Detalhamento em [`arquitetura.md`](../arquitetura.md) e modelagem completa no documento de projeto (#1, Apêndice A.1).

```mermaid
erDiagram
    USUARIO ||--o{ PROJETO : "cria (gerente)"
    USUARIO ||--o{ TAREFA : "responsavel"
    PROJETO ||--o{ TAREFA : "contem"

    USUARIO {
        uuid id PK
        string email UK
        string senha_hash
        enum perfil "gerente|colaborador"
        timestamp created_at
    }

    PROJETO {
        uuid id PK
        string nome
        text descricao
        date data_inicio
        date prazo
        enum status "planejado|em_andamento|concluido|cancelado"
        uuid criado_por FK
        timestamp created_at
        timestamp updated_at
    }

    TAREFA {
        uuid id PK
        uuid projeto_id FK
        string titulo
        text descricao
        uuid responsavel_id FK
        enum prioridade "baixa|media|alta"
        enum status "aberta|em_andamento|concluida|cancelada"
        date prazo
        timestamp created_at
        timestamp updated_at
    }
```

## Decisões

- **UUID v7** como PK (ordenável por tempo, melhor para sync) — registrar em ADR quando #14 for implementada
- **Soft delete** em Projeto e Tarefa (campo `deleted_at`) — manter histórico para sync e relatórios
- **Timestamps** em UTC; conversão no cliente
- **Enums** como string no banco (legibilidade, migrations simples)
- **FK com `ON DELETE RESTRICT`** em Tarefa→Projeto (regra RN02: não excluir projeto com tarefas abertas)

## Migrations

Versionadas com **Alembic** (backend) e **Room schema export** (Android, R5). Schema versionado no repo em `backend/alembic/versions/` e `android/app/schemas/`.

## Sincronização (R6)

Cada entidade carrega `updated_at` e `server_version`. Cliente usa `updated_at` para resolver conflito (server-wins, ver [`arquitetura.md`](../arquitetura.md#sincronização-offline--online-r6)).
