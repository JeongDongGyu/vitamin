// DatedTask.java
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatedTask extends Task {
    private LocalDate dueDate;

    public DatedTask(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    // 부모의 추상 메서드 구현
    @Override
    public String toFileString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return (isCompleted ? "[X] " : "[ ] ") + description + " (기한: " + dueDate.format(formatter) + ")";
    }
}