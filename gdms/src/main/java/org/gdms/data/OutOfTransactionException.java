package org.gdms.data;

/**
 * thrown when attempting to write data in a DataSource without a previous
 * call to beginTrans or after finishing the transaction by calling commitTrans
 * or rollBackTrans
 */
public class OutOfTransactionException extends RuntimeException{

	public OutOfTransactionException(String msg) {
		super(msg);
	}

}
