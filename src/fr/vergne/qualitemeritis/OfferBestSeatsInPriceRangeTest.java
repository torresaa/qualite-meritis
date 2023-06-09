package fr.vergne.qualitemeritis;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
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
		Price price = Price.euros(5);
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();
		SuggestionSystem system = new SuggestionSystem(allSeats);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(allSeats, bestSeats);
	}

	static Stream<Object[]> seatsCountAndIndex() {
		// The index is one-based to offer a better report readability
		return seatsCount().flatMap(count -> {
			return Stream.of(//
					new Object[] { 1, count }, // first seat
					new Object[] { (count + 1) / 2, count }, // middle seat
					new Object[] { count, count }// last seat
			);
		});
	}

	@ParameterizedTest(name = "seat {0} in {1}")
	@MethodSource("seatsCountAndIndex")
	void testReturnsOnlySeatMatchingPriceRange(int seatIndex, int seatsCount) {
		// GIVEN
		Supplier<Price> priceSupplier = createPriceSupplier();
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(priceSupplier.get())).toList();
		SuggestionSystem system = new SuggestionSystem(allSeats);
		Seat targetSeat = allSeats.get(seatIndex - 1);
		Price targetPrice = targetSeat.price();
		PriceRange priceRange = new PriceRange(targetPrice, targetPrice);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange);

		// THEN
		assertEquals(Arrays.asList(targetSeat), bestSeats);
	}

	private static Supplier<Price> createPriceSupplier() {
		int[] nextValue = { 0 };
		return () -> Price.euros(nextValue[0]++);
	}

	enum Currency {
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

	static class Price {
		private final int value;
		private final Currency currency;

		public Price(int value, Currency currency) {
			this.value = value;
			this.currency = currency;
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
			if (this.price.currency == price.currency) {
				return this.price.value == price.value;
			} else {
				return false;
			}
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
