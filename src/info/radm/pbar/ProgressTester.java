package info.radm.pbar;

public class ProgressTester {
	
	public static void main(String[] args) {
		int i = 20;
		try {
			ProgressBar pBar = new ProgressBar(i, "Progress Test");
			for (int j = 0; j <= i; j++) {
				pBar.setCurrentVal(j);
			}
			pBar.setProgressMode(ProgressBar.INDETERMINATE_MODE, true);
			pBar.startIndeterminate();
			pBar.setMessage("Waiting for 10 seconds...");
			try {
				Thread.sleep(10000);
			} 
			catch (InterruptedException ie) {
				ie.printStackTrace();
			}
			pBar.setMessage("Finished waiting.");
			pBar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true);

			pBar.setMessage("Testing first half of progress");
			pBar.setMaxVal(i);
			for (int j = 0; j <= i; j++) {
				if (j == i/2) {
					pBar.setMessage("More than half done");
				}
				pBar.setCurrentVal(j);
			}
				pBar.setMessage("Test complete");
				pBar.finish(true);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
}
