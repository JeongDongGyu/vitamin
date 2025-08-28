# vitamin

강사님에게 들은 거 간단히 정리
1. 추상 클래스 적용해 보기
2. 인터페이스로 강제적인 기능 넣고 implements로 구현
3. 기능 종류가 다른 경우(기본 기능 만들고 상속을 통해 다양한 기능 구현)
4. 다양성 면에선 같은 메서드 호출로도 다른 계산이 되도록 유도

안녕하세요. 요청하신 내용을 바탕으로 기존 코드를 객체 지향 원칙(추상 클래스, 인터페이스, 상속, 다형성)에 맞게 개선하는 가장 빠른 방법을 안내해 드리겠습니다.

핵심은 **`Task`를 여러 종류의 할 일을 표현할 수 있도록 확장**하고, **`TaskManager`의 역할을 명확하게 정의**하는 것입니다.

가장 빠르게 적용할 수 있는 방법은 다음과 같습니다.

1.  **`Task`를 `abstract class` (추상 클래스)로 변경**하여 모든 할 일의 기본 뼈대를 정의합니다. (상속)
2.  기존의 간단한 할 일을 **`SimpleTask` 클래스로 만들어 `Task`를 상속**받게 합니다.
3.  다양한 종류의 할 일을 위해 **`DatedTask` (날짜가 지정된 할 일) 클래스를 추가**합니다. (기능 확장)
4.  `listTasks` 메서드에서 **`Task`의 종류에 상관없이 동일한 방법으로 정보를 출력**하도록 하여 다형성을 구현합니다.
5.  **`ITaskManager` 인터페이스를 정의**하여 `TaskManager`가 구현해야 할 기능들을 명시적으로 강제합니다.

아래 순서대로 파일을 수정하거나 새로 만들면 개념들을 가장 빠르게 적용해 보실 수 있습니다.

-----

### 📝 1단계: Task 추상 클래스 및 하위 클래스 만들기

먼저, `TaskManager.java` 내부에 있던 `private record Task(...)` 부분을 삭제하고, 아래의 3개 클래스를 별도의 파일(`Task.java`, `SimpleTask.java`, `DatedTask.java`)로 생성합니다.

#### **Task.java (신규 파일)**

모든 '할 일'이 공통으로 가질 속성(설명, 완료 여부)과 기능(완료 처리)을 정의하는 뼈대입니다. `toFileString()`이 `abstract`이므로, 이 클래스를 상속받는 자식 클래스는 반드시 이 메서드를 자신의 방식대로 구현해야 합니다.

```java
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
```

#### **SimpleTask.java (신규 파일)**

기존의 할 일 기능을 그대로 구현한, 가장 기본적인 할 일 클래스입니다.

```java
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
```

#### **DatedTask.java (신규 파일)**

'마감일'이라는 새로운 기능을 추가한 할 일 클래스입니다. `toFileString()`을 오버라이딩하여 마감일 정보까지 함께 출력하도록 재정의했습니다. 이것이 바로 **다형성**의 예시입니다. 같은 `toFileString()` 호출에도 객체의 실제 종류(`SimpleTask` 또는 `DatedTask`)에 따라 다른 결과가 나옵니다.

```java
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
```

-----

### ⚙️ 2단계: TaskManager 수정하기

이제 `TaskManager`가 새로운 `Task` 클래스 구조를 사용하도록 수정합니다. `record`를 지우고, `List<Task>`를 사용하도록 변경하며, 할 일을 추가하고 불러오는 로직을 수정합니다.

#### **TaskManager.java (수정)**

`//` 주석으로 수정된 부분을 표시했습니다.

```java
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
```

-----

### 🔌 3단계: 인터페이스 정의하기

`TaskManager`의 공개된 기능들을 모아 인터페이스로 만듭니다. 이를 통해 `TaskManager`의 기능이 무엇인지 명확히 하고, 향후 다른 종류의 `TaskManager`를 만들 때도 동일한 규격을 따르도록 강제할 수 있습니다.

#### **ITaskManager.java (신규 파일)**

```java
// ITaskManager.java
import java.time.LocalDate;

public interface ITaskManager {
    void addTask(String description);
    void addDatedTask(String description, LocalDate dueDate);
    void listTasks();
    void completeTask(int taskNumber);
    void deleteTask(int taskNumber);
}
```

**※** 이제 `TaskManager.java` 파일 상단으로 돌아가 `public class TaskManager`를 `public class TaskManager implements ITaskManager`로 수정해주세요.

-----

### 🚀 4단계: TodoApp 수정하기

마지막으로 `main` 메서드가 있는 `TodoApp.java`를 수정하여 새로운 `DatedTask`를 추가하는 명령어를 처리하고, `TaskManager`를 인터페이스 타입으로 사용하도록 변경합니다.

#### **TodoApp.java (수정)**

```java
// TodoApp.java
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TodoApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("사용법: java TodoApp <명령어> [내용]");
            System.out.println("명령어: list, add \"할 일\" [YYYY-MM-DD], done <번호>, delete <번호>");
            return;
        }

        // TaskManager를 인터페이스(ITaskManager) 타입으로 선언 (의존성 감소)
        ITaskManager manager = new TaskManager("tasks.txt");
        String command = args[0];

        switch (command) {
            case "list" -> manager.listTasks();
            case "add" -> {
                if (args.length == 2) {
                    manager.addTask(args[1]); // 일반 할 일 추가
                } else if (args.length == 3) {
                    try {
                        // 날짜가 있으면 DatedTask로 추가
                        LocalDate dueDate = LocalDate.parse(args[2]);
                        manager.addDatedTask(args[1], dueDate);
                    } catch (DateTimeParseException e) {
                        System.out.println("날짜 형식이 잘못되었습니다. YYYY-MM-DD 형식으로 입력해주세요.");
                    }
                } else {
                    System.out.println("추가할 할 일의 내용을 입력해주세요.");
                }
            }
            case "done" -> {
                if (args.length > 1) manager.completeTask(Integer.parseInt(args[1]));
                else System.out.println("완료할 할 일의 번호를 입력해주세요.");
            }
            case "delete" -> {
                if (args.length > 1) manager.deleteTask(Integer.parseInt(args[1]));
                else System.out.println("삭제할 할 일의 번호를 입력해주세요.");
            }
            default -> System.out.println("알 수 없는 명령어입니다.");
        }
    }
}
```

이제 코드를 다시 컴파일하고 실행하면, `add "장보기" 2025-08-30`과 같은 명령어로 기한이 있는 할 일을 추가하고 관리할 수 있습니다. 이 과정에서 요청하신 모든 객체 지향 개념이 적용되었습니다.
