package fr.vergne.qualitemeritis;

import static java.util.Collections.emptyList;
import static java.util.Collections.shuffle;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

	@Test
	void testReturnsOnlySeatsInPriceRange() {
		// GIVEN
		Price minPrice = Price.euros(2);
		Price maxPrice = Price.euros(4);
		PriceRange priceRange = new PriceRange(minPrice, maxPrice);

		Seat seatBeforeRange = new Seat(Price.euros(1));
		Seat seatMinRange = new Seat(Price.euros(2));
		Seat seatMidRange = new Seat(Price.euros(3));
		Seat seatMaxRange = new Seat(Price.euros(4));
		Seat seatAfterRange = new Seat(Price.euros(5));
		List<Seat> allSeats = Arrays.asList(seatBeforeRange, seatMinRange, seatMidRange, seatMaxRange, seatAfterRange);
		List<Seat> seatsInRange = Arrays.asList(seatMinRange, seatMidRange, seatMaxRange);

		SuggestionSystem system = new SuggestionSystem(allSeats, allSeatsFree(), noSeatDistance(),
				noMiddleRowDistance(), noStageDistance());

		// WHEN
		Collection<Seat> result = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEqualsUnordered(seatsInRange, result);
	}

	static Stream<Object[]> testReturnsOnlyFreeSeats() {
		return Stream.of(//
				10, // Nominal case (reasonable size)
				1, 2, 3, // Corner cases (small size)
				10000// Corner case (big size)
		)//
				.flatMap(total -> {
					return Stream.of(//
							Arrays.asList(total, 0), // No seat free
							Arrays.asList(total, 1), // One seat free
							Arrays.asList(total, total / 2), // Half of seats free
							Arrays.asList(total, total - 1), // All but one seat free
							Arrays.asList(total, total)// All seats free
					);
				})//
				.distinct()// Remove redundant cases
				.map(List::toArray); // As Object[] to be compatible with parameterized tests
	}

	@ParameterizedTest(name = "{1} free seats in {0}")
	@MethodSource
	void testReturnsOnlyFreeSeats(int countTotalSeats, int countFreeSeats) {
		// GIVEN
		Price price = Price.euros(5);
		List<Seat> allSeats = range(0, countTotalSeats).mapToObj(i -> new Seat(price)).toList();
		List<Seat> freeSeats = allSeats.subList(0, countFreeSeats);
		Predicate<Seat> freeSeatPredicate = seat -> freeSeats.contains(seat);
		SuggestionSystem system = new SuggestionSystem(allSeats, freeSeatPredicate, noSeatDistance(),
				noMiddleRowDistance(), noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		Collection<Seat> result = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEqualsUnordered(freeSeats, result);
	}

	@Test
	void testReturnsSeatsAdjacentToParty() {
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
		List<Seat> result = system.offerBestSeatsIn(priceRange, party);

		// THEN
		assertEqualsUnordered(adjacentSeats, result.subList(0, adjacentSeats.size()));
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

		SuggestionSystem system = new SuggestionSystem(allSeats, allSeatsFree(), noSeatDistance(), middleRowDistancer,
				noStageDistance());
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> result = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(expected, result);
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

		SuggestionSystem system = new SuggestionSystem(allSeats, allSeatsFree(), noSeatDistance(),
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

		SuggestionSystem system = new SuggestionSystem(allSeats, allSeatsFree(), noSeatDistance(), middleRowDistancer,
				stageDistancer);
		PriceRange priceRange = new PriceRange(price, price);

		// WHEN
		List<Seat> result = system.offerBestSeatsIn(priceRange, noParty());

		// THEN
		assertEquals(Arrays.asList(seatCloseToMiddle, seatCloseToStage), result);
	}

	private static Collection<Seat> mergedValues(Map<Seat, Collection<Seat>> adjacencies) {
		return adjacencies.values().stream().flatMap(Collection::stream).toList();
	}

	private static Predicate<Seat> allSeatsFree() {
		return seat -> true;
	}

	private static BiFunction<Seat, Seat, Integer> noSeatDistance() {
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

}
