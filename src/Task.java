// Task.java
public abstract class Task {
    protected String description;
    protected boolean isCompleted;

    public Task(String description) {
        this.description = description;
        this.isCompleted = false;
    }

    public void complete() {
        this.isCompleted = true;
    }

    public String getDescription() {
        return this.description;
    }

    // 자식 클래스가 반드시 구현해야 하는 추상 메서드 (다형성을 위함)
    public abstract String toFileString();
}