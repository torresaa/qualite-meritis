package fr.vergne.qualitemeritis;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class OfferBestSeatsInPriceRangeTest {

	static Stream<Integer> seatsCount() {
		return Stream.of(1, 2, 5, 10, 100);
	}

	@ParameterizedTest(name = "{0} seats")
	@MethodSource("seatsCount")
	void testReturnsAllSeatsAssumingExactlyMatchingPriceRange(int seatsCount) {
		// GIVEN
		Price price = new Price();
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();
		SuggestionSystem system = new SuggestionSystem(allSeats);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(allSeats, bestSeats);
	}

	@ParameterizedTest(name = "{0} seats")
	@MethodSource("seatsCount")
	void testReturnsOnlySeatMatchingPriceRange(int seatsCount) {
		// GIVEN
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(new Price())).toList();
		SuggestionSystem system = new SuggestionSystem(allSeats);
		Seat targetSeat = allSeats.get(0);
		Price targetPrice = targetSeat.price();
		PriceRange priceRange = new PriceRange(targetPrice, targetPrice);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(Arrays.asList(targetSeat), bestSeats);
	}

	static class Price {
	}

	static class Seat {

		private final Price price;

		public Seat(Price price) {
			this.price = price;
		}

		public Price price() {
			return price;
		}
	}

	static class PriceRange {

		private final Price price;

		public PriceRange(Price price, Price price2) {
			this.price = price;
		}

		public boolean includes(Price price) {
			return this.price.equals(price);
		}
	}

	static class SuggestionSystem {

		private final List<Seat> seats;

		public SuggestionSystem(List<Seat> seats) {
			this.seats = seats;
		}

		public Collection<Seat> offerBestSeatsIn(PriceRange priceRange) {
			List<Seat> satisfyingSeats = new LinkedList<>();
			for (Seat seat : seats) {
				if (priceRange.includes(seat.price())) {
					satisfyingSeats.add(seat);
				}
			}
			return satisfyingSeats;
		}
	}
}
