package fr.vergne.qualitemeritis;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collection;

import org.junit.jupiter.api.Test;

class ProjectTest {

	@Test
	void test() {
		SuggestionSystem system = new SuggestionSystem();
		Collection<Seat> bestSeats = system.offerBestSeats();
	}

	static class Seat {
	}

	static class SuggestionSystem {

		public Collection<Seat> offerBestSeats() {
			// TODO
			return null;
		}
	}
}
