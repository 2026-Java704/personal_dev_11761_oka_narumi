-- users テーブルにデータを挿入するクエリ
INSERT INTO users (email, name, password) VALUES
('tanaka@aaa.com', '田中太郎', 'test123'),
('suzuki@aaa.com', '鈴木一郎', 'test456');

INSERT INTO categories (user_id, name) VALUES
(0, '運動'),
(0, '勉強'),
(0, 'その他');


--categories テーブルにデータを挿入するクエリ


-- tasks テーブルにデータを挿入するクエリ
INSERT INTO tasks (user_id,category_id, title, closing_date, progress, importance, memo)
VALUES
(1, 1, '散歩', '2026/5/18', 0, 0, '最低20分散歩する'),
(2, 2, '勉強', '2026/5/18', 1, 1, '最低20分勉強する');