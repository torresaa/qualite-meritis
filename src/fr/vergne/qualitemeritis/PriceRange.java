package fr.vergne.qualitemeritis;

public class PriceRange {

	private final Price priceMin;
	private final Price priceMax;

	public PriceRange(Price priceMin, Price priceMax) {
		this.priceMin = priceMin;
		this.priceMax = priceMax;
	}

	public boolean includes(Price price) {
		if (this.priceMin.currency() == price.currency()) {
			return this.priceMin.value() <= price.value()//
					&& this.priceMax.value() >= price.value();
		} else {
			return false;
		}
	}
}
