package data;

public class CreationException extends Exception
{
	public static int NAME_TOO_LONG    = 1;
	public static int TOO_MANY_LOCI    = 2;
	public static int TOO_MANY_INDV    = 3;
	public static int UNKNOWN_FILE     = 4;
	public static int NO_PAL_TREE      = 5;
	public static int NOT_UNIQUE       = 6;
	public static int NO_ALLELES       = 7;
	public static int TOO_MANY_ALLELES = 8;
	
	private int exception;
	private String additional;
	
	public CreationException(int exception)
	{
		super();		
		this.exception = exception;
	}
	
	public CreationException(int exception, String additional)
	{
		this(exception);
		this.additional = additional;
	}
	
	public int getException() { return exception; }
	
	public String toString()
	{
		if (exception == NAME_TOO_LONG)
			return "At least one or more of the markers within the dataset had "
				+  "a name longer than 20 characters.";
		
		if (exception == TOO_MANY_LOCI)
			return "The dataset contains more than 800 loci which is more than "
				+  "can be handled by TetraploidMap at present.";
		
		if (exception == TOO_MANY_INDV)
			return "The dataset contains loci with details on more than 300 "
				+  "offspring which is more than can be handled by "
				+  "TetraploidMap at present.";
		
		if (exception == UNKNOWN_FILE)
			return "The file is of a type unreadable by TetraploidMap.";
		
		if (exception == NOT_UNIQUE)
			return "The file contains one or more loci with the same name ("
				+ additional + " was found at least twice).";
		
		if (exception == NO_ALLELES)
			return "The file contains one or more loci with no allele data.";
		
		if (exception == TOO_MANY_ALLELES)
			return "The file contains one or more loci with more than 8 alleles.";
		
		if (exception == NO_PAL_TREE)
			return "A required dendrogram could not be created.";
		
		return "CreationException: code=" + exception;
	}
}