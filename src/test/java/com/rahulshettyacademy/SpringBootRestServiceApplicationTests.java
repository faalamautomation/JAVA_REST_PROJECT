package com.rahulshettyacademy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLEngineResult.Status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahulshettyacademy.controller.AddResponse;
import com.rahulshettyacademy.controller.Library;
import com.rahulshettyacademy.controller.LibraryController;
import com.rahulshettyacademy.repository.LibraryRepository;
import com.rahulshettyacademy.service.LibraryService;

@SpringBootTest
@AutoConfigureMockMvc

class SpringBootRestServiceApplicationTests {

	@Autowired
	LibraryController con;
	
	@MockBean
	LibraryRepository repository;
	
	@MockBean
	LibraryService libraryservice;
	
	@Autowired
	private MockMvc mockMvc;
	
	
	@Test
	void contextLoads() 
	{
		
	}

	@Test
	public void CheckBuildLogic()
	{
		
		LibraryService lib=new LibraryService();
		String id=lib.buildId("ZMAN", 24);
		assertEquals(id,"OLDZMAN24");
		
	}
	@Test
	public void addBookTest()
	{
		Library lib=Buildlibrary();
		
		when(libraryservice.buildId(lib.getIsbn(),lib.getAisle())).thenReturn(lib.getId());
		when(libraryservice.CheckBookAlreadyExist(lib.getId())).thenReturn(false);
		
		
		
		ResponseEntity res=con.addBookImplementation(Buildlibrary());
		
		System.out.println(res.getStatusCode());
		
		assertEquals(res.getStatusCode(), HttpStatus.CREATED);
		
		AddResponse ad=(AddResponse) res.getBody();
		ad.getId();
		assertEquals(lib.getId(),ad.getId());
		assertEquals("Success Book is Added",ad.getMsg());
	}
	
	@Test
	public void addBookControllerTest() throws Exception
	{
		
		
		Library lib=Buildlibrary();
		ObjectMapper map=new ObjectMapper();
		String jsonString=map.writeValueAsString(lib);
		
		when(libraryservice.buildId(lib.getIsbn(),lib.getAisle())).thenReturn(lib.getId());
		when(libraryservice.CheckBookAlreadyExist(lib.getId())).thenReturn(false);
		when(repository.save(any())).thenReturn(lib);
		
		this.mockMvc.perform(post("/addBook").contentType(MediaType.APPLICATION_JSON)
		               .content(jsonString)).andDo(print()).andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(lib.getId()));
	}
	
	
	@Test
	public void getBookByAuthorTest() throws Exception
	{
		List<Library> lib=new ArrayList<Library>();
		lib.add(Buildlibrary());
		lib.add(Buildlibrary());
		when(repository.findAllByAuthor(any())).thenReturn(lib);
		this.mockMvc.perform(get("/getbooks/author").param("authorname", "fahad"))
		.andDo(print()).andExpect(status().isOk())
		.andExpect(jsonPath("$.length()",is(2)))
		.andExpect(jsonPath("$.[0].id").value("556fgh24"));
		
		
	}
	
	@Test
	public void updateBookByIdTest() throws Exception 
	{
		Library lib=Buildlibrary();
		ObjectMapper map=new ObjectMapper();
		String jsonString=map.writeValueAsString(Updatelibrary());
		
		when(libraryservice.GetBookById(any())).thenReturn(Buildlibrary());
		this.mockMvc.perform(put("/updatebook/"+lib.getId()).contentType(MediaType.APPLICATION_JSON)
		.content(jsonString)).andDo(print()).andExpect(status().isOk())
		.andExpect(content().json("{\"book_name\":\"the fuzzy logic\",\"id\":\"556fgh24\",\"isbn\":\"556fgh\",\"aisle\":29,\"author\":\"Falamgir\"}"));
			
	}
	
	
	@Test
	public void deletebookTest() throws Exception
	{
		
		when(libraryservice.GetBookById(any())).thenReturn(Buildlibrary());
		doNothing().when(repository).delete(Buildlibrary());
		this.mockMvc.perform(delete("/delete").contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\":\"556fgh24\"}")).andDo(print()).andExpect(status().isCreated())
		        .andExpect(content().string("Book is Deleted"));
	}
	
	public Library Buildlibrary()
	{
		Library lib=new Library();
		lib.setAisle(24);
		lib.setAuthor("Fahad");
		lib.setBook_name("The fireman");
		lib.setIsbn("556fgh");
		lib.setId("556fgh24");
		
		return lib;
		
	}
	
	public Library Updatelibrary()
	{
		Library lib=new Library();
		lib.setAisle(29);
		lib.setAuthor("Falamgir");
		lib.setBook_name("the fuzzy logic");
		lib.setIsbn("7878SS");
		lib.setId("7878SS29");
		
		return lib;
		
	}
	
}
