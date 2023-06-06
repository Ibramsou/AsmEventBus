package fr.bramsou.event;

/**
 * event priority
 * LOWEST = method will be called in last
 * HIGH = method is called in first
 */
public enum EventPriority {
    LOWEST,
    LOW,
    MEDIUM,
    HIGH,
    HIGHEST
}
