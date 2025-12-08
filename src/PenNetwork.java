import java.util.Optional;

/**
 * Enhanced interface for the PenNetwork component. Extends the kernel with
 * secondary (derived) operations.
 *
 * @author Sandeep
 */
public interface PenNetwork extends PenNetworkKernel {

    /**
     * Transfers stock from one shop to another if possible.
     *
     * @param fromId
     *            the shop sending stock
     * @param toId
     *            the shop receiving stock
     * @param sku
     *            the pen product
     * @param qty
     *            the quantity requested
     * @return true if transfer occurred, false otherwise
     * @requires hasShop(fromId) and hasShop(toId) and sku != null and qty > 0
     * @ensures if returned true then getStock(fromId, sku) = #getStock(fromId,
     *          sku) - qty and getStock(toId, sku) = #getStock(toId, sku) + qty
     */
    boolean requestTransfer(String fromId, String toId, PenSKU sku, int qty);

    /**
     * Finds the shop with the highest quantity of the given SKU.
     *
     * @param sku
     *            the pen product
     * @return an Optional shop ID, empty if no shop carries the SKU
     * @requires sku != null
     * @ensures findTopSupplier = [shop with max stock of sku]
     */
    Optional<String> findTopSupplier(PenSKU sku);

    /**
     * Computes a suggested transfer to meet a minimum stock requirement.
     *
     * @param needyShopId
     *            the shop that needs stock
     * @param sku
     *            the pen product
     * @param minTarget
     *            the desired minimum stock level
     * @return an Optional suggested transfer plan
     * @requires hasShop(needyShopId) and sku != null and minTarget >= 0
     * @ensures computeRestockPlan = [smallest transfer needed to reach
     *          minTarget, or empty if impossible]
     */
    Optional<TransferPlan> computeRestockPlan(String needyShopId, PenSKU sku,
            int minTarget);

    /**
     * Returns a formatted textual summary of all shops and their inventory.
     *
     * @return multi-line formatted inventory report
     * @ensures inventoryReport = [human-readable formatted output]
     */
    String inventoryReport();
}
