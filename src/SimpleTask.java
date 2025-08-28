// SimpleTask.java
public class SimpleTask extends Task {
    public SimpleTask(String description) {
        super(description);
    }

    // 부모의 추상 메서드 구현
    @Override
    public String toFileString() {
        return (isCompleted ? "[X] " : "[ ] ") + description;
    }
}