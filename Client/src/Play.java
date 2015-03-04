import java.applet.AudioClip;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.JApplet;
public class Play {

	 static File file;
	static AudioClip audio;
	@SuppressWarnings("deprecation")
	public static void play(){
			try {
			file=new File("image\\msg.wav");
			System.out.println(file);
			audio=JApplet.newAudioClip(file.toURL());
			audio.play();
			
		} catch (MalformedURLException e) {
			System.out.println("fffffffff");
		} 
		   System.out.println("fff");
	}

}
