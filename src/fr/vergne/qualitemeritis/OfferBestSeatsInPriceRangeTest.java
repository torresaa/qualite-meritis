package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static java.util.Collections.shuffle;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance(),
				noMiddleRowDistance(), noStageDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance(),
				noMiddleRowDistance(), noStageDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance(),
				noMiddleRowDistance(), noStageDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, uncaringDistance(),
				noMiddleRowDistance(), noStageDistance());
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
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, uncaringDistance(),
				noMiddleRowDistance(), noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(emptyList(), bestSeats);
	}

	static Stream<Integer> seatsCountForAdjacency() {
		return Stream.of(3, 5, 10, 100);
	}

	@ParameterizedTest(name = "{0} seats")
	@MethodSource("seatsCountForAdjacency")
	void testReturnsAdjacentSeatsFirstForSinglePartyMember(int seatsCount) {
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
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer,
				noMiddleRowDistance(), noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEqualsUnordered(adjacentSeats, bestSeats.subList(0, adjacentSeats.size()));
	}

	@Test
	void testReturnsAdjacentSeatsFirstForAllPartyMembers() {
		// GIVEN
		Price price = Price.euros(5);
		// Create a lot of seats
		List<Seat> allSeats = range(0, 100).mapToObj(i -> new Seat(price)).collect(toList());

		// Extract a few seats for party and adjacent seats
		// The suggestion should prioritize these few in the mass
		Iterator<Seat> remainingSeat = allSeats.iterator();
		Map<Seat, Collection<Seat>> adjacencies = range(0, 3)//
				.mapToObj(i -> i)// Transform to object stream to access collect method
				.collect(toMap(//
						// party seat as key
						i -> remainingSeat.next(), //
						// list of few adjacent seats as value
						i -> Arrays.asList(remainingSeat.next(), remainingSeat.next())//
				));
		BiFunction<Seat, Seat, Integer> seatsDistancer = adjacencyDistance(adjacencies);
		shuffle(allSeats, new Random(0));// Ensure uncorrelated order with adjacencies

		Collection<Seat> party = adjacencies.keySet();
		Collection<Seat> adjacentSeats = mergedValues(adjacencies);
		Predicate<Seat> freeSeatPredicate = seat -> !party.contains(seat);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer,
				noMiddleRowDistance(), noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> bestSeats = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEqualsUnordered(adjacentSeats, bestSeats.subList(0, adjacentSeats.size()));
	}

	@Test
	void testReturnsSeatsNearestToMiddleOfRow() {
		// GIVEN
		Price price = Price.euros(5);
		// Create a lot of seats
		List<Seat> allSeats = range(0, 100).mapToObj(i -> new Seat(price)).collect(toList());

		// Fix the seats distance to the middle based on their index.
		// It totally constrains the expected result.
		List<Seat> expected = new ArrayList<>(allSeats);
		Function<Seat, Integer> middleRowDistancer = seat -> expected.indexOf(seat);
		shuffle(allSeats, new Random(0));// Ensure uncorrelated order with expected result

		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance(),
				middleRowDistancer, noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> bestSeats = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(expected, bestSeats);
	}

	@Test
	void testReturnsSeatsNearestToStage() {
		// GIVEN
		Price price = Price.euros(5);
		// Create a lot of seats
		List<Seat> allSeats = range(0, 100).mapToObj(i -> new Seat(price)).collect(toList());

		// Fix the seats distance to the middle based on their index.
		// It totally constrains the expected result.
		List<Seat> expected = new ArrayList<>(allSeats);
		Function<Seat, Integer> stageDistancer = seat -> expected.indexOf(seat);
		shuffle(allSeats, new Random(0));// Ensure uncorrelated order with expected result

		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance(),
				noMiddleRowDistance(), stageDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> result = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(expected, result);
	}

	@Test
	void testReturnsSeatCloseToPartyOverSeatCloseToMiddleOfRow() {
		// GIVEN
		Price price = Price.euros(5);
		Seat partySeat = new Seat(price);
		Seat seatCloseToParty = new Seat(price);
		Seat seatCloseToMiddle = new Seat(price);
		List<Seat> allSeats = Arrays.asList(partySeat, seatCloseToParty, seatCloseToMiddle);

		List<Seat> party = Arrays.asList(partySeat);
		Predicate<Seat> freeSeatPredicate = seat -> !party.contains(seat);
		BiFunction<Seat, Seat, Integer> seatsDistancer = adjacencyDistance(partySeat, Arrays.asList(seatCloseToParty));

		Function<Seat, Integer> middleRowDistancer = seat -> seat.equals(seatCloseToMiddle) ? 0 : 1;

		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, seatsDistancer, middleRowDistancer,
				noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> result = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEquals(Arrays.asList(seatCloseToParty, seatCloseToMiddle), result);
	}

	@Test
	void testReturnsSeatCloseToMiddleOfRowOverSeatCloseToStage() {
		// GIVEN
		Price price = Price.euros(5);
		Seat seatCloseToStage = new Seat(price);
		Seat seatCloseToMiddle = new Seat(price);
		List<Seat> allSeats = Arrays.asList(seatCloseToStage, seatCloseToMiddle);

		Function<Seat, Integer> middleRowDistancer = seat -> seat.equals(seatCloseToMiddle) ? 0 : 1;

		Function<Seat, Integer> stageDistancer = seat -> seat.equals(seatCloseToStage) ? 0 : 1;

		SuggestionSystem system = new SuggestionSystem(allSeats, nothingBooked(), uncaringDistance(),
				middleRowDistancer, stageDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> result = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(Arrays.asList(seatCloseToMiddle, seatCloseToStage), result);
	}

	private static Collection<Seat> mergedValues(Map<Seat, Collection<Seat>> adjacencies) {
		return adjacencies.values().stream().flatMap(Collection::stream).toList();
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

	private static Function<Seat, Integer> noMiddleRowDistance() {
		return seat -> 0;
	}

	private static Function<Seat, Integer> noStageDistance() {
		return seat -> 0;
	}

	private static List<Seat> noParty() {
		return emptyList();
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

	private static BiFunction<Seat, Seat, Integer> adjacencyDistance(Map<Seat, Collection<Seat>> adjacencies) {
		List<BiFunction<Seat, Seat, Integer>> distancers = adjacencies.entrySet().stream()//
				.map(entry -> adjacencyDistance(entry.getKey(), entry.getValue()))//
				.toList();
		return (seat1, seat2) -> {
			return distancers.stream()//
					.mapToInt(distancer -> distancer.apply(seat1, seat2))//
					.min().getAsInt();
		};
	}

	private static void assertEqualsUnordered(Collection<Seat> expected, Collection<Seat> actual) {
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
		private final Function<Seat, Integer> middleRowDistancer;
		private final Function<Seat, Integer> stageDistancer;

		public SuggestionSystem(Collection<Seat> seats, Predicate<Seat> freeSeatPredicate,
				BiFunction<Seat, Seat, Integer> seatsDistancer, Function<Seat, Integer> middleRowDistancer,
				Function<Seat, Integer> stageDistancer) {
			this.seats = seats;
			this.freeSeatPredicate = freeSeatPredicate;
			this.seatsDistancer = seatsDistancer;
			this.middleRowDistancer = middleRowDistancer;
			this.stageDistancer = stageDistancer;
		}

		public List<Seat> offerBestSeatsIn(PriceRange priceRange, Collection<Seat> party) {
			return seats.stream()//
					.filter(freeSeatPredicate)//
					.filter(seat -> priceRange.includes(seat.price()))//
					.sorted(onDistanceTo(party)//
							.thenComparing(onMiddleRowDistance())//
							.thenComparing(onStageDistance())//
					).toList();
		}

		private Comparator<Seat> onStageDistance() {
			return comparing(stageDistancer);
		}

		private Comparator<Seat> onMiddleRowDistance() {
			return comparing(middleRowDistancer);
		}

		private Comparator<Seat> onDistanceTo(Collection<Seat> party) {
			return party.isEmpty() ? allSeatsEquidistant() : seatsAdjacentTo(party);
		}

		private Comparator<Seat> allSeatsEquidistant() {
			return (seat1, seat2) -> 0;
		}

		private Comparator<Seat> seatsAdjacentTo(Collection<Seat> party) {
			return comparing(distanceFrom(party), favorDistanceOfOne());
		}

		private Comparator<? super Integer> favorDistanceOfOne() {
			return (dist1, dist2) -> {
				return dist1 == 1 && dist2 != 1 ? -1 // s1 is better because adjacent
						: dist1 != 1 && dist2 == 1 ? 1 // s2 is better because adjacent
								: 0;// No difference in other cases
			};
		}

		private Function<Seat, Integer> distanceFrom(Collection<Seat> party) {
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
