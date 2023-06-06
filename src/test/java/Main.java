import fr.bramsou.event.EventBus;

public class Main {

    public static void main(String[] args) {
        EventBus bus = new EventBus();
        bus.register(new EventHandler("Event Handler"));
        MessageEvent event = new MessageEvent("Example");
        bus.post(event);
        bus.post(new MessageEvent("Second Example"));
    }
}
