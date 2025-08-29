// TaskManager.java
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 4단계에서 생성할 인터페이스를 구현(implements)합니다.
public class TaskManager implements ITaskManager {
    // private record Task(...) 삭제

    private final String fileName;
    private final List<Task> tasks; // List의 타입을 Task(추상 클래스)로 변경

    public TaskManager(String fileName) {
        this.fileName = fileName;
        this.tasks = loadTasks();
    }

    // 할 일 추가 기능을 오버로딩하여 2가지 형태로 만듭니다.
    @Override
    public void addTask(String description) {
        tasks.add(new SimpleTask(description)); // SimpleTask 객체 생성
        System.out.println("'" + description + "' 할 일이 추가되었습니다.");
        saveTasks();
    }

    // 날짜가 있는 할 일을 추가하는 메서드 (새로운 기능)
    @Override
    public void addDatedTask(String description, LocalDate dueDate) {
        tasks.add(new DatedTask(description, dueDate)); // DatedTask 객체 생성
        System.out.println("'" + description + "' 할 일(기한: " + dueDate + ")이 추가되었습니다.");
        saveTasks();
    }

    @Override
    public void listTasks() {
        System.out.println("[To-Do List]");
        if (tasks.isEmpty()) {
            System.out.println("할 일이 없습니다.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                // 다형성: task 객체가 SimpleTask이든 DatedTask이든 상관없이 toFileString()을 호출하면
                // 각 객체에 맞게 오버라이딩된 메서드가 실행됩니다.
                System.out.println((i + 1) + ". " + tasks.get(i).toFileString());
            }
        }
    }

    @Override
    public void completeTask(int taskNumber) {
        if (taskNumber > 0 && taskNumber <= tasks.size()) {
            Task task = tasks.get(taskNumber - 1);
            task.complete(); // Task에 구현된 공통 메서드 호출
            System.out.println(taskNumber + "번 할 일을 완료 처리했습니다.");
            saveTasks();
        } else {
            System.out.println("잘못된 번호입니다.");
        }
    }

    @Override
    public void deleteTask(int taskNumber) {
        if (taskNumber > 0 && taskNumber <= tasks.size()) {
            Task removed = tasks.remove(taskNumber - 1);
            System.out.println("'" + removed.getDescription() + "' 할 일을 삭제했습니다.");
            saveTasks();
        } else {
            System.out.println("잘못된 번호입니다.");
        }
    }

    // 파일 입출력 로직 수정
    private List<Task> loadTasks() {
        List<Task> loaded = new ArrayList<>();
        Pattern datedPattern = Pattern.compile("(.+?) \\(기한: (\\d{4}-\\d{2}-\\d{2})\\)");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                boolean isCompleted = line.startsWith("[X]");
                String content = line.substring(4);

                Matcher matcher = datedPattern.matcher(content);
                Task task;

                if (matcher.find()) { // " (기한: YYYY-MM-DD)" 패턴이 있으면 DatedTask로 인식
                    String description = matcher.group(1);
                    LocalDate dueDate = LocalDate.parse(matcher.group(2));
                    task = new DatedTask(description, dueDate);
                } else { // 없으면 SimpleTask로 인식
                    task = new SimpleTask(content);
                }

                if (isCompleted) {
                    task.complete();
                }
                loaded.add(task);
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