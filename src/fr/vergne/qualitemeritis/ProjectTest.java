package fr.vergne.qualitemeritis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class ProjectTest {

	@Test
	void test() {
		SuggestionSystem system = new SuggestionSystem();
		PriceRange priceRange = new PriceRange();
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);
	}

	static class Seat {
	}

	static class PriceRange {
	}

	static class SuggestionSystem {

		public Collection<Seat> offerBestSeatsIn(PriceRange priceRange) {
			// TODO
			return null;
		}
	}
}
