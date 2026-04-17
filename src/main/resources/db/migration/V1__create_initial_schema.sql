-- Extensão para gerar UUIDs nativamente no PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Enums ─────────────────────────────────────────────────────
CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE transaction_type AS ENUM ('INCOME', 'EXPENSE');
CREATE TYPE debt_status AS ENUM ('OPEN', 'PARTIAL', 'PAID', 'OVERDUE');

-- ── Tabela: users ─────────────────────────────────────────────
CREATE TABLE users (
    id            UUID         NOT NULL DEFAULT uuid_generate_v4(),
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          user_role    NOT NULL DEFAULT 'USER',
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE INDEX idx_users_email ON users (email);

-- ── Tabela: refresh_tokens ────────────────────────────────────
CREATE TABLE refresh_tokens (
    id         UUID      NOT NULL DEFAULT uuid_generate_v4(),
    user_id    UUID      NOT NULL,
    token      TEXT      NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked    BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_token   ON refresh_tokens (token);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);

-- ── Tabela: categories ────────────────────────────────────────
CREATE TABLE categories (
    id         UUID        NOT NULL DEFAULT uuid_generate_v4(),
    user_id    UUID        NOT NULL,
    name       VARCHAR(80) NOT NULL,
    color      VARCHAR(7),
    icon       VARCHAR(50),
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT fk_categories_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_category_name_per_user UNIQUE (user_id, name)
);

-- ── Tabela: transactions ──────────────────────────────────────
CREATE TABLE transactions (
    id               UUID             NOT NULL DEFAULT uuid_generate_v4(),
    user_id          UUID             NOT NULL,
    category_id      UUID,
    type             transaction_type NOT NULL,
    amount           NUMERIC(15, 2)   NOT NULL,
    description      VARCHAR(255),
    transaction_date DATE             NOT NULL,
    notes            TEXT,
    created_at       TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_transactions PRIMARY KEY (id),
    CONSTRAINT chk_amount_positive CHECK (amount > 0),
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category
        FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_transactions_user_id   ON transactions (user_id);
CREATE INDEX idx_transactions_date      ON transactions (transaction_date DESC);
CREATE INDEX idx_transactions_user_date ON transactions (user_id, transaction_date DESC);
CREATE INDEX idx_transactions_category  ON transactions (category_id);

-- ── Tabela: debts ─────────────────────────────────────────────
CREATE TABLE debts (
    id                 UUID           NOT NULL DEFAULT uuid_generate_v4(),
    user_id            UUID           NOT NULL,
    creditor_name      VARCHAR(150)   NOT NULL,
    description        VARCHAR(255),
    principal_amount   NUMERIC(15, 2) NOT NULL,
    interest_rate      NUMERIC(6, 4)  NOT NULL DEFAULT 0,
    total_installments INT            NOT NULL DEFAULT 1,
    start_date         DATE           NOT NULL,
    status             debt_status    NOT NULL DEFAULT 'OPEN',
    created_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_debts PRIMARY KEY (id),
    CONSTRAINT chk_debt_principal_positive    CHECK (principal_amount > 0),
    CONSTRAINT chk_debt_interest_non_negative CHECK (interest_rate >= 0),
    CONSTRAINT chk_debt_installments_positive CHECK (total_installments > 0),
    CONSTRAINT fk_debts_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_debts_user_id ON debts (user_id);
CREATE INDEX idx_debts_status  ON debts (status);

-- ── Tabela: installments ──────────────────────────────────────
CREATE TABLE installments (
    id                  UUID           NOT NULL DEFAULT uuid_generate_v4(),
    debt_id             UUID           NOT NULL,
    installment_number  INT            NOT NULL,
    due_date            DATE           NOT NULL,
    amount              NUMERIC(15, 2) NOT NULL,
    paid                BOOLEAN        NOT NULL DEFAULT FALSE,
    paid_at             TIMESTAMP,
    paid_amount         NUMERIC(15, 2),
    created_at          TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_installments PRIMARY KEY (id),
    CONSTRAINT uq_installment_per_debt UNIQUE (debt_id, installment_number),
    CONSTRAINT chk_installment_amount_positive CHECK (amount > 0),
    CONSTRAINT fk_installments_debt
        FOREIGN KEY (debt_id) REFERENCES debts (id) ON DELETE CASCADE
);

CREATE INDEX idx_installments_debt_id  ON installments (debt_id);
CREATE INDEX idx_installments_due_date ON installments (due_date);
CREATE INDEX idx_installments_paid     ON installments (paid);

-- ── Trigger: atualiza updated_at automaticamente ──────────────
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_transactions_updated_at
    BEFORE UPDATE ON transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_debts_updated_at
    BEFORE UPDATE ON debts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();