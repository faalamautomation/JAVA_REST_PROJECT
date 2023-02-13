package com.rahulshettyacademy.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.rahulshettyacademy.repository.LibraryRepository;
import com.rahulshettyacademy.service.LibraryService;

@RestController
public class LibraryController {

	@Autowired
	LibraryRepository repository;
	
	@Autowired
	AddResponse addbook;
	
	@Autowired
	LibraryService libraryservice;
	
	private static final Logger logger=LoggerFactory.getLogger(LibraryController.class);
	
	@PostMapping("/addBook")
	public ResponseEntity addBookImplementation(@RequestBody Library library)
	{
		
		
		String id=libraryservice.buildId(library.getIsbn(),library.getAisle());
		if(!libraryservice.CheckBookAlreadyExist(id))
	    {
			logger.info("Book does not exist so creating one");
			
		library.setId(id);
		repository.save(library);
		addbook.setMsg("Success Book is Added");
		addbook.setId(library.getIsbn()+library.getAisle());
		HttpHeaders headers=new HttpHeaders();
		headers.add("unique",id);
		return new ResponseEntity<AddResponse>(addbook,headers,HttpStatus.CREATED);
	    }
		
		else
		{
			logger.info("Book exists so skipping creation");
			addbook.setMsg("Book Already Exist");
			addbook.setId(id);
			return new ResponseEntity<AddResponse>(addbook, HttpStatus.ACCEPTED);
			
		}
	}	
	
	@GetMapping("/getbooks/{id}")
	public Library GetBookByID(@PathVariable(value="id") String id)
	{
		
		try 
		{
		Library lib=repository.findById(id).get();
		return lib;
		}
        catch(Exception ex)
        {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }		
	}
	
	@GetMapping("/getbooks/author")
	public List<Library> GetBookByAuthorName(@RequestParam(value="authorname") String authorName )
	{
		return repository.findAllByAuthor(authorName);
		
	}
	
	@PutMapping("/updatebook/{id}")
	public ResponseEntity<Library> UpdateBookById(@PathVariable(value="id") String id, @RequestBody Library library)
	{
		
      //Library existingbook=repository.findById(id).get();
		Library existingbook=libraryservice.GetBookById(id);
		
		existingbook.setAisle(library.getAisle());
		existingbook.setAuthor(library.getAuthor());
		existingbook.setBook_name(library.getBook_name());
		
		repository.save(existingbook);
		
		return new ResponseEntity<Library>(existingbook,HttpStatus.OK);
		
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<String> DeleteBookById(@RequestBody Library library )
	{
		logger.info("Book is deleted");
		//Library libdelete=repository.findById(library.getId()).get();
		Library libdelete=libraryservice.GetBookById(library.getId());
		repository.delete(libdelete);
		
		return new ResponseEntity<>("Book is Deleted",HttpStatus.CREATED);
		
	}
	
	@GetMapping("/getbooks")
	public List<Library> GetAllBooks(Library library)
	{
		List<Library> liball=new ArrayList<Library>();
		List<Library> books=repository.findAll();
		for(Library item:books)
		{
			liball.add(item);
		}
		return liball;
	}
	
}
