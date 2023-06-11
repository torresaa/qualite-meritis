package fr.vergne.qualitemeritis;

import static java.util.Comparator.comparing;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class SuggestionSystem {

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
