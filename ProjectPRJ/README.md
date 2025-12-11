# Hệ Thống Quản Lý Ôn Thi Bằng Lái Xe Ôtô

## Mô tả dự án

Hệ thống quản lý và ôn thi bằng lái xe ôtô được xây dựng bằng Java, hỗ trợ người dùng làm bài thi thử và quản lý câu hỏi, đề thi.

## Công nghệ sử dụng

- **Backend**: Java (JSP/Servlet)
- **Database**: SQL Server
- **JDBC Driver**: Microsoft SQL Server JDBC Driver

## Cấu trúc Database

Database: `DrivingLicenseExam`

### Các bảng chính:

1. **Users** - Quản lý người dùng
   - `user_id` (PK)
   - `username`, `email`, `password`
   - `role` (0 = USER, 1 = ADMIN)

2. **QuestionCategories** - Danh mục câu hỏi
   - `category_id` (PK)
   - `category_name`

3. **Questions** - Câu hỏi
   - `question_id` (PK)
   - `category_id` (FK)
   - `question_text`, `question_image`, `explanation`
   - `is_critical` (Câu hỏi điểm liệt)

4. **Answers** - Đáp án
   - `answer_id` (PK)
   - `question_id` (FK)
   - `answer_text`, `is_correct`, `answer_order`

5. **ExamSets** - Bộ đề thi
   - `exam_set_id` (PK)
   - `exam_name`, `total_questions`, `duration_minutes`, `passing_score`

6. **ExamSetComments** - Comment cho đề thi
   - `comment_id` (PK)
   - `exam_set_id` (FK), `user_id` (FK)
   - `content`, `created_at`

7. **ExamQuestions** - Câu hỏi trong đề thi
   - `exam_question_id` (PK)
   - `exam_set_id` (FK), `question_id` (FK), `question_order`

8. **UserExams** - Bài thi của người dùng
   - `user_exam_id` (PK)
   - `user_id` (FK), `exam_set_id` (FK)
   - `start_time`, `end_time`, `total_score`, `correct_answers`, `wrong_answers`
   - `is_passed`, `status` (IN_PROGRESS, COMPLETED)

9. **UserAnswers** - Câu trả lời của người dùng
   - `user_answer_id` (PK)
   - `user_exam_id` (FK), `question_id` (FK), `answer_id` (FK)
   - `is_correct`

## Cấu trúc Project

```
ProjectPRJ/
├── src/
│   └── java/
│       ├── model/          # Model classes
│       │   ├── User.java
│       │   ├── QuestionCategory.java
│       │   ├── Question.java
│       │   ├── Answer.java
│       │   ├── ExamSet.java
│       │   ├── ExamSetComment.java
│       │   ├── ExamQuestion.java
│       │   ├── UserExam.java
│       │   └── UserAnswer.java
│       ├── dbcontext/      # Database connection
│       │   └── DBContext.java
│       ├── dao/            # Data Access Object
│       ├── filter/         # Servlet filters
│       ├── interfa/        # Interfaces
│       └── serverlet/      # Servlets
├── web/                    # Web resources
│   ├── admin/              # Admin pages
│   ├── user/               # User pages
│   └── common/             # Common resources
├── sql.sql                 # Database schema và seed data
└── README.md               # File này
```

## Model Classes

Tất cả các model classes đều implement `Serializable` để hỗ trợ serialization:

- **User**: Quản lý thông tin người dùng
- **QuestionCategory**: Danh mục câu hỏi (Ô tô, Xe máy)
- **Question**: Câu hỏi thi
- **Answer**: Đáp án của câu hỏi
- **ExamSet**: Bộ đề thi
- **ExamSetComment**: Comment về đề thi
- **ExamQuestion**: Mối quan hệ giữa đề thi và câu hỏi
- **UserExam**: Bài thi của người dùng
- **UserAnswer**: Câu trả lời của người dùng

## Cài đặt và Cấu hình

### 1. Database Setup

Chạy file `sql.sql` trong SQL Server Management Studio để tạo database và các bảng:

```sql
-- File: sql.sql
-- Tạo database DrivingLicenseExam
-- Tạo các bảng và insert dữ liệu mẫu
```

### 2. Cấu hình Database Connection

Tạo file `ConnectDB.properties` trong thư mục resources với nội dung:

```properties
userID=your_username
password=your_password
url=jdbc:sqlserver://localhost:1433;databaseName=DrivingLicenseExam;encrypt=true;trustServerCertificate=true
```

### 3. Dependencies

Đảm bảo có Microsoft SQL Server JDBC Driver trong classpath:
- `sqljdbc_auth.dll`
- `mssql-jdbc.jar`

## Tính năng

### Người dùng (User)
- Đăng ký/Đăng nhập
- Làm bài thi thử
- Xem kết quả thi
- Xem lịch sử thi
- Comment trên đề thi

### Quản trị viên (Admin)
- Quản lý người dùng
- Quản lý câu hỏi và đáp án
- Quản lý đề thi
- Xem thống kê

## Dữ liệu mẫu

File `sql.sql` đã bao gồm dữ liệu mẫu:
- 3 người dùng (1 admin, 2 user)
- 2 danh mục câu hỏi (Ô tô, Xe máy)
- 400 câu hỏi (200 Ô tô + 200 Xe máy)
- 1600 đáp án (4 đáp án/câu)
- 60 bộ đề thi
- Dữ liệu mẫu cho các bảng khác

## Lưu ý

- Tất cả các model classes implement `Serializable` để hỗ trợ lưu trữ trong session
- Password nên được hash trước khi lưu vào database
- File `ConnectDB.properties` cần được đặt đúng vị trí để DBContext có thể đọc được

## Tác giả

FPT University - PRJ30X

