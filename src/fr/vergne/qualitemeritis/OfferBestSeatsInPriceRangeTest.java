package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

class OfferBestSeatsInPriceRangeTest {

	@Test
	void testReturnsOnlyAvailableSeatWithExactlyMatchingPriceRange() {
		// GIVEN
		Price price = new Price();
		Seat seat = new Seat(price);
		SuggestionSystem system = new SuggestionSystem(seat);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(Arrays.asList(seat), bestSeats);
	}

	static class Price {
	}

	static class Seat {

		public Seat(Price price) {
		}

		public Object price() {
			return null;
		}
	}

	static class PriceRange {

		public PriceRange(Price price, Price price2) {
		}

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
