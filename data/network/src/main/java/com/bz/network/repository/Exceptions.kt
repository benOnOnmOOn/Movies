package com.bz.network.repository

public class EmptyBodyException : Exception("Empty body")

public class HttpException(message: String) : Exception(message)

public class NoInternetException : Exception("No Internet Connection")
