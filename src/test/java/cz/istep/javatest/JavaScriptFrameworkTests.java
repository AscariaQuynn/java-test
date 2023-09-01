package cz.istep.javatest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.istep.javatest.data.JavaScriptFramework;
import cz.istep.javatest.repository.JavaScriptFrameworkRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JavaScriptFrameworkTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private JavaScriptFrameworkRepository repository;

	@Autowired
	private DataSource dataSource;

	private final ObjectMapper mapper = new ObjectMapper();

	@Before
	public void prepareData() throws Exception {
		repository.deleteAll();
		try (Connection dbConnection = dataSource.getConnection()) {
			try (Statement statement = dbConnection.createStatement()) {
				//noinspection SqlResolve
				statement.execute("ALTER SEQUENCE HIBERNATE_SEQUENCE RESTART WITH 1");
			}
		}

		JavaScriptFramework react = new JavaScriptFramework("React");
		JavaScriptFramework vue = new JavaScriptFramework("Vue.js");
		
		repository.save(react);
		repository.save(vue);
	}

	@Test
	public void frameworksTest() throws Exception {
		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is("React")))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));
	}

	@Test
	public void createFrameworkTest() throws Exception {
		var name1 = "React";
		var name2 = "Match ěščřžýáíé 30 chars exact"; // try match with unicode (different bytes count vs char count)

		JavaScriptFramework framework1 = new JavaScriptFramework(name1);
		JavaScriptFramework framework2 = new JavaScriptFramework(name2);
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework1)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.name", is(name1)));
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework2)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.name", is(name2)));
	}

	@Test
	public void createFrameworkInvalidTest() throws Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].code", is("NotEmpty")));

		framework.setName("verylongnameofthejavascriptframeworkjavaisthebest");
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].code", is("Size")));
	}

	@Test
	public void updateFrameworkTest() throws Exception {
		var id1 = 1;
		var id2 = 2;
		var name1 = "React";
		var name2 = "Match ěščřžýáíé 30 chars exact"; // try match with unicode (different bytes count vs char count)

		JavaScriptFramework framework1 = new JavaScriptFramework(name1);
		JavaScriptFramework framework2 = new JavaScriptFramework(name2);
		mockMvc.perform(put("/frameworks/" + id1).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework1)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id", is(id1)))
			.andExpect(jsonPath("$.name", is(name1)));
		mockMvc.perform(put("/frameworks/" + id2).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework2)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id", is(id2)))
			.andExpect(jsonPath("$.name", is(name2)));
	}

	@Test
	public void deleteFrameworkTest() throws Exception {
		Assert.assertEquals(2, ((Collection<?>)repository.findAll()).size());

		mockMvc.perform(delete("/frameworks/1"))
			.andExpect(status().is2xxSuccessful());
		mockMvc.perform(delete("/frameworks/2"))
			.andExpect(status().is2xxSuccessful());

		Assert.assertEquals(0, ((Collection<?>)repository.findAll()).size());
	}
}
