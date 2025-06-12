CREATE TABLE person (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    age VARCHAR(10),
    password VARCHAR(255) NOT NULL,
    role_user_enum VARCHAR(20) NOT NULL CHECK (role_user_enum IN ('ADMIN', 'USER'))
);

CREATE TABLE bootcamp_person (
    id SERIAL PRIMARY KEY,
    id_bootcamp BIGINT NOT NULL,
    id_person BIGINT NOT NULL,
    CONSTRAINT fk_person
      FOREIGN KEY (id_person) REFERENCES person(id)
);