CREATE TABLE regular_payment (
    id UUID PRIMARY KEY,
    pib VARCHAR(255) NOT NULL,
    ipn VARCHAR(10) NOT NULL,
    iban VARCHAR(29) NOT NULL,
    mfo VARCHAR(6) NOT NULL,
    edrpou VARCHAR(20) NOT NULL,
    beneficiary_name VARCHAR(255) NOT NULL,
    debit_period VARCHAR(50) NOT NULL,
    payment_amount NUMERIC(15,2) NOT NULL
);
CREATE TABLE entries_payment (
    id UUID PRIMARY KEY,
    regular_payment_id UUID NOT NULL,
    date_of_payment TIMESTAMP NOT NULL DEFAULT now(),
    amount NUMERIC(15,2),
    status CHAR(1) CHECK (status IN ('A','S'))
);

CREATE INDEX idx_entries_payment_regular_payment_id ON entries_payment (regular_payment_id);
CREATE INDEX idx_regular_payment_ipn ON regular_payment (ipn);
CREATE INDEX idx_regular_payment_iban ON regular_payment (iban);