CREATE TABLE transfers (
   id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
   idempotency_key UUID NOT NULL UNIQUE,
   from_account_id BIGINT NOT NULL REFERENCES accounts(id),
   to_account_id BIGINT NOT NULL REFERENCES accounts(id),
   amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
   currency VARCHAR(3) NOT NULL,
   created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
   CHECK (from_account_id <> to_account_id)
);

CREATE TABLE ledger_entries (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    transfer_id BIGINT REFERENCES transfers(id),
    amount NUMERIC(19,2) NOT NULL CHECK (amount <> 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ledger_entries_account_id ON ledger_entries(account_id);