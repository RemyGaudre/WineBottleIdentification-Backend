package ctie.dmf.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Size;
import org.opencv.features2d.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;
import org.opencv.core.CvType;

import ctie.dmf.customType.KeyPointVectorType;
import ctie.dmf.customType.MatType;
import ctie.dmf.entity.Bottle;
import ctie.dmf.entity.Image;
import nu.pattern.OpenCV;


public class BottleFinder {

	List<Image> images;
	ORB orb;
    DescriptorMatcher matcher;
	
	private final int NFEATURE = 100;
	private final int INPUT_SIZE = 416;
	private final float RATIO_TRESHOLD = 0.75f;
	
	public BottleFinder() {
		OpenCV.loadLocally();
		this.images = Image.listAll();
		this.orb = ORB.create(NFEATURE);
		initialise_images();
		matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);

	}
	
	private Mat loadImage(String path) {
		Imgcodecs cdc = new Imgcodecs();
		return cdc.imread(path);
	}
	
	private Mat resizeImage(Mat src) {
		Mat resizeimage = new Mat();
		Size scaleSize = new Size(INPUT_SIZE,INPUT_SIZE);
		Imgproc.resize(src, resizeimage, scaleSize , 0, 0, Imgproc.INTER_AREA);
		return resizeimage;
	}
	
	private void initialise_images() {
		for(Image i : images) {
			//Initialize
			KeyPointVectorType kp = new KeyPointVectorType();
			MatType desc = new MatType();
			
			//Read and resize image
			Mat src  =  loadImage(i.getPath());
			Mat resizeimage = resizeImage(src);
			
			//Compute kp and desc
			orb.detectAndCompute(resizeimage,new Mat(), kp, desc);
			i.setKeypoints(kp);
			i.setDescriptors(desc);
		}
	}
	

	@Transactional
	public Bottle identify(Image imgsrc) {
		//-- Step 1: Detect the keypoints using ORB Detector, compute the descriptors
		
		//Initialize
		KeyPointVectorType kp = new KeyPointVectorType();
		Mat desc = new Mat();
		
		//Read and resize image
		Mat src  =  loadImage(imgsrc.getPath());
		Mat resizeimage = resizeImage(src);
		
		//Compute kp and desc
		orb.detectAndCompute(resizeimage,new Mat(), kp, desc);
		
        //List<Integer> listOfNumberOfMatches = new ArrayList<Integer>();
        int maxMatches = 0;
        Bottle bottlefinded = new Bottle();
        for(Image i: images) {
        	Mat iDesc = i.getDescriptors();
        	List<MatOfDMatch> knnMatches = new ArrayList<>();
        	matcher.knnMatch(desc, iDesc,knnMatches,2);
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
            if(listOfGoodMatches.size() > maxMatches) {
            	maxMatches = listOfGoodMatches.size();
            	bottlefinded = i.getBottle();
            }
        }		
		
		return bottlefinded;
	}
}
