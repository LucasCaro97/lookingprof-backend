ALTER TABLE profession
    ADD UNIQUE KEY uk_profession_name (name_profession);

ALTER TABLE city
    MODIFY name_city VARCHAR(180) NOT NULL;

ALTER TABLE city
    ADD UNIQUE KEY uk_city_province_name (fk_province, name_city);
