package interfa;

import java.util.List;
import model.QuestionCategory;

public interface IQuestionCategoryDAO {
    List<QuestionCategory> getAllCategories();
    QuestionCategory getCategoryById(int categoryId);
    boolean addCategory(String categoryName);
    boolean updateCategory(int categoryId, String categoryName);
    boolean deleteCategory(int categoryId);
    int getTotalCategories();
}

