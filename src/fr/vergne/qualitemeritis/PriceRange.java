package fr.vergne.qualitemeritis;

public class PriceRange {

	private final Price priceMin;
	private final Price priceMax;

	public PriceRange(Price priceMin, Price priceMax) {
		this.priceMin = priceMin;
		this.priceMax = priceMax;
	}

	public boolean includes(Price price) {
		return this.priceMin.value() <= price.value()//
				&& this.priceMax.value() >= price.value();
	}
}
