package newbank.server.model;

import java.time.LocalDateTime;

/**
 * Represents a notification sent to a customer in the NewBank system.
 *
 * A notification is created when an event of interest occurs, such as a loan
 * request being made to a lender. Each notification contains:
 *  - A unique identifier
 *  - The recipient customer
 *  - A message describing the event
 *  - A creation timestamp
 *  - A flag indicating whether the notification has been read
 *
 * Notifications are immutable except for the read status, which may be
 * updated by calling markAsRead.
 */
public class Notification {

    private final int id;
    private final CustomerID recipient;
    private final String message;
    private boolean read;
    private final LocalDateTime createdAt;

    /**
     * Creates a new notification for a specific customer.
     *
     * Parameters:
     *  id        - the unique identifier for the notification
     *  recipient - the customer who receives the notification
     *  message   - the message text describing the event
     *
     * The notification is timestamped at creation time and is marked unread
     * by default.
     */
    public Notification(int id, CustomerID recipient, String message){

        this.id = id;
        this.recipient = recipient;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public int getId() {
        return id;
    }

    public CustomerID getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markAsRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "[" + createdAt + "] " + message + (read ? " (read)" : " (unread)");
    }

}
