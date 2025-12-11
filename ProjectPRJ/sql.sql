-- ============================================
-- SCHEMA QUẢN LÝ ÔN THI BẰNG LÁI XE ÔTÔ
-- SQL Server Database Schema
-- ============================================

-- (Tùy chọn) XÓA DB CŨ NẾU CÓ
-- USE master;
-- IF DB_ID('DrivingLicenseExam') IS NOT NULL
--     DROP DATABASE DrivingLicenseExam;
-- GO

-- Tạo database
CREATE DATABASE DrivingLicenseExam;
GO

USE DrivingLicenseExam;
GO

-- ============================================
-- 1. BẢNG NGƯỜI DÙNG (Users)
-- ============================================
CREATE TABLE Users (
    user_id   INT IDENTITY(1,1) PRIMARY KEY,
    username  NVARCHAR(50)  NOT NULL UNIQUE,
    email     NVARCHAR(100) NOT NULL UNIQUE,
    password  NVARCHAR(255) NOT NULL,          -- lưu password đã hash
    role      INT NOT NULL CHECK (role IN (0, 1))  -- 0 = USER, 1 = ADMIN
);

-- ============================================
-- 2. BẢNG DANH MỤC CÂU HỎI (QuestionCategories)
-- ============================================
CREATE TABLE QuestionCategories (
    category_id   INT IDENTITY(1,1) PRIMARY KEY,
    category_name NVARCHAR(100) NOT NULL
);

-- ============================================
-- 3. BẢNG CÂU HỎI (Questions)
-- ============================================
CREATE TABLE Questions (
    question_id    INT IDENTITY(1,1) PRIMARY KEY,
    category_id    INT NOT NULL,
    question_text  NVARCHAR(1000) NOT NULL,
    question_image NVARCHAR(500),      -- URL hoặc path đến hình ảnh
    explanation    NVARCHAR(1000),     -- Giải thích đáp án
    is_critical    BIT DEFAULT 0,      -- Câu hỏi điểm liệt
    FOREIGN KEY (category_id) REFERENCES QuestionCategories(category_id)
);

-- ============================================
-- 4. BẢNG ĐÁP ÁN (Answers)
-- ============================================
CREATE TABLE Answers (
    answer_id    INT IDENTITY(1,1) PRIMARY KEY,
    question_id  INT NOT NULL,
    answer_text  NVARCHAR(500) NOT NULL,
    is_correct   BIT NOT NULL DEFAULT 0,
    answer_order INT NOT NULL, -- Thứ tự hiển thị (A, B, C, D)
    FOREIGN KEY (question_id) REFERENCES Questions(question_id) ON DELETE CASCADE
);

-- ============================================
-- 5. BẢNG BỘ ĐỀ THI (ExamSets)
-- ============================================
CREATE TABLE ExamSets (
    exam_set_id      INT IDENTITY(1,1) PRIMARY KEY,
    exam_name        NVARCHAR(200) NOT NULL,
    total_questions  INT NOT NULL,
    duration_minutes INT NOT NULL,  -- Thời gian làm bài (phút)
    passing_score    INT NOT NULL   -- Điểm đạt (số câu đúng tối thiểu)
);

-- ============================================
-- 6. BẢNG COMMENT CHO BỘ ĐỀ THI (ExamSetComments)
-- ============================================
CREATE TABLE ExamSetComments (
    comment_id  INT IDENTITY(1,1) PRIMARY KEY,
    exam_set_id INT NOT NULL,
    user_id     INT NOT NULL,
    content     NVARCHAR(1000) NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT GETDATE(),
    FOREIGN KEY (exam_set_id) REFERENCES ExamSets(exam_set_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id)     REFERENCES Users(user_id)
);

-- ============================================
-- 7. BẢNG CÂU HỎI TRONG ĐỀ THI (ExamQuestions)
-- ============================================
CREATE TABLE ExamQuestions (
    exam_question_id INT IDENTITY(1,1) PRIMARY KEY,
    exam_set_id      INT NOT NULL,
    question_id      INT NOT NULL,
    question_order   INT NOT NULL,
    FOREIGN KEY (exam_set_id) REFERENCES ExamSets(exam_set_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES Questions(question_id)
);

-- Không cho 1 câu xuất hiện 2 lần trong cùng 1 đề
ALTER TABLE ExamQuestions
ADD CONSTRAINT UQ_ExamQuestions_Exam_Question UNIQUE (exam_set_id, question_id);

-- ============================================
-- 8. BẢNG BÀI THI CỦA NGƯỜI DÙNG (UserExams)
-- ============================================
CREATE TABLE UserExams (
    user_exam_id    INT IDENTITY(1,1) PRIMARY KEY,
    user_id         INT NOT NULL,
    exam_set_id     INT NOT NULL,
    start_time      DATETIME NOT NULL DEFAULT GETDATE(),
    end_time        DATETIME,
    total_score     INT,
    correct_answers INT,
    wrong_answers   INT,
    is_passed       BIT,
    status          NVARCHAR(20) CHECK (status IN ('IN_PROGRESS', 'COMPLETED')),
    FOREIGN KEY (user_id) REFERENCES Users(user_id),
    FOREIGN KEY (exam_set_id) REFERENCES ExamSets(exam_set_id)
);

-- ============================================
-- 9. BẢNG CÂU TRẢ LỜI CỦA NGƯỜI DÙNG (UserAnswers)
-- ============================================
CREATE TABLE UserAnswers (
    user_answer_id INT IDENTITY(1,1) PRIMARY KEY,
    user_exam_id   INT NOT NULL,
    question_id    INT NOT NULL,
    answer_id      INT,        -- có thể NULL nếu user bỏ câu
    is_correct     BIT,
    FOREIGN KEY (user_exam_id) REFERENCES UserExams(user_exam_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id)  REFERENCES Questions(question_id),
    FOREIGN KEY (answer_id)    REFERENCES Answers(answer_id)
);

-- 1 câu trong 1 lần thi chỉ có 1 dòng trả lời
ALTER TABLE UserAnswers
ADD CONSTRAINT UQ_UserAnswers_Exam_Question UNIQUE (user_exam_id, question_id);



-- Insert data


-- ============================================
-- SEED DATA FOR DrivingLicenseExam
-- Mỗi bảng (trừ Users) ~60 bản ghi
-- Giả định: Database và các bảng đã được tạo, đang trống.
-- ============================================

USE DrivingLicenseExam;
SET NOCOUNT ON;

------------------------------------------------
-- 1. SEED USERS (ít bản ghi, không yêu cầu 60)
------------------------------------------------
INSERT INTO Users (username, email, password, role)
VALUES
    (N'admin',   N'admin@example.com',   N'admin123', 1),
    (N'user1',   N'user1@example.com',   N'user1pass', 0),
    (N'user2',   N'user2@example.com',   N'user2pass', 0);


------------------------------------------------
-- 2. SEED QUESTION CATEGORIES (2 chủ đề: Ô tô và Xe máy)
------------------------------------------------
INSERT INTO QuestionCategories (category_name)
VALUES
    (N'Ô tô'),
    (N'Xe máy');


------------------------------------------------
-- 3. SEED QUESTIONS (400 bản ghi: 200 Ô tô + 200 Xe máy)
------------------------------------------------
-- 200 câu hỏi Ô tô
;WITH Numbers AS (
    SELECT TOP (200) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.all_objects
)
INSERT INTO Questions (category_id, question_text, question_image, explanation, is_critical)
SELECT
    1 AS category_id,  -- Ô tô
    CONCAT(N'Câu hỏi Ô tô số ', n, N': Nội dung mô phỏng.'),
    NULL,
    N'Giải thích: nội dung mô phỏng cho câu hỏi ô tô.',
    CASE WHEN n % 20 = 0 THEN 1 ELSE 0 END  -- cứ 20 câu có 1 câu liệt
FROM Numbers;

-- 200 câu hỏi Xe máy
;WITH Numbers AS (
    SELECT TOP (200) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.all_objects
)
INSERT INTO Questions (category_id, question_text, question_image, explanation, is_critical)
SELECT
    2 AS category_id,  -- Xe máy
    CONCAT(N'Câu hỏi Xe máy số ', n, N': Nội dung mô phỏng.'),
    NULL,
    N'Giải thích: nội dung mô phỏng cho câu hỏi xe máy.',
    CASE WHEN n % 20 = 0 THEN 1 ELSE 0 END  -- cứ 20 câu có 1 câu liệt
FROM Numbers;


------------------------------------------------
-- 4. SEED ANSWERS (4 đáp án cho mỗi câu = 400 × 4 = 1600 bản ghi)
------------------------------------------------
;WITH Q AS (
    SELECT question_id
    FROM Questions
),
C AS (
    SELECT 1 AS answer_order
    UNION ALL SELECT 2
    UNION ALL SELECT 3
    UNION ALL SELECT 4
)
INSERT INTO Answers (question_id, answer_text, is_correct, answer_order)
SELECT
    Q.question_id,
    CONCAT(
        N'Đáp án ',
        CASE C.answer_order
            WHEN 1 THEN N'A'
            WHEN 2 THEN N'B'
            WHEN 3 THEN N'C'
            ELSE N'D'
        END,
        N' cho câu ',
        Q.question_id
    ),
    CASE WHEN C.answer_order = 3 THEN 1 ELSE 0 END, -- C là đáp án đúng
    C.answer_order
FROM Q
CROSS JOIN C;


------------------------------------------------
-- 5. SEED EXAM SETS (~60 bản ghi)
------------------------------------------------
;WITH Numbers AS (
    SELECT TOP (60) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.objects
)
INSERT INTO ExamSets (exam_name, total_questions, duration_minutes, passing_score)
SELECT
    CONCAT(N'Đề số ', n)    AS exam_name,
    1                       AS total_questions,   -- mỗi đề 1 câu (cho đơn giản, khớp với ExamQuestions)
    10                      AS duration_minutes,
    1                       AS passing_score
FROM Numbers;


------------------------------------------------
-- 6. SEED EXAM SET COMMENTS (~60 bản ghi)
------------------------------------------------
;WITH Numbers AS (
    SELECT TOP (60) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.objects
)
INSERT INTO ExamSetComments (exam_set_id, user_id, content, created_at)
SELECT
    ((n - 1) % 60) + 1               AS exam_set_id, -- đề 1..60
    ((n - 1) % 3) + 1                AS user_id,     -- user 1..3
    CONCAT(N'Nhận xét số ', n, N' cho đề ', ((n - 1) % 60) + 1),
    DATEADD(MINUTE, -n, GETDATE())   AS created_at
FROM Numbers;


------------------------------------------------
-- 7. SEED EXAM QUESTIONS (~60 bản ghi, mapping 1-1 giữa đề và câu)
------------------------------------------------
;WITH Numbers AS (
    SELECT TOP (60) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.objects
)
INSERT INTO ExamQuestions (exam_set_id, question_id, question_order)
SELECT
    n AS exam_set_id,
    n AS question_id,
    1 AS question_order
FROM Numbers;


------------------------------------------------
-- 8. SEED USER EXAMS (~60 bản ghi)
------------------------------------------------
;WITH Numbers AS (
    SELECT TOP (60) ROW_NUMBER() OVER (ORDER BY (SELECT NULL)) AS n
    FROM sys.objects
)
INSERT INTO UserExams (user_id, exam_set_id, start_time, end_time, total_score, correct_answers, wrong_answers, is_passed, status)
SELECT
    ((n - 1) % 3) + 1          AS user_id,         -- user 1..3
    ((n - 1) % 60) + 1         AS exam_set_id,     -- đề 1..60 (quay vòng nếu >60)
    DATEADD(MINUTE, -20, GETDATE()) AS start_time,
    GETDATE()                  AS end_time,
    1                          AS total_score,
    1                          AS correct_answers,
    0                          AS wrong_answers,
    1                          AS is_passed,
    N'COMPLETED'               AS status
FROM Numbers;


------------------------------------------------
-- 9. SEED USER ANSWERS (~60 bản ghi, mỗi lần thi trả lời 1 câu đúng)
------------------------------------------------
INSERT INTO UserAnswers (user_exam_id, question_id, answer_id, is_correct)
SELECT
    ue.user_exam_id,
    eq.question_id,
    ans.answer_id,
    1 AS is_correct
FROM UserExams ue
JOIN ExamQuestions eq
    ON ue.exam_set_id = eq.exam_set_id
JOIN Answers ans
    ON ans.question_id = eq.question_id
   AND ans.is_correct = 1;  -- luôn chọn đáp án đúng (C)

-- Hết seed script




-- ============================================
-- UPDATE tất cả câu hỏi để có cùng hình ảnh
-- ============================================

USE DrivingLicenseExam;

UPDATE Questions
SET question_image = N'https://llumar.com.vn/wp-content/uploads/2024/11/bang-gia-xe-o-to-dien-vinfat.webp'
WHERE question_image IS NULL OR question_image = '';

-- Kiểm tra kết quả
SELECT 
    category_id,
    COUNT(*) AS total_questions,
    SUM(CASE WHEN question_image IS NOT NULL THEN 1 ELSE 0 END) AS questions_with_image
FROM Questions
GROUP BY category_id;
