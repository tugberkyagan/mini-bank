CREATE TABLE deposits (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    idempotency_key UUID NOT NULL UNIQUE,
    to_account_id BIGINT NOT NULL REFERENCES accounts(id),
    amount NUMERIC(19,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE ledger_entries
    ADD COLUMN deposit_id BIGINT REFERENCES deposits(id)
    CHECK ((transfer_id IS NULL) <> (deposit_id IS NULL));
