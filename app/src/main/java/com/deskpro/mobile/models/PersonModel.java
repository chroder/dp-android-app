package com.deskpro.mobile.models;

public class PersonModel
{
	private int id;
	private String name;
	private String email;

	public PersonModel(int id, String name, String email)
	{
		this.id = id;
		this.name = name;
		this.email = email;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getEmail()
	{
		return email;
	}
}
