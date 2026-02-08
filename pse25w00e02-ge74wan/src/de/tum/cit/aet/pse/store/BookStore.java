package de.tum.cit.aet.pse.store;

/**
 * TASK 3: Reduce coupling - IMPORT CHANGES
 * 
 * REMOVED imports:
 *   - import de.tum.cit.aet.pse.ecommerce.OrderController;
 *   - import de.tum.cit.aet.pse.ecommerce.ShippingController;
 * 
 * ADDED import:
 *   - import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;
 * 
 * WHY: BookStore no longer needs to know about individual controllers.
 * It only interacts with the simplified ECommerceFacade interface.
 */
import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;
import de.tum.cit.aet.pse.ecommerce.Order;

// TODO 4 remove all associations to the different controllers in all classes of the package store and use the facade
// instead.
public class BookStore {

    private static int count = 1;
    private final String address;
    private final String name;
    private final int id;

    /**
     * TASK 3: FIELD CHANGES
     * 
     * REMOVED fields: - private final OrderController orderController; - private
     * final ShippingController shippingController;
     * 
     * ADDED field: - private final ECommerceFacade eCommerceFacade;
     * 
     * WHY: Instead of maintaining references to multiple controllers, we now only
     * need ONE reference to the facade. This reduces coupling!
     */
    private final ECommerceFacade eCommerceFacade;

    /**
     * TASK 3: CONSTRUCTOR CHANGES
     * 
     * REMOVED: - this.orderController = new OrderController(); -
     * this.shippingController = new ShippingController();
     * 
     * ADDED: - this.eCommerceFacade = new ECommerceFacade();
     * 
     * WHY: We only instantiate the facade now. The facade internally creates and
     * manages all the controllers.
     */
    public BookStore(String address, String name) {
        this.address = address;
        this.name = name;
        this.id = generateBookStoreId();
        this.eCommerceFacade = new ECommerceFacade();
    }

    /**
     * TASK 3: METHOD IMPLEMENTATION CHANGES
     * 
     * BEFORE (using multiple controllers): Order order =
     * orderController.retrieveLatestOrder(id); orderController.processOrder(order,
     * phoneNumber);
     * order.setShipping(shippingController.createShipping(shippingAddress));
     * shippingController.shipOrder(order);
     * 
     * AFTER (using facade): Order order = eCommerceFacade.retrieveLatestOrder(id);
     * eCommerceFacade.processOrder(order, phoneNumber);
     * eCommerceFacade.shipOrder(order, shippingAddress);
     * 
     * WHY: 1. Code is simpler - fewer lines, clearer intent 2. No need to manually
     * manage shipping creation and assignment 3. BookStore doesn't need to know HOW
     * shipping works internally
     */
    public void acceptOrder(String shippingAddress, String phoneNumber) {
        System.out.println("Accepting shipping order.");
        Order order = eCommerceFacade.retrieveLatestOrder(id);
        eCommerceFacade.processOrder(order, phoneNumber);
        eCommerceFacade.shipOrder(order, shippingAddress);
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Book store " + name + ", located at " + address;
    }

    private static int generateBookStoreId() {
        count += 2;
        return count;
    }

}
