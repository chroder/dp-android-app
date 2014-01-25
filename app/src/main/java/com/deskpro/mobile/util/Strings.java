package com.deskpro.mobile.util;

import com.google.common.base.CharMatcher;

public final class Strings
{
	public static final int TRIM_BOTH  = 0;
	public static final int TRIM_LEFT  = 1;
	public static final int TRIM_RIGHT = 2;

	private Strings() {}

	/**
	 * Trim a character form the beginning and/or end of a string
	 *
	 * @param str  The string to work on
	 * @param chr  The character to trim
	 * @param mode Which side to trim the character from
	 * @return
	 */
	public static String trim(CharSequence str, Character chr, int mode)
	{
		switch (mode) {
			case TRIM_BOTH:
				return CharMatcher.is(chr).trimFrom(str);
			case TRIM_LEFT:
				return CharMatcher.is(chr).trimLeadingFrom(str);
			case TRIM_RIGHT:
				return CharMatcher.is(chr).trimTrailingFrom(str);
			default:
				throw new IllegalArgumentException();
		}
	}


	/**
	 * Trim a character from the beginning and end of a string
	 *
	 * @param str  The string to work on
	 * @param chr  The character to trim
	 * @return str with chr trimmed
	 */
	public static String trim(CharSequence str, Character chr)
	{
		return trim(str, chr, TRIM_BOTH);
	}


	/**
	 * Trim characters form the beginning and/or end of a string
	 *
	 * @param str  The string to work on
	 * @param chrs The characters to trim
	 * @param mode Which side to trim the character from
	 * @return
	 */
	public static String trim(CharSequence str, CharSequence chrs, int mode)
	{
		switch (mode) {
			case TRIM_BOTH:
				return CharMatcher.anyOf(chrs).trimFrom(str);
			case TRIM_LEFT:
				return CharMatcher.anyOf(chrs).trimLeadingFrom(str);
			case TRIM_RIGHT:
				return CharMatcher.anyOf(chrs).trimTrailingFrom(str);
			default:
				throw new IllegalArgumentException();
		}
	}


	/**
	 * Trim characters from the beginning and end of a string
	 *
	 * @param str  The string to work on
	 * @param chrs The characters to trim
	 * @return str with chr trimmed
	 */
	public static String trim(CharSequence str, CharSequence chrs)
	{
		return trim(str, chrs, TRIM_BOTH);
	}
}
