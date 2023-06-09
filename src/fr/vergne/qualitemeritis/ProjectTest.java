package fr.vergne.qualitemeritis;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ProjectTest {

	@Test
	void test() {
		SuggestionSystem system = new SuggestionSystem();
		system.offerBestSeats();
	}

	static class SuggestionSystem {

		public void offerBestSeats() {
			// TODO
		}
	}
}
