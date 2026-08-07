package com.mentorhomeloans.domain.model

/**
 * Represents a push or in-app notification received by the customer.
 *
 * Notifications inform the customer about upcoming EMI due dates, payment
 * confirmations, statement availability, ticket updates, and other events.
 * Used in the Notifications screen.
 *
 * @property id           Unique notification identifier.
 * @property title        Notification title (short, action-oriented).
 * @property body         Full notification message body.
 * @property type         Type/category of notification.
 * @property isRead       True if the customer has viewed the notification.
 * @property receivedAt   Timestamp when notification was received (ISO-8601).
 * @property actionUrl    Optional deep-link URL to navigate on notification tap.
 * @property iconType     Icon variant to display alongside the notification.
 * @property metaData     Key-value metadata for contextual information.
 */
data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val isRead: Boolean,
    val receivedAt: String,
    val actionUrl: String?,
    val iconType: NotificationIconType,
    val metaData: Map<String, String> = emptyMap()
)

/**
 * Category of a notification for icon/colour selection and filtering.
 *
 * @property displayName Human-readable category name.
 */
enum class NotificationType(val displayName: String) {
    EMI_REMINDER("EMI Reminder"),
    PAYMENT_CONFIRMATION("Payment Confirmation"),
    PAYMENT_FAILED("Payment Failed"),
    STATEMENT_AVAILABLE("Statement Available"),
    TICKET_UPDATE("Ticket Update"),
    DOCUMENT_AVAILABLE("Document Available"),
    OVERDUE_ALERT("Overdue Alert"),
    GENERAL("General")
}

/**
 * Icon type mapped to Material Icons in the UI layer.
 *
 * The domain layer specifies semantic icon intent; the UI layer resolves
 * the actual icon from the design system.
 */
enum class NotificationIconType {
    PAYMENT,
    ALERT,
    DOCUMENT,
    SUPPORT,
    INFO
}
