package info.radm.pbar;


/**
 * A simple, customizable class for displaying progression
 * (or activity) using a text-based progress bar. Supports
 * to types of progress indication. In PROGRESSABLE_MODE,
 * the progress is indicated by setting the current value
 * of progress up to a defined max value. 
 * 
 * When the max value of progress in unknown, the use of the
 * INTERMEDIATE_MODE allows to indicate indefiniate progress
 * (activity).
 *  
 * @author <a href="http://radm.info">Andrew D. Moore</a>
 * @version 0.7.1
 * @see Thread
 *
 */
public class ProgressBar {

	/**
	 * Intermediate progress mode for use when the duration of an activity is unknown.
	 * See @{link #setProgressMode(int, boolean)}
	 */
	public static int INDERTERMINATE_MODE = 0;
	
	/**
	 * Progressable progress mode for use the duration of an activity is known
	 * See {@link #setProgressMode(int, boolean)}
	 */
	public static int PROGRESSABLE_MODE = 1;
	
	/**
	 * Characters used for 'drawing' progress:
	 * |||||||||||||||||||||||||||||||||||||| (DEFAULT)
	 * |====================================|
	 * |\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\|
	 * |####################################|
	 */
	
	/**
	 * Character used for 'drawing' 
	 * {@code
	 * ||||||||||||||||||||||||||||||||||||||
	 * }
	 * This is the <b>DEFAULT</b> indicator character.
	 * Set character using {@link #setIndicatorCharater(char)}.
	 */
	public static char SIGN_1 = '|';

	/**
	 * Character used for 'drawing' 
	 * {@code
	 * |====================================|
	 * }
	 * Set character using {@link #setIndicatorCharater(char)}.
	 */
	public static char SIGN_2 = '=';
	

	/**
	 * Character used for 'drawing' 
	 * {@code
	 * |\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\|
	 * }
	 * Set character using {@link #setIndicatorCharater(char)}.
	 */
	public static char SIGN_3 = '\\';
	
	/**
	 * Character used for 'drawing' 
	 * {@code
	 * |####################################|
	 * }
	 * Set character using {@link #setIndicatorCharater(char)}.
	 */
	public static char SIGN_4 = '#';

	/**
	 * Timestamps
	 */
	@SuppressWarnings("unused")
	private int current, max, seconds, minutes, ETAsec, ETAmin;
	private long start = -1, elapsed, ETAtime;
	private String runningTime, message = "";
	private boolean indicate = false, quiet = false;
	private char indChar = SIGN_1;
	private int mode = 0;
	private int barWidth = 35;
	
	/**
	 * Use this constructor to create a new ProgressBar in indeterminate_mode.
	 * Change the mode on instance using setProgressMode.
	 *   
	 * @param message - the message string which precedes the progress bar.
	 * @throws Exception if message > 35 characters
	 * @see Exception
	 * 
	 */
	public ProgressBar(String message) {
		setMessage(message);
		this.indicate = true;
	}
	
	/**
	 * Use this constructor to create a ProgressBar in progressable mode
	 * Change the mode on instance using setProgressMode.
	 * 
	 * @param max - the max value for the progress bar.
	 * @param message - the message string which precedes the progress bar.
	 * @throws Exception if message > 35 characters
	 * @see Exception
	 */
	public ProgressBar(int max, String message) {
		setMessage(message);
		this.max = max;
		this.indicate = true;
		try {
			setProgressMode(ProgressBar.INDETERMINATE_MODE, false);
		}
		catch (Exception e) { };
	}
	
	/**
	 * Sets the maximum value for the progress
	 * (marks 100% of the activity)
	 * 
	 * @param max - the maximum value of the progress
	 */
	public void setMaxVal(int max) {
		this.max = max;
	}
	
	/**
	 * When ProgressBar is set to quiet, nothing
	 * will be displayed. However, time stamps will be
	 * taken such that when quiet mode is switched off again
	 * progress will have continued.
	 */
	public void setQuietMode(boolean quiet) {
		this.quiet = quiet;
	}
	
	
	/**
	 * Set the character used for indication. This character 
	 * will be used to visually advance the progress in either modes.
	 * 
	 * @param indicatorChar - the character used for indication
	 */
	public void setIndicatorCharater(char indicatorChar) {
		this.indChar = indicatorChar;
	}
	
	/**
	 * Set the progress bar to mode specified by mode, and indicate 
	 * whether the current progress bar should finish (which will
	 * add a new line).
	 * 
	 * @param mode - the ProgressBar mode
	 * @param finish - finish currently displayed ProgressBar (if any)
	 */
	public void setProgressMode(int mode, boolean finish){

		if (mode == PROGRESSABLE_MODE) {
			if (this.mode == INDERTERMINATE_MODE) {	
				indicate = false;
				if ((!quiet) && (finish))
					finishIntermediate(true);
			}
			this.mode = mode;
			reset();
		}
		else if (mode == INDERTERMINATE_MODE) {
			if ((!quiet) && (finish))
				finishProgress(true);
			this.mode = mode;
			reset();
		}
		else
			return;
		
	}

	
	/**
	 * Start indication of ProgressBar in indeterminate mode. 
	 * This will run in a new Thread. As, in comparison to 
	 * ProgressBar.PROGRESSABLE_MODE, the progess indication in
	 * intermediate mode is indefiniate it must be placed in a seperate
	 * thread. This seperate thread terminiates when finish(newline) 
	 * is called on an instance of ProgressBar.
	 * 
	 * @see Thread
	 */
	public void startIntermediate() {
		startThread();
	}
	
	/**
	 * Set the message displayed in front of the ProgressBar.
	 * The space to write in is 35 characters long. If less, 
	 * the message will be padded to the right end with white spaces.
	 * 
	 * @param message - the message to be set in front of the ProgressBar
	 */
	public void setMessage(String message) {
		if (message.length() <= 35)
			this.message = String.format("%1$-" + 35 + "s", message);
		else if ( message.length() == 25 )
			this.message = message;
		else
			this.message = message.substring(0, 32)+"...";
		
	}
	
	/**
	 * Set the current value of the Progress to currentValue. This should only be
	 * used on a ProgressBar instance in progressable mode, and
	 * will do nothing if called on an instance in intermediate mode. 
	 * 
	 * @param currentValue - the current value of the progress
	 */
	public void setCurrentVal(int currentValue) {
		if (this.mode == ProgressBar.INDETERMINATE_MODE)
			return;
		if (start == -1)
			start = System.currentTimeMillis();
	  this.current = currentValue;
	  if (!quiet)
		  this.printProgressBar();
	}
	
	/**
	 * Increase the current value of the Progress. This should only be
	 * used on a ProgressBar instance in progressable mode, and
	 * will do nothing if called on an instance in intermediate mode. 
	 * 
	 */
	public void incProgress() {
		if (this.mode == ProgressBar.INDETERMINATE_MODE)
			return;
		if (start == -1)
			start = System.currentTimeMillis();
	  this.current += 1; 
	  if (!quiet)
		  this.printProgressBar();
	}
	
	
	/**
	 * Finish the current progress. This will reprint the whole
	 * progress bar, displaying 100% of the progress. If 
	 * newLine is <code>true</code>, a new line will be added after the complete
	 * progress bar.
	 * 
	 * @param newLine - whether or not a new line should be appended
	 */
	public void finish(boolean newLine) {
	//	finished = true;
		if (mode == INDETERMINATE_MODE)
			finishIntermediate(newLine);
		else
			finishProgress(newLine);
		
		reset();
	}		
	
	
	
	//##############################
	// PRIVATE METHODS
	//##############################

	/**
	 * Resets all time stamps 
	 */
	private void reset() {
		current = 0;
	    elapsed = 0;
	    seconds = 0;
	    minutes = 0;
    	ETAtime = 0;
    	ETAsec = 0;
    	ETAmin = 0;
    	//ETAstring = "--:--";
    	runningTime = "";
	}
	
	
	/**
	 * Finishes the current progressable ProgressBar
	 * 
	 * @param newLine - whether or not to append a boolean
	 */
	private void finishProgress(boolean newLine) {
	    this.current = this.max;
	    takeTime();
		if (!quiet) {
		    StringBuilder finalBar = new StringBuilder();
		    finalBar.append("|");
		    for (int i= 0; i < barWidth; i++)
		    	finalBar.append(indChar);
		    finalBar.append("|");
		    finalBar.append(" 100% [Total: "+runningTime+"]     ");
		    System.err.print(message+": "+finalBar);
		    if (newLine)
		       	System.err.println("");
		}
	}
	
	/**
	 * Finishes the current intermediate ProgressBar
	 * 
	 * @param newLine - whether or not to append a boolean
	 */
	private void finishIndeterminate(boolean newLine) {
		indicate = false;
		takeTime();
		//this.start = System.currentTimeMillis();
		if (!quiet) {
		    StringBuilder finalBar = new StringBuilder();
		    finalBar.append("|");
		    for (int i= 0; i < barWidth; i++)
		    	finalBar.append(indChar);
		    finalBar.append("|");
	    	finalBar.append(" ["+runningTime+"]     ");
		    System.err.print("\r"+message+": "+finalBar);
		    if (newLine)
		    	System.err.println("");
		}
	}
	
	/**
	 * Prints an indeterminate ProgressBar
	 */
	private void printInterBar() {
		int indSize = 10;
		int pos = 0;
		int start = 0;
		StringBuilder iPbar = new StringBuilder();
	  
		while (indicate) {
			takeTime();
			if (pos >= barWidth) {
				start = 0;
				pos = 0;
				indSize = 10;
			} 
			if (indSize < 10) {
				start = 10 - indSize;
				for (int i = 0; i < start; i++) {
					iPbar.append(indChar);
				 }
			}
			if (pos + indSize > barWidth)
				indSize -= 1;
			  
			for (int i = start; i < pos; i++) {
				 iPbar.append(" ");
			}
			for (int i = pos; i < pos+indSize; i++) {
				 iPbar.append(indChar);
			}
			for (int i = pos+indSize; i < barWidth; i++) {
				 iPbar.append(" ");
			}

			System.err.print(message+": |"+iPbar+"| ["+runningTime+"]");
			System.err.print("\r");
			iPbar = new StringBuilder();
			pos += 1;
			try {
				 Thread.sleep(25);
			} 
			catch (InterruptedException e) {
				 e.printStackTrace();
			}
		}	
	}
	
	
	/**
	 * Prints a progressable ProgressBar
	 *
	 */
	private void printProgressBar() {
	    
		double numbar = Math.floor(barWidth*(double)current/(double)max);
		double progress = Math.floor(100*((double)current/(double)max));
	    StringBuilder strbar = new StringBuilder();
	    int i = 0;
	    
	    for(i = 0; i < numbar; i++)
	    	strbar.append(indChar);
	    
	    for(i = (int)numbar; i < barWidth; i++)
	    	strbar.append(" ");
	    
	    takeTime(); 
	    System.err.print(message+": |"+strbar+"| "+(int)progress+"%          "); 
		System.err.print("\r");
	}

	
	/**
	 * Stopwatch 
	 */
	private void takeTime() {
	    elapsed = (System.currentTimeMillis() - this.start);
	    seconds = (int)(elapsed / 1000)%60;
	    minutes = (int)(elapsed / 1000)/60;
    	ETAtime = elapsed * (long)((double)max/(double)current);
    	ETAsec = (int)(ETAtime /1000)%60;
    	ETAmin = (int)(ETAtime /1000)/60;
    	//ETAstring = String.format("%02d",ETAmin)+":"+String.format("%02d",ETAsec);
    	runningTime = String.format("%02d",minutes)+":"+String.format("%02d",seconds);
	}
	
	
	/**
	 * Starts new thread. Invoked by calling <code>start()</code>
	 */
	private void startThread() {
		new Thread() {
			public void run() {
				start = System.currentTimeMillis();
				try {
					if (mode == INDETERMINATE_MODE)
						if (!quiet)
							printInterBar();
					else
						if (!quiet)
							printProgressBar();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
}
