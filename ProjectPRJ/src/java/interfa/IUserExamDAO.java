package interfa;

public interface IUserExamDAO {
    int getTotalUserExams();

    java.util.List<model.UserExam> getUserExams(int offset, int limit);

    java.util.List<model.UserExam> getUserExamsWithInfo(int offset, int limit);

    int getTotalUserExamsByUser(int userId);

    int getPassedUserExamsByUser(int userId);

    Integer getLastScoreByUser(int userId);

    int startExam(int userId, int examSetId);

    boolean saveUserAnswer(int userExamId, int questionId, Integer answerId, Boolean isCorrect);

    boolean submitExam(int userExamId, int totalScore, int correctAnswers, int wrongAnswers, boolean isPassed);

    model.UserExam getUserExamById(int userExamId);

    model.UserExam getInProgressExam(int userId, int examSetId);

    java.util.List<model.UserExam> getUserExamsByUser(int userId, int offset, int limit);
}
