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

		JavaScriptFramework react = new JavaScriptFramework("React", 0L);
		JavaScriptFramework vue = new JavaScriptFramework("Vue.js", 0L);
		
		repository.save(react);
		repository.save(vue);
	}

	@Test
	public void getFrameworksTest() throws Exception {
		mockMvc.perform(get("/frameworks")).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name", is("React")))
				.andExpect(jsonPath("$[1].name", is("Vue.js")));
	}

	@Test
	public void createFrameworkTest() throws Exception {
		var name1 = "React";
		var hype1 = -5L;
		var name2 = "Match ěščřžýáíé 30 chars exact"; // try match with unicode (different bytes count vs char count)
		var hype2 = 5L;
		JavaScriptFramework framework1 = new JavaScriptFramework(name1, hype1);
		JavaScriptFramework framework2 = new JavaScriptFramework(name2, hype2);
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework1)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.name", is(name1)))
			.andExpect(jsonPath("$.hype", is(hype1), Long.class));
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework2)))
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.id", notNullValue()))
			.andExpect(jsonPath("$.name", is(name2)))
			.andExpect(jsonPath("$.hype", is(hype2), Long.class));
	}

	@Test
	public void createFramework_Empty_InvalidTest() throws Exception {
		JavaScriptFramework framework = new JavaScriptFramework();
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(3)))
			.andExpect(jsonPath("$.errors[0].field", is("hype")))
			.andExpect(jsonPath("$.errors[0].code", is("NotNull")))
			.andExpect(jsonPath("$.errors[1].field", is("name")))
			.andExpect(jsonPath("$.errors[1].code", is("NotEmpty")))
			.andExpect(jsonPath("$.errors[2].field", is("versionList")))
			.andExpect(jsonPath("$.errors[2].code", is("NotEmpty")));
	}

	@Test
	public void createFramework_MaxSize_InvalidTest() throws Exception {
		JavaScriptFramework framework = new JavaScriptFramework(
			"verylongnameofthejavascriptframeworkjavaisthebest",
			0L
		);
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].code", is("Size")));
	}

	@Test
	public void createFramework_MinSize_InvalidTest() throws Exception {
		JavaScriptFramework framework = new JavaScriptFramework(
			"ve",
			0L
		);
		mockMvc.perform(post("/frameworks").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsBytes(framework)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasSize(1)))
			.andExpect(jsonPath("$.errors[0].field", is("name")))
			.andExpect(jsonPath("$.errors[0].code", is("Size")));
	}

	@Test
	public void updateFrameworkTest() throws Exception {
		var id1 = 1;
		var id2 = 3; // because of single sequence in mem h2, javascriptframeworkversion record got id=2
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
		var id1 = 1;
		var id2 = 3; // because of single sequence in mem h2, javascriptframeworkversion record got id=2

		Assert.assertEquals(2, ((Collection<?>)repository.findAll()).size());

		mockMvc.perform(delete("/frameworks/" + id1))
			.andExpect(status().is2xxSuccessful());
		mockMvc.perform(delete("/frameworks/" + id2))
			.andExpect(status().is2xxSuccessful());

		Assert.assertEquals(0, ((Collection<?>)repository.findAll()).size());
	}
}
