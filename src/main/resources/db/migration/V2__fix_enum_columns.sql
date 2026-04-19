-- Remove os defaults antes de alterar os tipos
ALTER TABLE users ALTER COLUMN role DROP DEFAULT;
ALTER TABLE debts ALTER COLUMN status DROP DEFAULT;

-- Converte as colunas de enum nativo para VARCHAR
ALTER TABLE users
    ALTER COLUMN role TYPE VARCHAR(10) USING role::VARCHAR;

ALTER TABLE transactions
    ALTER COLUMN type TYPE VARCHAR(10) USING type::VARCHAR;

ALTER TABLE debts
    ALTER COLUMN status TYPE VARCHAR(10) USING status::VARCHAR;

-- Remove os tipos customizados com CASCADE
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS transaction_type CASCADE;
DROP TYPE IF EXISTS debt_status CASCADE;

-- Reaplica os defaults como VARCHAR
ALTER TABLE users ALTER COLUMN role SET DEFAULT 'USER';
ALTER TABLE debts ALTER COLUMN status SET DEFAULT 'OPEN';