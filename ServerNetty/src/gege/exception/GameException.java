package gege.exception;

public class GameException extends ServerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public GameException(String msg) {
		super(msg);
	}
	
	
	
	public GameException( String msg, Object keywords ){
		this( String.format( "%s : [%s].", msg, keywords ) );
	}

}
