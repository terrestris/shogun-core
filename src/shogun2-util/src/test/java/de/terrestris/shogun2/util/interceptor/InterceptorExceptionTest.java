package de.terrestris.shogun2.util.interceptor;

import org.junit.Test;

public class InterceptorExceptionTest {

	private static final String EXCEPTION_MESSAGE = "Shinji!";

	@Test(expected = InterceptorException.class)
	public void throw_exception_empty() throws InterceptorException {
		throw new InterceptorException();
	}

	@Test(expected = InterceptorException.class)
	public void throw_exception_message() throws InterceptorException {
		throw new InterceptorException(EXCEPTION_MESSAGE);
	}

	@Test(expected = InterceptorException.class)
	public void throw_exception_throwable() throws InterceptorException {
		throw new InterceptorException(new Throwable());
	}

	@Test(expected = InterceptorException.class)
	public void throw_exception_message_throwable() throws InterceptorException {
		throw new InterceptorException(EXCEPTION_MESSAGE, new Throwable());
	}
}
