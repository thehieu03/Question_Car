# Hệ Thống Quản Lý Ôn Thi Bằng Lái Xe Ôtô

## Mô tả dự án

Hệ thống quản lý và ôn thi bằng lái xe ôtô được xây dựng bằng Java Web (Jakarta EE), hỗ trợ người dùng làm bài thi thử trực tuyến với tính năng đếm ngược thời gian, quản lý câu hỏi, đề thi và xem kết quả chi tiết.

## Công nghệ sử dụng

- **Backend**: Java Web (Jakarta EE - JSP/Servlet)
- **Frontend**: HTML, CSS, JavaScript, JSP
- **Database**: Microsoft SQL Server
- **JDBC Driver**: Microsoft SQL Server JDBC Driver (mssql-jdbc-13.2.0.jre11.jar)
- **Build Tool**: Apache Ant (NetBeans)
- **Server**: Apache Tomcat (Jakarta EE compatible)

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

Cấu hình kết nối database trong file `src/java/dbcontext/DBContext.java`:

```java
private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=DrivingLicenseExam;encrypt=true;trustServerCertificate=true";
private static final String DB_USER = "sa";
private static final String DB_PASSWORD = "123";
```

**Lưu ý**: Thay đổi `DB_USER` và `DB_PASSWORD` theo cấu hình SQL Server của bạn.

### 3. Dependencies

Các thư viện cần thiết đã được đặt trong `web/WEB-INF/lib/`:

- `mssql-jdbc-13.2.0.jre11.jar` - Microsoft SQL Server JDBC Driver
- `jakarta.servlet.jsp.jstl-2.0.0.jar` - Jakarta JSTL
- `jakarta.servlet.jsp.jstl-api-2.0.0.jar` - Jakarta JSTL API

### 4. Build và Deploy

**Sử dụng NetBeans IDE:**

1. Mở project trong NetBeans
2. Clean and Build project (F11)
3. Deploy lên Tomcat server

**Sử dụng Ant (command line):**

```bash
ant clean build
```

File WAR sẽ được tạo trong thư mục `dist/ProjectPRJ.war`

## Tính năng

### Người dùng (User)

- **Đăng ký/Đăng nhập**: Tạo tài khoản và đăng nhập vào hệ thống
- **Chọn đề thi**: Xem danh sách đề thi theo danh mục, xem thống kê (số lần làm, số lần đỗ, điểm lần cuối)
- **Làm bài thi**:
  - Hiển thị câu hỏi từng câu một
  - Đếm ngược thời gian (tự động nộp bài khi hết thời gian)
  - Điều hướng giữa các câu hỏi (Previous/Next)
  - Panel điều hướng hiển thị câu hỏi đã trả lời với màu khác
  - Lưu câu trả lời tự động
  - Tiếp tục bài thi đang làm dở (nếu có)
- **Xem kết quả thi**:
  - Xem điểm số, số câu đúng/sai
  - Xem đáp án đúng và giải thích cho từng câu
  - Xem lại câu trả lời của mình
- **Xem lịch sử thi**: Xem tất cả các bài thi đã làm với phân trang
- **Comment trên đề thi**: Bình luận về đề thi sau khi hoàn thành (tùy chọn)

### Quản trị viên (Admin)

- **Quản lý người dùng**:
  - Xem danh sách người dùng với phân trang
  - Tìm kiếm người dùng theo username/email
  - Sửa thông tin người dùng
  - Xóa người dùng (tự động xóa tất cả bài thi và dữ liệu liên quan)
- **Quản lý danh mục câu hỏi**: CRUD danh mục
- **Quản lý câu hỏi và đáp án**:
  - CRUD câu hỏi
  - Quản lý đáp án cho từng câu hỏi
  - Upload hình ảnh cho câu hỏi
  - Đánh dấu câu hỏi điểm liệt
- **Quản lý đề thi**:
  - Tạo, sửa, xóa đề thi
  - Thêm/xóa câu hỏi vào đề thi
  - Xem chi tiết đề thi
- **Xem kết quả thi**: Xem tất cả bài thi của người dùng với phân trang

## Dữ liệu mẫu

File `sql.sql` đã bao gồm dữ liệu mẫu:

- 3 người dùng (1 admin, 2 user)
- 2 danh mục câu hỏi (Ô tô, Xe máy)
- 400 câu hỏi (200 Ô tô + 200 Xe máy)
- 1600 đáp án (4 đáp án/câu)
- 60 bộ đề thi
- Dữ liệu mẫu cho các bảng khác

## Kiến trúc và Design Patterns

- **MVC Pattern**: Model-View-Controller được áp dụng
  - Model: Các class trong package `model/`
  - View: Các file JSP trong `web/`
  - Controller: Các Servlet trong `serverlet/`
- **DAO Pattern**: Data Access Object pattern cho việc truy cập database
- **Filter Pattern**: Servlet Filter cho authentication và authorization
  - `AdminFilter`: Bảo vệ các trang admin
  - `UserFilter`: Bảo vệ các trang user

## Cấu trúc Code

### DAO Layer

- `UserDAO`: Quản lý người dùng
- `UserExamDAO`: Quản lý bài thi của người dùng
- `ExamSetDAO`: Quản lý đề thi
- `QuestionDAO`: Quản lý câu hỏi
- `AnswerDAO`: Quản lý đáp án
- `QuestionCategoryDAO`: Quản lý danh mục câu hỏi
- `ExamSetCommentDAO`: Quản lý bình luận

### Servlet Layer

- **Common**: `LoginServlet`, `RegisterServlet`, `LogoutServlet`
- **User**: `ExamSelectionServlet`, `ExamStartServlet`, `ExamResultServlet`, `ExamHistoryServlet`, `CommentServlet`, `UserHomeServlet`
- **Admin**: `UserManagementServlet`, `QuestionManagementServlet`, `ExamSetManagementServlet`, `CategoryManagementServlet`, `ExamResultsServlet`, `AdminServlet`

## Tính năng kỹ thuật

- **Session Management**: Sử dụng HttpSession để quản lý phiên đăng nhập
- **Cookie**: Lưu thông tin username và role trong cookie
- **Pagination**: Phân trang cho danh sách người dùng, đề thi, bài thi
- **Transaction Management**: Sử dụng transaction cho các thao tác xóa phức tạp
- **Cascade Delete**: Xóa người dùng sẽ tự động xóa tất cả bài thi và dữ liệu liên quan
- **Real-time Timer**: JavaScript countdown timer cho bài thi
- **Dynamic UI**: JavaScript cho điều hướng câu hỏi và cập nhật UI động

## Lưu ý

- Tất cả các model classes implement `Serializable` để hỗ trợ lưu trữ trong session
- Password được lưu dạng plain text (nên hash trong production)
- Database connection được cấu hình trực tiếp trong `DBContext.java`
- Project sử dụng Jakarta EE (không phải Java EE cũ)
- Server cần hỗ trợ Jakarta EE (Tomcat 10+ hoặc tương đương)

## Tác giả

FPT University - PRJ30X

## License

Dự án học tập - FPT University
