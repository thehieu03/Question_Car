package interfa;

public interface IUserExamDAO {
    int getTotalUserExams();
    java.util.List<model.UserExam> getUserExams(int offset, int limit);
    java.util.List<model.UserExam> getUserExamsWithInfo(int offset, int limit);
    int getTotalUserExamsByUser(int userId);
    int getPassedUserExamsByUser(int userId);
    Integer getLastScoreByUser(int userId);
}

