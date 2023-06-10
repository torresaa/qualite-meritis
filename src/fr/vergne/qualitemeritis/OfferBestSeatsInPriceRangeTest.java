package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(allSeats, bestSeats);
	}

	static Stream<Object[]> seatIndexAndCount() {
		// The index is one-based to offer a better report readability
		return seatsCount().flatMap(count -> {
			return Stream.of(//
					Arrays.asList(1, count), // first seat
					Arrays.asList((count + 1) / 2, count), // middle seat
					Arrays.asList(count, count)// last seat
			);
		})//
				.distinct()// Remove redundant cases
				.map(List::toArray); // As Object[] to be compatible with parameterized tests
	}

	@ParameterizedTest(name = "seat {0} in {1}")
	@MethodSource("seatIndexAndCount")
	void testReturnsOnlySeatMatchingPriceRange(int seatIndex, int seatsCount) {
		// GIVEN
		Supplier<Price> priceSupplier = createIncrementingPricesPer(1);
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(priceSupplier.get())).toList();
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked());
		Seat targetSeat = allSeats.get(seatIndex - 1);
		Price targetPrice = targetSeat.price();
		PriceRange priceRange = new PriceRange(targetPrice, targetPrice);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(Arrays.asList(targetSeat), bestSeats);
	}

	@ParameterizedTest(name = "seat {0} in {1}")
	@MethodSource("seatIndexAndCount")
	void testReturnsOnlySeatWithinPriceRange(int seatIndex, int seatsCount) {
		// GIVEN
		Supplier<Price> priceSupplier = createIncrementingPricesPer(10);
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(priceSupplier.get())).toList();
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked());
		Seat targetSeat = allSeats.get(seatIndex - 1);
		Price targetPrice = targetSeat.price();
		PriceRange priceRange = new PriceRange(targetPrice.minus(1), targetPrice.plus(1));

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(Arrays.asList(targetSeat), bestSeats);
	}

	@ParameterizedTest(name = "seat {0} in {1}")
	@MethodSource("seatIndexAndCount")
	void testReturnsAllButSingleBookedSeat(int seatIndex, int seatsCount) {
		// GIVEN
		Price price = Price.euros(10);
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();
		List<Seat> freeSeats = new ArrayList<>(allSeats);
		Seat bookedSeat = freeSeats.remove(seatIndex - 1);
		Predicate<Seat> freeSeatPredicate = seat -> !bookedSeat.equals(seat);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(freeSeats, bestSeats);
	}

	@ParameterizedTest(name = "{0} seats")
	@MethodSource("seatsCount")
	void testReturnsNoSeatAssumingExactlyMatchingPriceRangeButAllBooked(int seatsCount) {
		// GIVEN
		Price price = Price.euros(5);
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();
		Predicate<Seat> freeSeatPredicate = seat -> false;
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(emptyList(), bestSeats);
	}

	@Test
	void testReturnsAdjacentSeatOfSinglePartyMemberFirst() {
		// GIVEN
		Price price = Price.euros(5);
		Seat seat1 = new Seat(price);
		Seat seat2 = new Seat(price);
		Seat seat3 = new Seat(price);
		List<Seat> allSeats = Arrays.asList(seat1, seat2, seat3);
		Seat partySeat = seat1;
		List<Seat> party = Arrays.asList(partySeat);
		Predicate<Seat> freeSeatPredicate = seat -> !party.contains(seat);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEquals(Arrays.asList(seat2, seat3), bestSeats);
	}

	private static Supplier<Price> createIncrementingPricesPer(int increment) {
		int[] nextValue = { 0 };
		return () -> Price.euros(nextValue[0] += increment);
	}

	private static Predicate<Seat> nothingBooked() {
		return seat -> true;
	}

	private static List<Seat> noParty() {
		return emptyList();
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

		private final Price priceMin;
		private final Price priceMax;

		public PriceRange(Price priceMin, Price priceMax) {
			this.priceMin = priceMin;
			this.priceMax = priceMax;
		}

		public boolean includes(Price price) {
			if (this.priceMin.currency == price.currency) {
				return this.priceMin.value <= price.value//
						&& this.priceMax.value >= price.value;
			} else {
				return false;
			}
		}
	}

	static class SuggestionSystem {

		private final List<Seat> seats;
		private final Predicate<Seat> freeSeatPredicate;

		public SuggestionSystem(List<Seat> seats, Predicate<Seat> freeSeatPredicate) {
			this.seats = seats;
			this.freeSeatPredicate = freeSeatPredicate;
		}

		public Collection<Seat> offerBestSeatsIn(PriceRange priceRange, Collection<Seat> party) {
			List<Seat> satisfyingSeats = new LinkedList<>();
			for (Seat seat : seats) {
				if (freeSeatPredicate.test(seat) && priceRange.includes(seat.price())) {
					satisfyingSeats.add(seat);
				}
			}
			return satisfyingSeats;
		}
	}
}
