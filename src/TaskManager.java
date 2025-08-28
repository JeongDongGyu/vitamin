// TaskManager.java
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    // Task의 역할을 하는 private record를 내부에 품습니다.
    private record Task(String description, boolean isCompleted) {
        public String toFileString() {
            return (isCompleted ? "[X] " : "[ ] ") + description;
        }
    }

    private final String fileName;
    private final List<Task> tasks;

    // 생성될 때 파일로부터 데이터를 불러와 준비합니다.
    public TaskManager(String fileName) {
        this.fileName = fileName;
        this.tasks = loadTasks();
    }

    // --- 핵심 기능들 ---
    public void addTask(String description) {
        tasks.add(new Task(description, false));
        System.out.println("'" + description + "' 할 일이 추가되었습니다.");
        saveTasks(); // 변경 후 바로 저장
    }

    public void listTasks() {
        System.out.println("[To-Do List]");
        if (tasks.isEmpty()) {
            System.out.println("할 일이 없습니다.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i).toFileString());
            }
        }
    }

    public void completeTask(int taskNumber) {
        if (taskNumber > 0 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            tasks.set(taskNumber - 1, new Task(task.description(), true));
            System.out.println(taskNumber + "번 할 일을 완료 처리했습니다.");
            saveTasks(); // 변경 후 바로 저장
        } else {
            System.out.println("잘못된 번호입니다.");
        }
    }

    public void deleteTask(int taskNumber) {
        if (taskNumber > 0 && taskNumber <= tasks.size()) {
            Task removed = tasks.remove(taskNumber - 1);
            System.out.println("'" + removed.description() + "' 할 일을 삭제했습니다.");
            saveTasks(); // 변경 후 바로 저장
        } else {
            System.out.println("잘못된 번호입니다.");
        }
    }

    // --- 파일 입출력 (private으로 숨김) ---
    private List<Task> loadTasks() {
        List<Task> loaded = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean isCompleted = line.startsWith("[X]");
                String description = line.substring(4);
                loaded.add(new Task(description, isCompleted));
            }
        } catch (IOException e) {
            // 파일이 없으면 빈 리스트로 시작
        }
        return loaded;
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("파일 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}