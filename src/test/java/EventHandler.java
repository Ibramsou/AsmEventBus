import fr.bramsou.event.EventPriority;
import fr.bramsou.event.Subscriber;

public class EventHandler {

    private final String name;

    public EventHandler(String name) {
        this.name = name;
    }

    @Subscriber(priority = EventPriority.MEDIUM)
    public void onMessage(MessageEvent event) {
        System.out.println("Message: " + event.getMessage() + " (name: " + this.name + ")");
        event.setMessage("Next Message");
    }

    @Subscriber(priority = EventPriority.HIGH)
    public void onFirstMessage(MessageEvent event) {
        System.out.println("EXAMPLE");
        event.setMessage("[My Prefix] " + event.getMessage());
    }
}
