package de.tum.cit.aet.pse.ecommerce;

/**
 * TASK 1: Introduce the Facade This class was ADDED as a new file to implement
 * the Facade Pattern.
 * 
 * WHY: The Facade Pattern reduces coupling by providing a simplified interface
 * to a complex subsystem. Instead of clients (BookStore, Cinema) directly
 * interacting with multiple controllers, they now only interact with this
 * facade.
 * 
 * BENEFIT: Clients no longer have access to methods they shouldn't use (e.g.,
 * commissionExternalParcelService in ShippingController).
 */
public class ECommerceFacade {

    // TASK 2: The facade holds references to all subsystem controllers
    // These are PRIVATE - clients cannot access them directly
    private OrderController orderController;
    private AdvertisementController advertisementController;
    private ShippingController shippingController;

    /**
     * Constructor initializes all controllers. WHY: The facade is responsible for
     * creating and managing the subsystem objects. Clients don't need to know about
     * these internal controllers.
     */
    public ECommerceFacade() {
        this.orderController = new OrderController();
        this.advertisementController = new AdvertisementController();
        this.shippingController = new ShippingController();
    }

    /**
     * TASK 2: Delegate public methods from OrderController WHY: We expose only the
     * methods that clients need. The facade delegates the actual work to the
     * OrderController.
     */
    public void processOrder(Order order) {
        orderController.processOrder(order);
    }

    // Overloaded method for processing with phone notification
    public void processOrder(Order order, String phoneNumber) {
        orderController.processOrder(order, phoneNumber);
    }

    // Retrieves the latest order for a given store ID
    public Order retrieveLatestOrder(int id) {
        return orderController.retrieveLatestOrder(id);
    }

    /**
     * TASK 2: Delegate public method from AdvertisementController WHY: Cinema needs
     * to play ads, so we expose this functionality.
     */
    public void playAdvertisement(int ageRestriction) {
        advertisementController.playAdvertisement(ageRestriction);
    }

    /**
     * TASK 2 (shipOrder): This method COMBINES multiple controller operations.
     * 
     * WHY: Previously, clients had to: 1. Call
     * shippingController.createShipping(address) 2. Call
     * order.setShipping(shipping) 3. Call shippingController.shipOrder(order)
     * 
     * NOW: The facade handles this complexity internally. Clients just call
     * shipOrder(order, address) - much simpler!
     * 
     * NOTE: We do NOT expose createShipping() or commissionExternalParcelService()
     * because clients don't need direct access to these internal operations.
     */
    public void shipOrder(Order order, String shippingAddress) {
        // Step 1: Create shipping object from address
        // Step 2: Set the shipping on the order
        order.setShipping(shippingController.createShipping(shippingAddress));
        // Step 3: Actually ship the order
        shippingController.shipOrder(order);
    }

}
