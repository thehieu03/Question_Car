package interfa;

import java.util.List;
import model.Question;

public interface IQuestionDAO {
    int getTotalQuestions();
    List<Question> getAllQuestions(int offset, int limit);
    List<Question> searchQuestions(String keyword, int offset, int limit);
    int getTotalQuestionsBySearch(String keyword);
    Question getQuestionById(int questionId);
    boolean addQuestion(int categoryId, String questionText, String questionImage, String explanation, boolean isCritical);
    boolean updateQuestion(int questionId, int categoryId, String questionText, String questionImage, String explanation, boolean isCritical);
    boolean deleteQuestion(int questionId);
    List<Question> getQuestionsFiltered(String keyword, String type, Integer categoryId, int offset, int limit);
    int getTotalQuestionsFiltered(String keyword, String type, Integer categoryId);
}

