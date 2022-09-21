package com.notarius.lepetite.PetiteShortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.notarius.lepetite.PetiteShortener.controllers.ShortenerMongoRepository;
import com.notarius.lepetite.PetiteShortener.exceptions.BadRequestException;
import com.notarius.lepetite.PetiteShortener.exceptions.NotFoundException;
import com.notarius.lepetite.PetiteShortener.model.DictionaryEntry;
import com.notarius.lepetite.PetiteShortener.rest.PetiteRestController;
import com.notarius.lepetite.PetiteShortener.utils.ShortenerUtils;
import lombok.extern.log4j.Log4j2;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.class)
@Log4j2
public class PetiteRestControllerTests
{
	private ObjectMapper objectMapper = new ObjectMapper();
	private ObjectWriter objectWriter = objectMapper.writer();
	@Mock
	private ShortenerMongoRepository persistor;

	@InjectMocks
	private PetiteRestController shortenerController;
	private MockMvc mockMVC;

	private DictionaryEntry entry1 = new DictionaryEntry("http://www.wikepedia.org","http://localhost:8080/s/encicloped");
	private DictionaryEntry entry2 = new DictionaryEntry("http://www.netflix.com","http://localhost:8080/s/videoteca1");
	private DictionaryEntry entry3 = new DictionaryEntry("AnIncorrectAddres!sdas","http://localhost:8080/s/SuperMarket");

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		this.mockMVC = MockMvcBuilders.standaloneSetup(shortenerController).build();
	}

	@Test
	public void getShortURL_success() throws Exception
	{
		Mockito.lenient().when(persistor.save(entry1)).thenReturn(entry1);

		String bodyContent = objectWriter.writeValueAsString(entry1);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/petiteshortener")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(bodyContent);

		mockMVC.perform(mockRequest)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", notNullValue()));
	}

	@Test
	public void getShortURL_BadRequest() throws Exception
	{
		String bodyContent = objectWriter.writeValueAsString(entry3);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/petiteshortener")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(bodyContent);

		mockMVC.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
				.andExpect(result -> assertEquals(ShortenerUtils.BAD_URL, result.getResolvedException().getMessage()));

	}

	@Test
	public void getFullURL_success() throws Exception
	{
		Mockito.when((persistor.existsById(entry2.getShorturl()))).thenReturn(true);
		Mockito.when(persistor.findById(entry2.getShorturl())).thenReturn(Optional.of(entry2));

		mockMVC.perform(MockMvcRequestBuilders.get(entry2.getShorturl()))
				.andExpect(status().isFound())
				.andExpect(redirectedUrl(entry2.getFullurl()));
	}

	@Test
	public void getFullURL_NotFound() throws Exception
	{
		mockMVC.perform(MockMvcRequestBuilders.get(entry3.getShorturl()))
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NotFoundException))
				.andExpect(result -> assertEquals(ShortenerUtils.KEY_NOT_FOUND, result.getResolvedException().getMessage()));;

	}

	@Test
	public void AssignAliasURL_BadRequest() throws Exception
	{
		String bodyContent = objectWriter.writeValueAsString(entry3);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/s/assign")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(bodyContent);

		mockMVC.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
				.andExpect(result -> assertEquals(ShortenerUtils.BAD_URL, result.getResolvedException().getMessage()));
	}

	@Test
	public void AssignAliasURL_AliasTaken() throws Exception
	{
		String bodyContent = objectWriter.writeValueAsString(entry2);

		Mockito.when(persistor.existsById(entry2.getShorturl())).thenReturn(true);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/s/assign")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(bodyContent);

		mockMVC.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
				.andExpect(result -> assertEquals(ShortenerUtils.ALIAS_TAKEN, result.getResolvedException().getMessage()));
	}

	@Test
	public void AssignAliasURL_DuplicatedEntry() throws Exception
	{
		String bodyContent = objectWriter.writeValueAsString(entry2);

		Mockito.when(persistor.existsById(entry2.getShorturl())).thenReturn(false);
		Mockito.when(persistor.existsByFullurl(entry2.getFullurl())).thenReturn(true);

		MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/s/assign")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(bodyContent);

		mockMVC.perform(mockRequest)
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
				.andExpect(result -> assertEquals(ShortenerUtils.DUPLICATED_ENTRY, result.getResolvedException().getMessage()));
	}

}