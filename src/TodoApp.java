// TodoApp.java
public class TodoApp {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("사용법: java TodoApp <명령어> [내용]");
            System.out.println("명령어: list, add \"할 일\", done <번호>, delete <번호>");
            return;
        }

        // TaskManager(엔진)를 생성합니다.
        TaskManager manager = new TaskManager("tasks.txt");
        String command = args[0];

        // 명령어에 따라 엔진의 기능을 호출만 합니다.
        switch (command) {
            case "list" -> manager.listTasks();
            case "add" -> {
                if (args.length > 1) manager.addTask(args[1]);
                else System.out.println("추가할 할 일의 내용을 입력해주세요.");
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
