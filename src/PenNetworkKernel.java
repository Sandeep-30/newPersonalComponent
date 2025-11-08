/**
 * PenDistributionKernel defines the minimal kernel functionality required to
 * represent and manipulate a collection of shops, each maintaining inventory
 * counts for different pen types.
 *
 * @author Sandeep Venigandla
 * @version 2025.10.25
 */
public interface PenDistributionKernel extends Standard<PenDistribution> {

    /**
     * Adds (or restocks) a given quantity of a specific pen type to a given
     * shop.
     *
     * @param shop
     *            the shop identifier
     * @param pen
     *            the pen SKU or model name
     * @param quantity
     *            the quantity to add (must be positive)
     * @requires quantity > 0
     * @ensures this = #this with quantity added to (shop, pen)
     */
    void add(String shop, String pen, int quantity);

    /**
     * Transfers a quantity of a pen type from one shop to another.
     *
     * @param fromShop
     *            the shop transferring out inventory
     * @param toShop
     *            the destination shop receiving inventory
     * @param pen
     *            the pen type to transfer
     * @param quantity
     *            the quantity to transfer (must not exceed available stock)
     * @requires quantity > 0 and stock(fromShop, pen) >= quantity
     * @ensures this = #this with quantity moved from fromShop to toShop
     */
    void transfer(String fromShop, String toShop, String pen, int quantity);

    /**
     * Reports the current stock quantity for a given shop and pen.
     *
     * @param shop
     *            the shop identifier
     * @param pen
     *            the pen SKU or model name
     * @return the number of pens currently available
     * @requires shop and pen are defined in this
     * @ensures this = #this
     */
    int getStock(String shop, String pen);

    /**
     * Returns the total number of shops currently tracked.
     *
     * @return the number of shops represented in this
     * @ensures getShopCount = |shops|
     */
    int getShopCount();
}