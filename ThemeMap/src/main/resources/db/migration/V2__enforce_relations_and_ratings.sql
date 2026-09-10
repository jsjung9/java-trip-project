CREATE TABLE IF NOT EXISTS place_score (
    place_id VARCHAR(64) NOT NULL,
    editor_id VARCHAR(50) NOT NULL,
    score TINYINT NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (place_id, editor_id),
    CONSTRAINT chk_score_range CHECK (score BETWEEN 1 AND 5)
);

CREATE TEMPORARY TABLE like_theme_dedup (editor_id BIGINT NOT NULL, theme_id BIGINT NOT NULL);
INSERT INTO like_theme_dedup SELECT DISTINCT editor_id, theme_id FROM like_theme;
DELETE FROM like_theme;
INSERT INTO like_theme SELECT editor_id, theme_id FROM like_theme_dedup;
DROP TEMPORARY TABLE like_theme_dedup;

CREATE TEMPORARY TABLE place_in_theme_dedup (theme_id BIGINT NOT NULL, place_id VARCHAR(64) NOT NULL, editor_id BIGINT NOT NULL);
INSERT INTO place_in_theme_dedup
SELECT theme_id, place_id, MIN(editor_id) FROM place_in_theme GROUP BY theme_id, place_id;
DELETE FROM place_in_theme;
INSERT INTO place_in_theme SELECT theme_id, place_id, editor_id FROM place_in_theme_dedup;
DROP TEMPORARY TABLE place_in_theme_dedup;

CREATE TEMPORARY TABLE tag_in_theme_dedup (theme_id BIGINT NOT NULL, tag_id BIGINT NOT NULL);
INSERT INTO tag_in_theme_dedup SELECT DISTINCT theme_id, tag_id FROM tag_in_theme;
DELETE FROM tag_in_theme;
INSERT INTO tag_in_theme SELECT theme_id, tag_id FROM tag_in_theme_dedup;
DROP TEMPORARY TABLE tag_in_theme_dedup;

ALTER TABLE like_theme ADD UNIQUE KEY uk_like_editor_theme (editor_id, theme_id);
ALTER TABLE place_in_theme ADD UNIQUE KEY uk_place_theme (theme_id, place_id);
ALTER TABLE tag_in_theme ADD UNIQUE KEY uk_tag_theme (theme_id, tag_id);
