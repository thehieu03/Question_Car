package interfa;

import java.util.List;
import model.Answer;

public interface IAnswerDAO {
    List<Answer> getAnswersByQuestionId(int questionId);
    boolean addAnswer(int questionId, String answerText, boolean isCorrect, int answerOrder);
    boolean updateAnswer(int answerId, String answerText, boolean isCorrect, int answerOrder);
    boolean deleteAnswer(int answerId);
    boolean deleteAnswersByQuestionId(int questionId);
}

