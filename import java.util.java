import java.util.Set;

/**
 * Kernel interface for the PenNetwork component.
 *
 * @author Sandeep
 */
public interface PenNetworkKernel {

    /**
     * Adds a shop to the network if it does not already exist.
     *
     * @param id
     *            the unique shop identifier
     * @updates this
     * @requires id is not null
     * @ensures hasShop(id)
     */
    void addShop(String id);

    /**
     * Reports whether a shop with the given id exists in this network.
     *
     * @param id
     *            the shop identifier
     * @return true if the shop exists, false otherwise
     * @requires id is not null
     * @ensures hasShop = (id is in this)
     */
    boolean hasShop(String id);

    /**
     * Returns the current stock of the given SKU at the given shop.
     *
     * @param shopId
     *            the name of the shop
     * @param sku
     *            the pen product
     * @return quantity of sku at shopId (0 if not present)
     * @requires hasShop(shopId) and sku is not null
     * @ensures getStock = [quantity of sku stored at shopId]
     */
    int getStock(String shopId, PenSKU sku);

    /**
     * Adds stock of a given SKU to the given shop.
     *
     * @param shopId
     *            the shop receiving stock
     * @param sku
     *            the pen product
     * @param qty
     *            the quantity to add
     * @updates this
     * @requires hasShop(shopId) and sku is not null and qty >= 0
     * @ensures getStock(shopId, sku) = #getStock(shopId, sku) + qty
     */
    void addStock(String shopId, PenSKU sku, int qty);

    /**
     * Attempts to remove stock of a given SKU from a shop.
     *
     * @param shopId
     *            the source shop
     * @param sku
     *            the pen product
     * @param qty
     *            the quantity requested
     * @return true if qty was available and removed, false otherwise
     * @updates this
     * @requires hasShop(shopId) and sku is not null and qty > 0
     * @ensures
     *  if returned true then getStock(shopId, sku) = #getStock(shopId, sku) - qty <br>
     *  else this = #this
     */
    boolean removeStock(String shopId, PenSKU sku, int qty);

    /**
     * Returns the set of shop identifiers currently in the network.
     *
     * @return a set of shop IDs
     * @ensures shopIDs = [all shop names in this]
     */
    Set<String> shopIDs();
}