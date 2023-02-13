package com.rahulshettyacademy.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.rahulshettyacademy.controller.Library;

public class LibraryRepositoryImpl implements LibraryRepositoryCustom 
{
	@Autowired
	LibraryRepository repository;

	@Override
	public List<Library> findAllByAuthor(String authorName) {
		// TODO Auto-generated method stub
		List<Library> bookswithauthor=new ArrayList<Library>();
		List<Library> books=repository.findAll();
		for(Library item:books)
		{
			if(item.getAuthor().equalsIgnoreCase(authorName)) 
			{
				
				bookswithauthor.add(item);
			}
		}
		return bookswithauthor;
	}
	
	

}
