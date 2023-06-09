package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
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

		public Object price() {
			return null;
		}
	}

	static class PriceRange {

		public boolean includes(Object price) {
			return true;
		}
	}

	static class SuggestionSystem {

		private final Seat seat;

		public SuggestionSystem(Seat seat) {
			this.seat = seat;
		}

		public Collection<Seat> offerBestSeatsIn(PriceRange priceRange) {
			if (priceRange.includes(seat.price())) {
				return Arrays.asList(seat);
			} else {
				return emptyList();
			}
		}
	}
}
