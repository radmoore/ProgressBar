ProgressBar
===========
A text-based ProgressBar for Java. There may be alternatives around, but I was unable to
find a text-based progress bar which is able to run in intermediate mode (that is, indicate
activity when the progress of the activity is unknown). 

###### Features
* Text-Based progress bar written in Java
* Progressable and intermediate mode
* Messaging system
* Width and padding of progress bar customizable
* Charaters used for indicator customizable (e.g. <code>=</code> instead of <code>|</code>)
* Progress timing
* Surpressable output (quite mode)

###### Example
```java

package info.radm.pbar;

public class ProgressTester {
  
  public static void main(String[] args) {
    
    int i = Integer.valueOf(args[0]);
    try {
      // create new 'progressable' ProgressBar instance
      ProgressBar pBar = new ProgressBar(i, "Progress Test");
      for (int j = 0; j <= i; j++) {
        pBar.setCurrentVal(j);
      }   
      
      // switch progress mode to intermediate
      // (which will display no progression)
      pBar.setProgressMode(ProgressBar.INTERMEDIATE_MODE, true);
      // start intermediate progress
      pBar.startIntermediate();
      pBar.setMessage("Waiting for 10 seconds...");
      // display intermediate progress for 10 seconds
      try { Thread.sleep(10000); }   
      catch (InterruptedException ie) { ie.printStackTrace(); }   
      pBar.setMessage("Finished waiting.");
      
      // switch back to progressable mode
      pBar.setProgressMode(ProgressBar.PROGRESSABLE_MODE, true);
      pBar.setMessage("Testing first half of progress");
      pBar.setMaxVal(i);
      for (int j = 0; j <= i; j++) {
        if (j == i/2)
          pBar.setMessage("More than half done");
        pBar.setCurrentVal(j);
      }   
      pBar.setMessage("Test complete");
      // finish displaying progress
      pBar.finish(true);
    }   
    catch (Exception e) {
      e.printStackTrace();
    }   
  }
}
```
<pre>
Progress Test                      : ||||||||||||||||||||||||||||||||||||| 100% [Total: 00:09]     
Finished waiting.                  : ||||||||||||||||||||||||||||||||||||| [00:19]     
Test complete                      : ||||||||||||||||||||||||||||||||||||| 100% [Total: 00:28]     
</pre>

###### TODO
* ETA
* Multiple timestamps
* Choose stream


