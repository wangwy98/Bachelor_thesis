# Bachelor_thesis
BA repository containes two folders FilopodyanAndConfig and ComparisionAndResuits.

In the FilopodyanAndConfig, folder Filopodyan is the modified version of my bachelor thesis. 
The filopodyan-config.csv is the corresponding configuration file, where all the parameters of Filopodyan are included.

A jar file (Filopodyan-1.3.1.jar) can be downloaded in release.

First fill the filopodyan-config.csv, then Filopodyan can called using:

java -jar /path/Filopodyan-1.3.1.jar -d -f:/path/images_folder -c:/path/filopodyan-config.csv

  -s: Execute and display data in a pop-up window
  
  -d: Exit when execution is complete
  
  -f: Path of folder contains the images for detection
  
  -c: Path of the configuration file
  
E.g.
java -jar /Users/name/Desktop/Filopodyan-1.3.1.jar -d -f:/Users/name/Desktop/R2_27hAPF_tif -c:/Users/name/Desktop/R2_27hAPF_filopodyan-config.csv

After running Filopodyan, the results tables can be used in the python file testParameter.ipynb in the folder ComparisionAndResuits,
to calculate the distances between manually and automatically detected filopodia tips.

The results are represented as plots. (see R1-R6.jpg)

