package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static java.util.Collections.shuffle;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, uncaringDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, uncaringDistance());
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
		BiFunction<Seat, Seat, Integer> seatsDistancer = sequentialDistanceOver(allSeats);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEquals(Arrays.asList(seat2, seat3), bestSeats);
	}

	@Test
	void testReturnsAdjacentSeatOfSinglePartyMemberFirst2() {
		// GIVEN
		Price price = Price.euros(5);
		Seat seat1 = new Seat(price);
		Seat seat2 = new Seat(price);
		Seat seat3 = new Seat(price);
		List<Seat> allSeats = Arrays.asList(seat1, seat2, seat3);
		Seat partySeat = seat3;
		List<Seat> party = Arrays.asList(partySeat);
		Predicate<Seat> freeSeatPredicate = seat -> !party.contains(seat);
		BiFunction<Seat, Seat, Integer> seatsDistancer = sequentialDistanceOver(allSeats);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEquals(Arrays.asList(seat2, seat1), bestSeats);
	}

	@Test
	void testReturnsAdjacentSeatOfSinglePartyMemberFirst3() {
		// GIVEN
		Price price = Price.euros(5);
		Seat seat1 = new Seat(price);
		Seat seat2 = new Seat(price);
		Seat seat3 = new Seat(price);
		Seat seat4 = new Seat(price);
		Seat seat5 = new Seat(price);
		List<Seat> allSeats = Arrays.asList(seat1, seat2, seat3, seat4, seat5);
		Seat partySeat = seat3;
		List<Seat> party = Arrays.asList(partySeat);
		Predicate<Seat> freeSeatPredicate = seat -> !party.contains(seat);
		BiFunction<Seat, Seat, Integer> seatsDistancer = sequentialDistanceOver(allSeats);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEqualsUnordered(Arrays.asList(seat2, seat4), bestSeats.subList(0, 2));
	}

	static Stream<Integer> seatsCountForAdjacency() {
		return Stream.of(3, 5, 10, 100);
	}

	@ParameterizedTest(name = "{0} seats")
	@MethodSource("seatsCountForAdjacency")
	void testReturnsFirstAdjacentSeatsOfSinglePartyMember(int seatsCount) {
		// GIVEN
		Price price = Price.euros(5);
		List<Seat> allSeats = range(0, seatsCount).mapToObj(i -> new Seat(price)).toList();

		List<Seat> adjacentSeats = new ArrayList<>(allSeats);
		shuffle(adjacentSeats, new Random(0));
		Seat partySeat = adjacentSeats.remove(0);
		adjacentSeats = adjacentSeats.subList(0, adjacentSeats.size() / 2);

		List<Seat> party = Arrays.asList(partySeat);
		Predicate<Seat> freeSeatPredicate = seat -> !party.contains(seat);
		BiFunction<Seat, Seat, Integer> seatsDistancer = adjacencyDistance(partySeat, adjacentSeats);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEqualsUnordered(adjacentSeats, bestSeats.subList(0, adjacentSeats.size()));
	}

	private static Supplier<Price> createIncrementingPricesPer(int increment) {
		int[] nextValue = { 0 };
		return () -> Price.euros(nextValue[0] += increment);
	}

	private static Predicate<Seat> nothingBooked() {
		return seat -> true;
	}

	private static BiFunction<Seat, Seat, Integer> uncaringDistance() {
		return (s1, s2) -> 0;
	}

	private static List<Seat> noParty() {
		return emptyList();
	}

	private static BiFunction<Seat, Seat, Integer> sequentialDistanceOver(List<Seat> seats) {
		return (s1, s2) -> Math.abs(seats.indexOf(s2) - seats.indexOf(s1));
	}

	private static BiFunction<Seat, Seat, Integer> adjacencyDistance(Seat partySeat, Collection<Seat> adjacentSeats) {
		return (seat1, seat2) -> {
			if (seat1.equals(seat2)) {
				return 0;
			} else if ((partySeat.equals(seat1) && adjacentSeats.contains(seat2))//
					|| (partySeat.equals(seat2) && adjacentSeats.contains(seat1))) {
				return 1;
			} else {
				return 2;
			}
		};
	}

	private static void assertEqualsUnordered(List<Seat> expected, List<Seat> actual) {
		assertEquals(new HashSet<>(expected), new HashSet<>(actual));
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

		@Override
		public String toString() {
			return "Seat[" + hashCode() % 1000 + "]@" + price;
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

		private final Collection<Seat> seats;
		private final Predicate<Seat> freeSeatPredicate;
		private final BiFunction<Seat, Seat, Integer> seatsDistancer;

		public SuggestionSystem(Collection<Seat> seats, Predicate<Seat> freeSeatPredicate,
				BiFunction<Seat, Seat, Integer> seatsDistancer) {
			this.seats = seats;
			this.freeSeatPredicate = freeSeatPredicate;
			this.seatsDistancer = seatsDistancer;
		}

		public List<Seat> offerBestSeatsIn(PriceRange priceRange, Collection<Seat> party) {
			List<Seat> satisfyingSeats = new LinkedList<>();
			for (Seat seat : seats) {
				if (freeSeatPredicate.test(seat) && priceRange.includes(seat.price())) {
					satisfyingSeats.add(seat);
				}
			}

			if (!party.isEmpty()) {
				satisfyingSeats.sort(onPartyAdjacency(party));
			}
			return satisfyingSeats;
		}

		private Comparator<Seat> onPartyAdjacency(Collection<Seat> party) {
			Function<Seat, Integer> partyDistancer = partyDistancerOn(party);
			return (s1, s2) -> {
				Integer dist1 = partyDistancer.apply(s1);
				Integer dist2 = partyDistancer.apply(s2);
				if (dist1 == 1 && dist2 != 1) {
					// s1 is better because adjacent
					return -1;
				} else if (dist1 != 1 && dist2 == 1) {
					// s2 is better because adjacent
					return 1;
				} else {
					// No difference
					return 0;
				}
			};
		}

		private Function<Seat, Integer> partyDistancerOn(Collection<Seat> party) {
			if (party.isEmpty()) {
				throw new IllegalArgumentException("No party seat");
			}
			return seat -> party.stream()//
					.mapToInt(partySeat -> seatsDistancer.apply(partySeat, seat))//
					.min()//
					.getAsInt();
		}
	}
}
