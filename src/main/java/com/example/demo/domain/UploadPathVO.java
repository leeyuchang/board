package com.example.demo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
// @Data
@Getter
@NoArgsConstructor // Mybatis エラー
@AllArgsConstructor
public class UploadPathVO {
	private LocalDateTime id;
	private String root;
	private String year;
	private String month;
	private String day;

	/**
	 * システム日付と同じであれば、TRUE
	 * @return
	 */
	public boolean isToday() {
		Objects.requireNonNull(id);
		return id.toLocalDate().equals(LocalDate.now());
	}

	/**
	 * 日付（月、日）を二桁に変換した日付配列を返す
	 * @return
	 */
	public String[] getDateArr() {
		Objects.requireNonNull(id);
		return new String[] { 
			String.valueOf(id.getYear()), 
			id.getMonthValue() < 10 ? "0" + id.getMonthValue() : String.valueOf(id.getMonthValue()), 
			id.getDayOfMonth() < 10 ? "0" + id.getDayOfMonth() : String.valueOf(id.getDayOfMonth())
		 };
	}

	/**
	 * DBから取得したレコードを配列に返す
	 * @return
	 */
	public String[] getValues() {
		Objects.requireNonNull(root);
		Objects.requireNonNull(year);
		Objects.requireNonNull(month);
		Objects.requireNonNull(day);
		return new String[] { this.root, this.year, this.month, this.day };
	}
}
