package org.resurged.jdbc;

/**
 * The subclass of RuntimeException which is thrown by the ease of development
 * APIs, such as DataSet.
 */
public class SQLRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -6601416134871891420L;

	/**
	 * Constructs a SQLRuntimeException object with a given reason.
	 * 
	 * @param reason
	 *            - a description of the exception
	 */
	public SQLRuntimeException(String reason) {
		super(reason);
	}

	/**
	 * Constructs a SQLRuntimeException object with a given reason and cause.
	 * 
	 * @param reason
	 *            - a description of the exception.
	 * @param cause
	 *            - the underlying reason for this SQLRuntimeException (which is
	 *            saved for later retrieval by the getCause() method); may be
	 *            null indicating the cause is non-existent or unknown.
	 */
	public SQLRuntimeException(String reason, Throwable cause) {
		super(reason, cause);
	}

	/**
	 * Constructs a SQLRuntimeException object with a given cause. The reason is
	 * initialized to null if cause==null or to cause.toString() if cause!=null.
	 * 
	 * @param cause
	 *            - the underlying reason for this SQLRuntimeException (which is
	 *            saved for later retrieval by the getCause() method); may be
	 *            null indicating the cause is non-existent or unknown.
	 */
	public SQLRuntimeException(Throwable cause) {
		super(cause);
	}
}
