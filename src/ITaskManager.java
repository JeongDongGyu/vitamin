
// ITaskManager.java
import java.time.LocalDate;

public interface ITaskManager {
    void addTask(String description);
    void addDatedTask(String description, LocalDate dueDate);
    void listTasks();
    void completeTask(int taskNumber);
    void deleteTask(int taskNumber);
}