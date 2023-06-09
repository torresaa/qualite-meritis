package fr.vergne.qualitemeritis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class ProjectTest {

	@Test
	void test() {
		Seat seat = new Seat();
		SuggestionSystem system = new SuggestionSystem(seat);
		PriceRange priceRange = new PriceRange();
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);
		assertEquals(Arrays.asList(seat), bestSeats);
	}

	static class Seat {
	}

	static class PriceRange {
	}

	static class SuggestionSystem {

		private final Seat seat;

		public SuggestionSystem(Seat seat) {
			this.seat = seat;
		}

		public Collection<Seat> offerBestSeatsIn(PriceRange priceRange) {
			return Arrays.asList(seat);
		}
	}
}
