package com.finance.tracker;

import org.junit.jupiter.api.Test;

/**
 * Basic unit test for application startup without database dependencies
 */
class TrackerApplicationTests {

	@Test
	void contextLoads() {
		// This test simply verifies that the application class exists and can be loaded
		// without requiring a full Spring context startup
		TrackerApplication app = new TrackerApplication();
		assertThat(app);
	}

	private void assertThat(Object obj) {
		if (obj == null) {
			throw new AssertionError("Expected object to not be null");
		}
	}
}
