CREATE TABLE IF NOT EXISTS person (
    idperson INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    lastname VARCHAR(45) NOT NULL,
    firstname VARCHAR(45) NOT NULL,
    nickname VARCHAR(45) NOT NULL,
    phone_number VARCHAR(15) NULL,
    address VARCHAR(200) NULL,
    email_address VARCHAR(150) NULL,
    birth_date DATE NULL
);


INSERT INTO person (lastname, firstname, nickname, phone_number, address, email_address, birth_date)
VALUES 
    ('Doe', 'John', 'JD', '555-0101', '123 Main St', 'john.doe@email.com', NULL),
    ('Smith', 'Jane', 'Janey', '555-0102', '456 Oak Ave', 'jane.smith@email.com', NULL);