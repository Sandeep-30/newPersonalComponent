/**
 * PenDistribution represents an enhanced interface layered on top of the kernel
 * functionality. It includes higher-level analytics and convenience methods for
 * managing and analyzing shop inventories.
 *
 * @author Sandeep Venigandla
 * @version 2025.10.25
 */
public interface PenDistribution extends PenDistributionKernel {

    /**
     * Computes the total quantity of all pens across all shops.
     *
     * @return total inventory across all shops
     * @ensures totalInventory = Σ(shop, pen) of quantities in this
     */
    int totalInventory();

    /**
     * Finds which pen type currently has the highest total stock across all
     * shops.
     *
     * @return name of the pen type with maximum stock
     * @requires this is not empty
     * @ensures returned pen p has stock(p) >= stock(q) for all q
     */
    String topSellingPen();

    /**
     * Returns the name of the shop that currently has the lowest total stock of
     * all pens.
     *
     * @return shop identifier with smallest total inventory
     * @requires this is not empty
     * @ensures returned shop s has total(s) <= total(t) for all t
     */
    String lowestStockShop();

    /**
     * Balances stock between shops such that total differences are minimized.
     *
     * @ensures all shops have approximately equal total stock
     */
    void balanceStock();
}
