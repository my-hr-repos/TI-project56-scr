CREATE TABLE IF NOT EXISTS sensors (
    id           MEDIUMINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(20)      NOT NULL,
    pin          TINYINT UNSIGNED NOT NULL,
    project_id   INT UNSIGNED     NOT NULL,
    calibrationA FLOAT DEFAULT NULL,
    calibrationB FLOAT DEFAULT NULL,
    calibrationC FLOAT DEFAULT NULL,

    CONSTRAINT foreign_key_sensors_project_id FOREIGN KEY (project_id) REFERENCES projects (id),

    CHECK (pin BETWEEN 0 AND 7), -- 8 pins on ADC
    UNIQUE (pin, project_id) -- every pin can be used only once in a project
);

CREATE INDEX IF NOT EXISTS index_sensors_project_id ON sensors (project_id);

-- A * pow(x, 2)  +  B * x  +  C
-- x = raw value from sensor
