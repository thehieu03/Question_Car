package interfa;

public interface IExamSetDAO {
    int getTotalExamSets();
    java.util.List<model.ExamSet> getExamSets(int offset, int limit);
    boolean addExamSet(String examName, int totalQuestions, int durationMinutes, int passingScore);
}

