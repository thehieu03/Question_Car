package interfa;

public interface IExamSetDAO {
    int getTotalExamSets();
    java.util.List<model.ExamSet> getExamSets(int offset, int limit);
    boolean addExamSet(String examName, int totalQuestions, int durationMinutes, int passingScore);
    model.ExamSet getExamSetById(int examSetId);
    java.util.List<model.Question> getQuestionsByExamSet(int examSetId);
    boolean deleteExamSet(int examSetId);
    int getLastInsertedExamSetId();
    boolean addExamQuestions(int examSetId, java.util.List<model.Question> questions);
}

