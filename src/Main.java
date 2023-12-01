import Controller.ControllerView;
import View.WindowClient;
public class Main {
    public static void main(String[] args) {
        WindowClient a = new WindowClient();
        ControllerView mainWindowController = new ControllerView(a);
        a.setController(mainWindowController);






    }
}