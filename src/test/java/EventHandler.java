import fr.bramsou.event.Subscriber;

public class EventHandler {

    private final String name;

    public EventHandler(String name) {
        this.name = name;
    }

    @Subscriber
    public void onMessage(MessageEvent event) {
        System.out.println("Message: " + event.getMessage() + " (name: " + this.name + ")");
        event.setMessage("Next Message");
    }
}
