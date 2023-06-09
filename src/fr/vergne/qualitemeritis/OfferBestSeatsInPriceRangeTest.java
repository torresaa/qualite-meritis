package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

class OfferBestSeatsInPriceRangeTest {

	@Test
	void testReturnsOnlyAvailableSeatWithExactlyMatchingPriceRange() {
		int seatsCount = 1;
		Price price = new Price();
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();

		testReturnsAllSeatsAssumingExactlyMatchingPriceRange(price, allSeats);
	}

	@Test
	void testReturnsOnlyAvailableTwoSeatsWithExactlyMatchingPriceRange() {
		int seatsCount = 2;
		Price price = new Price();
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();

		testReturnsAllSeatsAssumingExactlyMatchingPriceRange(price, allSeats);
	}

	private void testReturnsAllSeatsAssumingExactlyMatchingPriceRange(Price price, List<Seat> allSeats) {
		// GIVEN
		SuggestionSystem system = new SuggestionSystem(allSeats);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(allSeats, bestSeats);
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

		private final List<Seat> seats;

		public SuggestionSystem(List<Seat> seats) {
			this.seats = seats;
		}

		public Collection<Seat> offerBestSeatsIn(PriceRange priceRange) {
			for (Seat seat : seats) {
				if (priceRange.includes(seat.price())) {
					return seats;
				}
			}
			return emptyList();
		}
	}
}
