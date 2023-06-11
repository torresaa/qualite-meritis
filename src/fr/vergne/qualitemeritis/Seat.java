package fr.vergne.qualitemeritis;

public class Seat {

	private final Price price;

	public Seat(Price price) {
		this.price = price;
	}

	public Price price() {
		return price;
	}

	@Override
	public String toString() {
		return "Seat[" + hashCode() % 1000 + "]@" + price;
	}
}
