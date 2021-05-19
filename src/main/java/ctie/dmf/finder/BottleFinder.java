package ctie.dmf.finder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.transaction.Transactional;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.Size;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;

import ctie.dmf.customType.KeyPointVectorType;
import ctie.dmf.customType.MatType;
import ctie.dmf.entity.Bottle;
import ctie.dmf.entity.Image;
import nu.pattern.OpenCV;


public class BottleFinder {

	private List<Image> images;
	private ORB orb;
    private DescriptorMatcher matcher;
	private String cwd = System.getProperty("user.dir")+ "\\..\\";
	
	private final int NFEATURE = 3000;
	private final int WIDTH = 512;
	private final int HEIGHT = 768;
	private final float RATIO_TRESHOLD = 0.75f;
	private final int IDENTIFICATION_TRESHOLD = 12;
	
	
	//Initialisation of recognition
	public BottleFinder() {
		OpenCV.loadLocally();
		this.images = Image.listAll();
		this.orb = ORB.create(NFEATURE);
		initialise_images();
		matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMINGLUT);

	}
	
	//Load image file
	private Mat loadImage(String path) {
		String cwd = System.getProperty("user.dir")+ "\\..\\";
		return Imgcodecs.imread(cwd + path);
	}
	
	//Resize image to (INPUT_SIZE,INPUT_SIZE)
	private Mat resizeImage(Mat src) {
		Mat resizeimage = new Mat();
		Size scaleSize = new Size(WIDTH,HEIGHT);
		Imgproc.resize(src, resizeimage, scaleSize , 0, 0, Imgproc.INTER_AREA);
		return resizeimage;
	}
	
	//Compute descriptor of all images in data base
	private void initialise_images() {
		for(Image i : images) {
			//Initialize
			KeyPointVectorType kp = new KeyPointVectorType();
			MatType desc = new MatType();
			
			//Read and resize image
			Mat src  =  loadImage(i.getPath());
			//Mat resizeimage = resizeImage(src);
			
			//Compute kp and desc
			orb.detectAndCompute(src,new Mat(), kp, desc);
			i.setKeypoints(kp);
			i.setDescriptors(desc);
			
		}
	}
	
	private int compare(Mat desc0, Mat desc1) {
		List<MatOfDMatch> knnMatches = new ArrayList<>();
		
		//Compute corresponding matches
    	matcher.knnMatch(desc0, desc1,knnMatches,2);
    	//-- Filter matches using the Lowe's ratio test
        List<DMatch> listOfGoodMatches = new ArrayList<>();
        for (int j = 0; j < knnMatches.size(); j++) {
            if (knnMatches.get(j).rows() > 1) {
                DMatch[] matches = knnMatches.get(j).toArray();
                if (matches[0].distance < RATIO_TRESHOLD * matches[1].distance) {
                    listOfGoodMatches.add(matches[0]);
                }
            }
        }
		return listOfGoodMatches.size();
	}
	

	//Image identification
	@Transactional
	public Bottle identify(Image imgsrc) {
		//-- Step 1: Detect the keypoints using ORB Detector, compute the descriptors
		
		//Initialize
		KeyPointVectorType kp = new KeyPointVectorType();
		Mat desc = new Mat();
		
		//Read and resize image
		Mat src  =  loadImage(imgsrc.getPath());
		//Mat resizeimage = resizeImage(src);
		
		//Compute kp and desc
		orb.detectAndCompute(src, new Mat(), kp, desc);
		
        //Compute corresponding matches with all images in data base
		AtomicInteger maxMatches = new AtomicInteger(0);
		AtomicReference<Bottle> bottlefound = new AtomicReference<>(); //Bottle identified
        
        images.parallelStream()
        		.forEach(i -> {
        			int n = compare(desc,(Mat) i.getDescriptors());
        	        System.out.println(n + " : " + i.getBottle().getName() );
        			if(n > maxMatches.get() && n > IDENTIFICATION_TRESHOLD) {
        				maxMatches.set(n);
        				bottlefound.set(i.getBottle());
        			}
        		});
        //System.out.println(bottlefound.get().toString());
        	
		return bottlefound.get();
	}
}
