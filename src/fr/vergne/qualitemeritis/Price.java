package fr.vergne.qualitemeritis;

public class Price {
	private final int value;
	private final Currency currency;

	public Price(int value, Currency currency) {
		this.value = value;
		this.currency = currency;
	}

	public int value() {
		return value;
	}

	public Currency currency() {
		return currency;
	}

	public Price minus(int delta) {
		return new Price(value - delta, currency);
	}

	public Price plus(int delta) {
		return new Price(value + delta, currency);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (this instanceof Price that) {
			return this.value == that.value//
					&& this.currency == that.currency;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return value * currency.hashCode();
	}

	@Override
	public String toString() {
		return "" + value + currency;
	}

	public static Price euros(int value) {
		return new Price(value, Currency.EURO);
	}
}
