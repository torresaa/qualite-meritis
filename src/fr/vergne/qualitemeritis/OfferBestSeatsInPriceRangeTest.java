package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

class OfferBestSeatsInPriceRangeTest {

	@Test
	void testReturnsOnlyAvailableSeatWithExactlyMatchingPriceRange() {
		// GIVEN
		Price price = new Price();
		Seat seat = new Seat(price);
		List<Seat> allSeats = Arrays.asList(seat);
		SuggestionSystem system = new SuggestionSystem(allSeats);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(allSeats, bestSeats);
	}

	@Test
	void testReturnsOnlyAvailableTwoSeatsWithExactlyMatchingPriceRange() {
		// GIVEN
		Price price = new Price();
		Seat seat1 = new Seat(price);
		Seat seat2 = new Seat(price);
		List<Seat> allSeats = Arrays.asList(seat1, seat2);
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
