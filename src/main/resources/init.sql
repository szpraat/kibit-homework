CREATE TABLE IF NOT EXISTS accounts (
    account_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_name VARCHAR(255),
    currency VARCHAR(3),
    balance DECIMAL
    );

INSERT INTO accounts (account_id, account_name, currency, balance) VALUES
                                                                       ('123e4567-e89b-12d3-a456-426614174000', 'Savings Account', 'EUR', 1000),
                                                                       ('123e4567-e89b-12d3-a456-426614174001', 'Checking Account', 'USD', 1500),
                                                                       ('123e4567-e89b-12d3-a456-426614174002', 'Business Account', 'EUR', 2000),
                                                                       ('123e4567-e89b-12d3-a456-426614174003', 'Investment Account', 'HUF', 500);
