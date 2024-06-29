package com.skybreak.samurai.application.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skybreak.samurai.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class SearchControllerIT extends IntegrationTestBase {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mvc;

	@Test
	public void given_search_request_then_should_give_successful_response() throws Exception {

		mvc.perform(MockMvcRequestBuilders
				.get("/api/v1/search")
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.searchTrackingId").isString())
			.andExpect(MockMvcResultMatchers.jsonPath("$.language").value("en"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.content").isEmpty());
	}

}