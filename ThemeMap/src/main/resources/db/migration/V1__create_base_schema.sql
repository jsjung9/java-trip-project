CREATE TABLE editor (
    editor_id BIGINT NOT NULL AUTO_INCREMENT,
    id VARCHAR(30) NOT NULL,
    pw VARCHAR(72) NOT NULL,
    salt VARCHAR(64) NULL,
    email_id VARCHAR(64) NOT NULL,
    email_domain VARCHAR(128) NOT NULL,
    editor_name VARCHAR(30) NOT NULL,
    like_sum INT NOT NULL DEFAULT 0,
    join_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    token TEXT NULL,
    PRIMARY KEY (editor_id),
    UNIQUE KEY uk_editor_login_id (id)
);

CREATE TABLE theme (
    theme_id BIGINT NOT NULL AUTO_INCREMENT,
    theme_name VARCHAR(100) NOT NULL,
    description VARCHAR(1000) NOT NULL DEFAULT '',
    editor_id BIGINT NOT NULL,
    type TINYINT(1) NOT NULL DEFAULT 1,
    visible TINYINT(1) NOT NULL DEFAULT 1,
    like_sum INT NOT NULL DEFAULT 0,
    PRIMARY KEY (theme_id),
    KEY idx_theme_editor (editor_id),
    KEY idx_theme_discovery (visible, like_sum),
    CONSTRAINT fk_theme_editor FOREIGN KEY (editor_id) REFERENCES editor(editor_id) ON DELETE CASCADE
);

CREATE TABLE place (
    place_id VARCHAR(64) NOT NULL,
    place_name VARCHAR(150) NOT NULL,
    latitude DECIMAL(10, 7) NOT NULL,
    longitude DECIMAL(10, 7) NOT NULL,
    score_sum INT NOT NULL DEFAULT 0,
    score_count INT NOT NULL DEFAULT 0,
    address VARCHAR(255) NOT NULL DEFAULT '',
    phone VARCHAR(30) NOT NULL DEFAULT '',
    PRIMARY KEY (place_id)
);

CREATE TABLE place_in_theme (
    theme_id BIGINT NOT NULL,
    place_id VARCHAR(64) NOT NULL,
    editor_id BIGINT NOT NULL,
    PRIMARY KEY (theme_id, place_id),
    KEY idx_place_in_theme_editor (theme_id, editor_id),
    CONSTRAINT fk_place_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id) ON DELETE CASCADE,
    CONSTRAINT fk_place_place FOREIGN KEY (place_id) REFERENCES place(place_id) ON DELETE CASCADE,
    CONSTRAINT fk_place_editor FOREIGN KEY (editor_id) REFERENCES editor(editor_id) ON DELETE CASCADE
);

CREATE TABLE like_theme (
    editor_id BIGINT NOT NULL,
    theme_id BIGINT NOT NULL,
    PRIMARY KEY (editor_id, theme_id),
    CONSTRAINT fk_like_editor FOREIGN KEY (editor_id) REFERENCES editor(editor_id) ON DELETE CASCADE,
    CONSTRAINT fk_like_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id) ON DELETE CASCADE
);

CREATE TABLE tag (
    tag_id BIGINT NOT NULL AUTO_INCREMENT,
    tag_name VARCHAR(30) NOT NULL,
    PRIMARY KEY (tag_id),
    UNIQUE KEY uk_tag_name (tag_name)
);

CREATE TABLE tag_in_theme (
    theme_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (theme_id, tag_id),
    CONSTRAINT fk_tag_theme FOREIGN KEY (theme_id) REFERENCES theme(theme_id) ON DELETE CASCADE,
    CONSTRAINT fk_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(tag_id) ON DELETE CASCADE
);

CREATE TABLE comment (
    comment_id BIGINT NOT NULL AUTO_INCREMENT,
    place_id VARCHAR(64) NOT NULL,
    content VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (comment_id),
    KEY idx_comment_place (place_id, comment_id),
    CONSTRAINT fk_comment_place FOREIGN KEY (place_id) REFERENCES place(place_id) ON DELETE CASCADE
);

INSERT INTO tag (tag_name) VALUES
    ('맛집'), ('카페'), ('산책'), ('데이트'), ('여행'), ('문화');
