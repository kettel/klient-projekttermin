package models;

/**
 * Enum för uppdragsstatus.
 * 
 * @author kettel
 * 
 */
public enum AssignmentStatus {
	// Ett uppdrag är antingen icke påbörjat (NOT_STARTED), påbörjat (STARTED),
	// behöver hjälp (NEED_HELP) samt avslutat (FINISHED)
	NOT_STARTED, STARTED, NEED_HELP, FINISHED, QUE
}