package bbdebet2.kernel.mailing;

public class InvalidEncryptionException extends Exception {
	private  static final long serialVersionUID = 1l;

	/**
	 * creates a new InvalidEncryptionException with given message
	 * @param  type name of non-supported encryption
	 */
	public InvalidEncryptionException(String type) {
		super("Invalid encryption type: " + type);
	}
}
