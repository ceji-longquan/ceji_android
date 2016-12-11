package com.lqtemple.android.lqbookreader.model;

import java.io.Serializable;
import java.util.Locale;

public class Author implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -9027442126212861173L;
	
	private String firstName;
	private String lastName;
	
	private String authorKey;
	
	public Author(String firstName, String lastName ) {
		this.firstName = firstName;
		this.lastName = lastName;
		
		this.authorKey = firstName.toLowerCase(Locale.US) + "_" + lastName.toLowerCase(Locale.US);
	}
	
	public String getAuthorKey() {
		return authorKey;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
}
