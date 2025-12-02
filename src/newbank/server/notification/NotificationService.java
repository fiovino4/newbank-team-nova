package newbank.server.notification;

import newbank.server.CustomerID;
import java.util.*;

/**
 * Manages the creation and retrieval of notifications for customers in the NewBank system.
 *
 * This service stores notifications in-memory, grouped by the recipient's username.
 * Each notification is assigned a sequential identifier when it is created.
 *
 * Main responsibilities include:
 *  - Creating new notifications for customers
 *  - Assigning unique incremental notification IDs
 *  - Retrieving all notifications belonging to a specific customer
 *
 * The class is thread-safe. All public operations are synchronized to avoid race
 * conditions during notification creation or retrieval.
 *
 * This implementation uses a simple in-memory map and is intended for version 1.0.
 * A future version may persist notifications in a database or external storage.
 */
public class NotificationService {

    private final Map<String, List<Notification>> notifications = new HashMap<>();
    private int nextId = 1;

    /**
     * Creates a new notification for the specified customer.
     *
     * A unique notification ID is assigned automatically. The notification is then
     * added to the internal list of notifications belonging to the recipient.
     *
     * Parameters:
     *  recipient - the customer who should receive the notification
     *  message   - the text message describing the event
     *
     * Returns:
     *  the newly created Notification object
     *
     * The method is synchronized to ensure thread-safe ID generation and storage.
     */
    public synchronized Notification createNotification(CustomerID recipient, String message) {
        Notification notification = new Notification(nextId++, recipient, message);

        notifications.computeIfAbsent(recipient.getKey(), k -> new ArrayList<>()).add(notification);

        return notification;
    }

    /**
     * Returns all notifications belonging to the specified customer.
     *
     * If the customer has no notifications, an empty list is returned.
     * The returned list is the internal list stored by the service, so it should
     * not be modified directly by callers.
     *
     * Parameters:
     *  recipient - the customer whose notifications should be retrieved
     *
     * Returns:
     *  a list of Notification objects for the customer, or an empty list if none exist
     *
     * The method is synchronized to ensure safe access to the internal data structure.
     */
    public synchronized List<Notification> getNotifications(CustomerID recipient) {
        List<Notification> list = notifications.get(recipient.getKey());
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
    }
}
