package saisei.codec.mp3

/**
 * Thrown whenever the Mp3 decoder needs more data to continue decoding, this should not be left uncaught.
 */
public class Mp3NeedMoreDataException : Exception()