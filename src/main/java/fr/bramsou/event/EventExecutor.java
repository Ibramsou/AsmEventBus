package fr.bramsou.event;

public class EventExecutor {

    private final EventPriority priority;
    private final EventPost post;

    public EventExecutor(EventPriority priority, EventPost post) {
        this.priority = priority;
        this.post = post;
    }

    public void execute(Object event) {
        this.post.post(event);
    }

    public EventPriority getPriority() {
        return priority;
    }
}
