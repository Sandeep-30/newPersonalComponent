public final class PenNetworkDemo1 {

    public static void main(String[] args) {
        PenNetwork net = new PenNetwork1L();

        PenSKU blue = new PenSKU("Uni-ball", "Signo", "0.5mm Gel", "Blue");

        net.add("Shop0", blue.toString(), 10);
        net.add("Shop1", blue.toString(), 4);

        boolean ok = net.requestTransfer("Shop0", "Shop1", blue, 6);

        System.out.println("Transfer successful? " + ok);
        System.out.println(net.inventoryReport());
    }
}
