CREATE TABLE province (
    id_province INT NOT NULL AUTO_INCREMENT,
    name_province VARCHAR(120) NOT NULL,
    PRIMARY KEY (id_province),
    UNIQUE KEY uk_province_name (name_province)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE city (
    id_city INT NOT NULL AUTO_INCREMENT,
    name_city VARCHAR(120) NOT NULL,
    fk_province INT NOT NULL,
    PRIMARY KEY (id_city),
    CONSTRAINT fk_city_province FOREIGN KEY (fk_province) REFERENCES province (id_province)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE profession (
    id_profession INT NOT NULL AUTO_INCREMENT,
    name_profession VARCHAR(120) NOT NULL,
    PRIMARY KEY (id_profession)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user (
    id_user INT NOT NULL AUTO_INCREMENT,
    last_name VARCHAR(120) NOT NULL,
    first_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address VARCHAR(255),
    country VARCHAR(80),
    id_province INT,
    id_city INT,
    qualification INT,
    image_url VARCHAR(255),
    id_profession INT,
    role VARCHAR(40),
    description VARCHAR(250),
    create_at DATETIME NOT NULL,
    update_at DATETIME NOT NULL,
    is_active BOOLEAN,
    PRIMARY KEY (id_user),
    UNIQUE KEY uk_user_email (email),
    CONSTRAINT fk_user_province FOREIGN KEY (id_province) REFERENCES province (id_province),
    CONSTRAINT fk_user_city FOREIGN KEY (id_city) REFERENCES city (id_city),
    CONSTRAINT fk_user_profession FOREIGN KEY (id_profession) REFERENCES profession (id_profession)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE comment (
    id INT NOT NULL AUTO_INCREMENT,
    description VARCHAR(1000),
    qualification INT,
    fk_user_origin INT,
    fk_user_destination INT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comment_origin FOREIGN KEY (fk_user_origin) REFERENCES user (id_user),
    CONSTRAINT fk_comment_destination FOREIGN KEY (fk_user_destination) REFERENCES user (id_user)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE advertisement (
    id_advertisement INT NOT NULL AUTO_INCREMENT,
    description VARCHAR(1000),
    id_user INT,
    id_announcement INT,
    PRIMARY KEY (id_advertisement)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE publication (
    id INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255),
    profession VARCHAR(120),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE support_contact (
    id_suport_contact INT NOT NULL AUTO_INCREMENT,
    user_name VARCHAR(120),
    last_name VARCHAR(120),
    email VARCHAR(180),
    asunt VARCHAR(255),
    description VARCHAR(2000),
    PRIMARY KEY (id_suport_contact)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
