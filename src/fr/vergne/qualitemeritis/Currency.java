package fr.vergne.qualitemeritis;

public enum Currency {
	EURO("€");

	private final String unit;

	Currency(String unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return unit;
	}
}
