package de.tum.cit.aet.pse.store;

/**
 * TASK 3: Reduce coupling - IMPORT CHANGES
 * 
 * REMOVED imports:
 *   - import de.tum.cit.aet.pse.ecommerce.AdvertisementController;
 *   - import de.tum.cit.aet.pse.ecommerce.OrderController;
 *   - import de.tum.cit.aet.pse.ecommerce.ShippingController;
 * 
 * ADDED import:
 *   - import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;
 * 
 * WHY: Cinema had the MOST dependencies (3 controllers). Now it only
 * depends on the facade - a significant reduction in coupling!
 */
import de.tum.cit.aet.pse.ecommerce.ECommerceFacade;
import de.tum.cit.aet.pse.ecommerce.Order;

// TODO 4 remove all associations to the different controllers in all classes of the package store and use the facade
// instead.
public class Cinema {

	private static int count = 1;
	private final String address;
	private final String name;
	private final int id;

	/**
	 * TASK 3: FIELD CHANGES
	 * 
	 * REMOVED fields: - private final OrderController orderController; - private
	 * final ShippingController shippingController; - private final
	 * AdvertisementController advertisementController;
	 * 
	 * ADDED field: - private final ECommerceFacade eCommerceFacade;
	 * 
	 * WHY: Reduced from 3 controller references to 1 facade reference!
	 */
	private final ECommerceFacade eCommerceFacade;

	/**
	 * TASK 3: CONSTRUCTOR CHANGES
	 * 
	 * REMOVED: - this.orderController = new OrderController(); -
	 * this.shippingController = new ShippingController(); -
	 * this.advertisementController = new AdvertisementController();
	 * 
	 * ADDED: - this.eCommerceFacade = new ECommerceFacade();
	 */
	public Cinema(String address, String name) {
		this.address = address;
		this.name = name;
		this.id = generateCinemaId();
		this.eCommerceFacade = new ECommerceFacade();
	}

	public void startLiveStream(int ageRestriction) {
		System.out.println("Let's watch some ads at the beginning.");
		advertise(ageRestriction);
		System.out.println("The film starts. Your cinema " + name + " hopes you enjoy the movie.");
	}

	public void stopLiveStream(int ageRestriction) {
		System.out.println("Let's watch some more ads. ");
		advertise(ageRestriction);
		System.out.println("Have a nice evening!");
		System.out.println("-----------------------------------------------------------------------------------------");
	}

	/**
	 * TASK 3: METHOD CHANGE
	 * 
	 * BEFORE: advertisementController.playAdvertisement(ageRestriction); AFTER:
	 * eCommerceFacade.playAdvertisement(ageRestriction);
	 * 
	 * WHY: Now using facade instead of direct controller access.
	 */
	public void advertise(int ageRestriction) {
		eCommerceFacade.playAdvertisement(ageRestriction);
	}

	/**
	 * TASK 3: METHOD IMPLEMENTATION CHANGES
	 * 
	 * BEFORE (using multiple controllers): Order order =
	 * orderController.retrieveLatestOrder(id); orderController.processOrder(order);
	 * order.setShipping(shippingController.createShipping(shippingAddress));
	 * shippingController.shipOrder(order);
	 * 
	 * AFTER (using facade): Order order = eCommerceFacade.retrieveLatestOrder(id);
	 * eCommerceFacade.processOrder(order); eCommerceFacade.shipOrder(order,
	 * shippingAddress);
	 * 
	 * WHY: Same benefits as BookStore - simpler code, hidden complexity.
	 */
	public void deliverPopcorn(String shippingAddress) {
		Order order = eCommerceFacade.retrieveLatestOrder(id);
		eCommerceFacade.processOrder(order);
		eCommerceFacade.shipOrder(order, shippingAddress);
	}

	@Override
	public String toString() {
		return "Cinema " + name + ", located at " + address;
	}

	private static int generateCinemaId() {
		count *= 2;
		return count;
	}

}
