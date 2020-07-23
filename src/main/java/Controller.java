import javafx.scene.control.Alert;

public class Controller {
    private static Controller controller = null;

    private Controller() {
    }

    public static Controller getController() {
        if(controller == null)
            controller = new Controller();
        return controller;
    }

    public synchronized String getHello() throws InterruptedException {
        Thread.sleep(5000);
        return "Hello";
    }

    public int add(int a, int b, Object addLock) throws InterruptedException {
        synchronized (addLock) {
            Thread.sleep(5000);
            return a + b;
        }
    }

    public int sub(int a, int b, Object subLock) throws InterruptedException {
        synchronized (subLock) {
            Thread.sleep(5000);
            return a - b;
        }
    }
}
