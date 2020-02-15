package com.example.demo.domain;

import java.time.LocalDate;
import java.util.Objects;

public class Date {

	private final LocalDate date;

	public Date(LocalDate date) {
		this.date = date;
	}

	public String[] getDateArr() {
		Objects.requireNonNull(date);
		String[] splitDate = date.toString().split("-");
		return new String[] { splitDate[0], splitDate[1], splitDate[2] };
	}
}
