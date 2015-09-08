package pt.trycatch.jamify;

import pt.trycatch.jamify.jammer.Song;
import pt.trycatch.jamify.learner.AutoHorizontalScrollView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

public class LearnActivity extends Activity {

	public static Song mardySong;
	public static Song wishSong;
	public static Song karmaSong;
	public static Song letSong;

	static {
		mardySong = BackendRequestThread.parseSong("103;602;4;4;0:G Major;1.8576:F# Minor;2.322:F# Major;4.08671:E Minor;4.45823:E Major;5.47991:E Minor;6.8731:A Major;10.6812:F# Minor;10.9598:C# Major;11.1456:F# Major;12.0744:F# Major;12.7246:G Major;13.0032:G Major;13.7462:C# Diminished;14.1178:C# Minor;14.7679:E Minor;15.3252:E Minor;15.8825:E Major;16.6255:A Major;19.5048:F# Major;20.6194:C# Major;21.0838:G Major;21.6411:G Major;22.7556:F# Major;23.3129:G Major7;25.1705:A Major;25.9135:D Major;27.9569:F# Major;29.5359:B Minor;29.7216:B Minor;30.6504:A Augmented;30.7433:A Major;31.8579:E Minor;33.344:F# Minor;34.2728:D Major7;35.1087:D Major;36.3161:C# Minor;36.7805:F# Major;37.8951:C# Major;38.2666:G Major7;39.8456:C# Minor;40.4029:G Major;40.5886:E Minor;42.632:G Major;44.8611:A Major;46.8116:D Major;47.7404:A Major;49.4123:B Minor;51.177:G Major;53.3132:A Major;55.821:D Major;56.2854:A Major;58.143:B Minor;58.886:D Major;59.8148:G Major;62.044:A Major;64.6446:D Major;65.2019:A Major;66.4093:F# Minor;66.8737:F# Minor;66.9666:F# Major;67.8025:F# Major;68.1741:F# Minor;69.4744:F# Minor;69.8459:G Diminished;70.4032:G Major7;71.1462:E Minor;72.1679:A Major;74.8614:F# Minor;75.3258:F# Minor;75.4187:F# Major;76.5333:G Diminished;76.9048:G Major;79.041:E Minor;80.62:C# Diminished;80.8987:A Major;82.1061:D Major;83.9637:F# Major;84.4281:F# Major;84.7996:F# Minor;85.3569:G Major7;85.8213:G Major;86.843:A Dominant7;87.586:E Major;88.7935:E Minor;89.908:G Major;92.0443:A Major;96.8741:B Minor7;96.9669:B Minor;98.3601:G Major;100.589:A Major;105.512:B Minor;106.812:G Major;109.134:C# Minor;109.227:A Major;112.664:G Diminished;113.128:C# Major;113.778:G Minor;113.964:G Major;114.521:G Major;115.079:G Major;115.636:G Diminished;115.822:C# Diminished;116.007:C# Minor;116.379:C# Diminished;118.608:G Major;121.209:A# Major;123.159:G Major;125.481:G Augmented;126.317:B Minor7;126.503:G Major;127.339:A Major;128.36:D Major;132.633:G Major;133.655:C# Major;133.933:F# Minor;134.862:E Minor;136.627:A Major;139.32:F# Major;141.178:G Major;142.571:F# Minor;143.5:G Major7;144.707:E Minor;145.636:G Major;147.865:A Major;149.909:D Major;151.023:A Major;152.138:F# Minor;154.088:E Minor;154.46:G Augmented;154.646:G Major;156.782:A Major;158.732:D Major;159.754:A Major;161.519:B Major;162.262:B Minor;165.791:G Major;168.113:A Major;170.249:D Major");
		wishSong = BackendRequestThread.parseSong("102;307;4;4;58.143:F# Minor;59.6291:E Dominant7;61.208:B Minor;61.4867:G Major;61.7653:D Major;66.1307:D Major;66.4093:A Major;66.8737:E Minor7;67.6168:E Major;69.2886:D# Diminished;69.753:G Major;74.3042:A Major;75.233:E Major;76.5333:F Major7;77.7407:B Diminished7;77.8336:A Major;78.7624:B Dominant7;80.2485:A Major;82.199:A Major;84.1495:E Major;85.1711:E Major;85.7284:A Major;87.2145:D Minor;89.4436:D Major;89.5365:G Major;94.2734:C Major;98.3601:D Major;103.469:A Minor;105.419:E Major;106.348:G Major;110.899:D Major;115.729:C Major;116.379:E Minor;117.215:C Major;118.051:D Major;118.422:C Major;119.444:A Minor;123.531:G Major;127.803:C Major;131.611:D Major;135.605:A Minor;138.02:A Major;138.577:A Minor;139.785:G Major;143.593:D Major;147.68:C Major;151.952:A Minor;156.039:G Major;160.126:E Minor;161.147:G Major;167.927:E Minor;168.67:G Major;175.637:E Minor;176.844:G Major;177.68:E Minor;179.537:A Major;183.624:E Minor;184.553:G Major;185.389:E Minor;187.339:A Major;190.683:D Major;191.333:G Major;195.42:C Major;197.928:C Major;199.321:D Major;203.222:A Minor;204.522:A Major;205.08:A Minor;207.402:G Major;211.117:D Major;215.111:C Major;219.104:A Minor;223.005:G Major;227.185:E Minor;230.9:G Major;234.894:E Minor;237.588:G Major;242.789:E Minor;243.811:G Major;244.461:E Minor;246.411:A Major;249.662:D Major;250.498:E Minor;251.334:G Major;252.17:E Minor;254.12:A Major;257.835:G Major;265.914:E Minor;268.422:G Major;272.786:D Major;273.437:E Minor;275.758:G Major;276.594:E Minor;277.151:G Major;281.051:E Minor;281.887:G Major;282.816:E Minor;284.673:A Major;288.759:E Minor;289.409:G Major;290.431:E Minor;291.545:E Minor7;292.381:A Major;296.188:G Major;306.682:G Major;308.075:G Major;311.14:G Major;311.604:G Major;311.883:E Minor;312.44:G Major;313.183:G Major;313.461:G Major;313.74:E Minor;314.204:G Major;314.576:E Minor;315.412:G Major;315.783:G Major;316.433:G Major;319.033:B Diminished;320.519:G Major;321.541:C# Diminished;324.048:A Major;324.512:A Major;324.698:A Major;325.255:F# Major;325.998:E Diminished;326.741:E Diminished;");
		karmaSong = BackendRequestThread.parseSong("69;400;4;4;0:A Major;0.464399:A Minor;1.48608:F# Minor;2.97215:E Minor;4.82975:G Major;6.50159:A Minor;8.17342:F Major;9.65951:E Minor;11.3314:G Major;12.9103:A Minor7;13.4676:A Minor;14.5822:D Major;16.254:G Major;17.0899:D Major;18.1116:E Minor;18.8547:G Major;19.5048:A Minor;23.1271:B Minor;24.3346:D Major;26.6566:A Minor;29.0715:B Minor7;29.5359:E Minor;31.1148:G Major;32.8796:A Minor;34.2728:F Major;35.8517:E Minor;37.988:G Major;39.3812:A Minor;41.3317:D Major;42.1676:G Major;43.8394:C Major;46.0686:A Minor;47.6475:A Major;49.4123:B Minor;50.5268:D Major;52.1058:A Major;52.6631:A Minor;54.0563:F# Diminished;55.2637:G Major;55.3566:E Minor;56.9356:G Major;58.886:A Minor;61.5796:E Minor;63.7158:G Major;65.2019:A Minor;66.3165:D Major;67.8954:G Major;69.6601:C Major;71.9821:A Minor;72.9109:A Major;73.9326:A Minor;75.0472:B Minor;76.2546:D Major;77.9265:C Major;80.1556:D Major;80.8987:G Major;82.7563:G Diminished;83.9637:C Major;85.1711:G Major;86.2857:D Major;87.0287:G Major;88.8864:C# Diminished;89.4436:G Diminished;90.3724:C Major;92.6016:D Major;93.4375:G Major;95.388:B Minor;96.7812:C Major;100.589:B Minor;101.611:D Major;103.469:A Minor;104.583:F# Minor;105.326:F# Diminished;105.883:F# Minor;106.162:E Minor;107.741:G Augmented;108.391:G Major;108.948:C Major;109.692:A Minor;111.178:F Major;112.571:E Minor;114.243:G Major;116.007:A Minor;118.144:D Major;119.258:G Major;120.28:C Major;121.394:B Minor;122.137:A Minor;124.552:A Major;125.946:B Minor;126.689:B Minor7;128.732:C Major;130.682:D Major;131.518:G Major;133.19:G Diminished;134.769:C Major;137.277:D Major;138.02:G Major;140.249:G Diminished;141.271:C Major;143.407:D Major;144.429:G Major;146.565:B Minor;147.68:C Major;148.887:E Minor;150.094:C Major;151.581:G Major;151.673:E Minor;153.252:A Major;153.717:B Minor7;154.181:B Minor;155.946:D Major;157.06:G Major;159.011:D Major;160.033:G Major;161.704:D Major;163.841:E Major;165.791:E Major;166.441:E Major;166.999:B Minor;168.763:D Major;169.785:G Major;171.178:D Major;173.036:G Major;174.336:D Major;176.472:B Minor7;176.844:E Major;179.816:F# Minor;181.488:D Major;182.974:G Major;183.996:D Major;185.76:G Major;187.247:D Major;189.29:E Major;191.891:G# Diminished7;192.726:B Minor;193.748:D Major;194.77:D Minor;195.327:G Major;196.535:D Major;198.764:G Major;199.6:D Major;200.436:D Major;202.479:E Major;205.358:B Minor;206.473:D Major;207.959:G Major;209.445:D Major;210.281:B Minor7;210.838:D Major;211.674:G Major;212.603:D Major;215.203:E Major;220.126:D Major;228.207:E Major;228.95:E Major;229.136:B Minor;229.693:D Major;230.714:F# Minor;231.272:D Major;241.303:E Dominant7;242.325:E Dominant7;242.603:A Major;243.068:B Minor;245.111:B Minor;245.575:G# Diminished;246.783:B Minor;249.291:C# Minor7;250.312:A Major;254.027:F# Major;258.95:B Minor;259.228:F Diminished;259.693:G Diminished;");
		letSong = BackendRequestThread.parseSong("129;411;4;4;0:B Major;0.0928798:E Major;6.03719:B Minor;6.50159:G Diminished;6.68734:F# Major;6.78022:F# Minor;12.1673:E Major;18.6689:F# Major;18.8547:F# Minor;20.8051:F# Major;21.8268:F# Minor;23.0343:F# Minor;23.4058:B Major;24.1488:F# Minor7;24.3346:E Major;31.5792:F# Major;33.9012:F# Minor;37.1521:C# Minor7;37.6165:B Major;37.8022:E Major7;38.1737:E Major;45.9757:F# Minor;51.3627:E Major;57.4:G Major;57.6786:F# Major;58.0501:F# Minor;58.6074:F# Major;62.5084:C# Minor;62.6941:B Major;63.7158:E Major;70.1245:F# Dominant7;70.3103:F# Minor;75.4187:B Major7;75.6974:B Major;76.1618:E Major;84.2423:E Major7;86.0071:E Major;88.7935:B Major;90.6511:D# Diminished;91.3012:F# Minor;92.3229:E Major;93.6232:D# Diminished;95.2022:B Major;97.3385:F# Minor;98.6388:E Major;99.7533:D# Diminished;101.332:B Major;101.704:E Major;103.19:A Minor;104.49:E Major;106.812:E Major;107.648:F# Major;107.927:F# Minor;112.849:B Major;113.128:C# Diminished7;113.964:E Major;120.001:E Minor;120.559:F# Major;122.045:F# Minor;125.388:F# Minor;125.76:C# Diminished;126.689:E Major;129.754:E Major;134.026:F# Major;137.741:F# Minor;139.692:F# Minor;140.156:E Major;151.395:F# Minor;151.581:F# Minor;151.766:F# Minor7;151.952:F# Minor;154.738:E Major;159.382:G# Minor;160.961:F# Major;163.748:F# Minor;165.884:B Major;166.256:E Major;166.627:E Minor;167.092:D# Diminished;167.184:E Major;173.686:F# Minor;174.243:F# Major;175.172:F# Minor;178.609:F# Minor;179.166:E Major;179.352:E Major;179.537:B Dominant7;180.002:E Major;186.318:F# Minor;187.99:F# Major;190.962:B Minor;191.705:F# Minor;192.262:E Major;194.027:C# Minor7;195.141:E Major;199.414:F# Major;200.25:F# Minor;201.457:F# Major;202.758:C# Minor;205.172:B Major;205.265:E Major;208.237:E Major;");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Intent intent = getIntent();
		String songStr = intent.getExtras().getString("song");
		Song s = null;
		if (songStr.equals(ChooseLearnActivity.ARCTIC_MONKEYS)) {
			s = mardySong;
		} else if (songStr.equals(ChooseLearnActivity.PINK_FLOYD)) {
			s = wishSong;
		} else if (songStr.equals(ChooseLearnActivity.KARMA_POLICE)) {
			s = karmaSong;
		}
		else {
			s = letSong;
		}
		AutoHorizontalScrollView autoScrollView = new AutoHorizontalScrollView(s, this);
		autoScrollView.songName = songStr;
		setContentView(autoScrollView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.learn, menu);
		return true;
	}

}
