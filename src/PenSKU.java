import java.util.Objects;

/**
 * Value object representing a pen SKU (brand + name + tip + color).
 *
 * @author Sandeep
 */
public final class PenSKU {

    private final String brand;
    private final String name;
    private final String tip; // e.g., "0.5mm Gel"
    private final String color; // e.g., "Blue"

    public PenSKU(String brand, String name, String tip, String color) {
        this.brand = Objects.requireNonNull(brand);
        this.name = Objects.requireNonNull(name);
        this.tip = Objects.requireNonNull(tip);
        this.color = Objects.requireNonNull(color);
    }

    public String brand() {
        return this.brand;
    }

    public String name() {
        return this.name;
    }

    public String tip() {
        return this.tip;
    }

    public String color() {
        return this.color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PenSKU))
            return false;
        PenSKU other = (PenSKU) o;
        return this.brand.equals(other.brand) && this.name.equals(other.name)
                && this.tip.equals(other.tip) && this.color.equals(other.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.brand, this.name, this.tip, this.color);
    }

    @Override
    public String toString() {
        return this.brand + " " + this.name + " " + this.tip + " (" + this.color + ")";
    }
}