import java.util.*;

class OrderManager {
    private List<Order> orders = new ArrayList<>();

    public void placeOrder(int orderId, String customerName, double amount, String paymentMethod) {
        System.out.println("Placing order for: " + customerName);

        // Validate order
        if (amount <= 0) {
            System.out.println("Invalid order amount!");
            return;
        }

        // Calculate tax (magic number 0.15 for tax)
        double tax = amount * 0.15;
        double total = amount + tax;

        // Process payment (Feature Envy)
        if (paymentMethod.equals("CREDIT_CARD")) {
            System.out.println("Processing credit card payment...");
        } else if (paymentMethod.equals("PAYPAL")) {
            System.out.println("Processing PayPal payment...");
        } else {
            System.out.println("Invalid payment method!");
            return;
        }

        // Store order
        Order order = new Order(orderId, customerName, amount, tax, total, paymentMethod);
        orders.add(order);

        // Notify customer
        System.out.println("Order placed successfully for " + customerName);
        System.out.println("Sending email confirmation...");
    }

    public void printOrders() {
        for (Order order : orders) {
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Customer: " + order.getCustomerName());
            System.out.println("Amount: $" + order.getAmount());
            System.out.println("Tax: $" + order.getTax());
            System.out.println("Total: $" + order.getTotal());
            System.out.println("Payment Method: " + order.getPaymentMethod());
            System.out.println("--------------------------");
        }
    }
}

// Order class (Lazy Class with unnecessary constructor)
class Order {
    private int orderId;
    private String customerName;
    private double amount;
    private double tax;
    private double total;
    private String paymentMethod;

    public Order(int orderId, String customerName, double amount, double tax, double total, String paymentMethod) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
        this.tax = tax;
        this.total = total;
        this.paymentMethod = paymentMethod;
    }

    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public double getAmount() { return amount; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public String getPaymentMethod() { return paymentMethod; }
}

public class Main {
    public static void main(String[] args) {
        OrderManager orderManager = new OrderManager();
        orderManager.placeOrder(1, "Alice", 100, "CREDIT_CARD");
        orderManager.placeOrder(2, "Bob", 200, "PAYPAL");
        orderManager.printOrders();
    }
}

